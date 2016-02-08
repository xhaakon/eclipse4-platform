/*******************************************************************************
 *  Copyright (c) 2005, 2013 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Wind River - Pawel Piech - added an evaluation context source provider (bug 229219)
 *     Patrick Chuong (Texas Instruments) and Pawel Piech (Wind River) - 
 *     		Allow multiple debug views and multiple debug context providers (Bug 327263)
 *******************************************************************************/
package org.eclipse.debug.internal.ui.contexts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.contexts.DebugContextEvent;
import org.eclipse.debug.ui.contexts.IDebugContextListener;
import org.eclipse.debug.ui.contexts.IDebugContextProvider;
import org.eclipse.debug.ui.contexts.IDebugContextProvider2;
import org.eclipse.debug.ui.contexts.IDebugContextService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.IEvaluationService;

/**
 * Context service for a specific window.
 * 
 * @since 3.2
 */
public class DebugWindowContextService implements IDebugContextService, IPartListener2, IDebugContextListener {
	
	private Map<String, ListenerList> fListenersByPartId = new HashMap<String, ListenerList>();
	private Map<String, IDebugContextProvider> fProvidersByPartId = new HashMap<String, IDebugContextProvider>();
	private Map<String, ListenerList> fPostListenersByPartId = new HashMap<String, ListenerList>();
	
	private IWorkbenchWindow fWindow;
	private List<IDebugContextProvider> fProviders = new ArrayList<IDebugContextProvider>();
	
	private DebugContextSourceProvider fSourceProvider;

	public DebugWindowContextService(IWorkbenchWindow window, IEvaluationService evaluationService) {
		fWindow = window;
		fWindow.getPartService().addPartListener(this);
		
		fSourceProvider = new DebugContextSourceProvider(this, evaluationService);
	}
	
	public void dispose() {
		fSourceProvider.dispose();
		fWindow.getPartService().removePartListener(this);
		fWindow = null;
	}
	
	@Override
	public synchronized void addDebugContextProvider(IDebugContextProvider provider) {
	    if (fWindow == null)
		 {
			return; // disposed
		}
	    
		IWorkbenchPart part = provider.getPart();
		fProvidersByPartId.put( getCombinedPartId(part), provider );

		// Check if provider is a window context provider
		boolean canSetActive = true;
        if (provider instanceof IDebugContextProvider2) {
            canSetActive = ((IDebugContextProvider2) provider).isWindowContextProvider();
        }
        // Make the provider active if matches the active part. Otherwise, it 
        // may still become the active provider if fProviders.isEmpty(). 
		if (canSetActive) {
	        IWorkbenchPart activePart = null;
	        IWorkbenchPage activePage = fWindow.getActivePage();
	        if (activePage != null) {
	            activePart = activePage.getActivePart();
	        }        
	        canSetActive = (activePart == null && part == null) || (activePart != null && activePart.equals(part));
		}
		
		if (canSetActive) {
		    fProviders.add(0, provider);
		} else {
		    fProviders.add(provider);
		}
        notify(provider);
		provider.addDebugContextListener(this);
	}
	
	@Override
	public synchronized void removeDebugContextProvider(IDebugContextProvider provider) {
		int index = fProviders.indexOf(provider);
		if (index >= 0) {
			fProvidersByPartId.remove( getCombinedPartId(provider.getPart()) );
			fProviders.remove(index);
			IDebugContextProvider activeProvider = getActiveProvider();
			if (index == 0) {
    			if (activeProvider != null) {
    				notify(activeProvider);
    			} else {
    			    // Removed last provider.  Send empty selection to all listeners.
    				notify(new DebugContextEvent(provider, StructuredSelection.EMPTY, DebugContextEvent.ACTIVATED));
    			}
			} else {
			    // Notify listeners of the removed provider with the active window context.
			    notifyPart(provider.getPart(), 
			        new DebugContextEvent(activeProvider, getActiveContext(), DebugContextEvent.ACTIVATED));
			}
		}
		provider.removeDebugContextListener(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService#addDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener)
	 */
	@Override
	public void addDebugContextListener(IDebugContextListener listener) {
		addDebugContextListener(listener, null);
	}
	
	@Override
	public void addPostDebugContextListener(IDebugContextListener listener, String partId) {
		ListenerList list = fPostListenersByPartId.get(partId);
		if (list == null) {
			list = new ListenerList();
			fPostListenersByPartId.put(partId, list);
		}
		list.add(listener);	
	}

	@Override
	public void addPostDebugContextListener(IDebugContextListener listener) {
		addPostDebugContextListener(listener, null);
	}
	
	@Override
	public void removePostDebugContextListener(IDebugContextListener listener, String partId) {
		ListenerList list = fPostListenersByPartId.get(partId);
		if (list != null) {
			list.remove(listener);
		}
	}

	@Override
	public void removePostDebugContextListener(IDebugContextListener listener) {
		removePostDebugContextListener(listener, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService#removeDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener)
	 */
	@Override
	public void removeDebugContextListener(IDebugContextListener listener) {
		removeDebugContextListener(listener, null);
	}
	
	/**
	 * Notifies listeners of the context in the specified provider.
	 * 
	 * @param provdier context provider
	 */
	protected void notify(IDebugContextProvider provdier) {
		ISelection activeContext = provdier.getActiveContext();
		if (activeContext == null) {
			activeContext = new StructuredSelection();
		}
		notify(new DebugContextEvent(provdier, activeContext, DebugContextEvent.ACTIVATED));
	}
	
	protected void notify(DebugContextEvent event) {
	    // Allow handling for case where getActiveProvider() == null.
	    // This can happen upon removeContextProvider() called on last available 
	    // provider (bug 360637).
		IDebugContextProvider provider = getActiveProvider();
		IWorkbenchPart part = event.getDebugContextProvider().getPart();
		
		// Once for listeners
		if (provider == null || provider == event.getDebugContextProvider()) {		
			notify(event, getListeners(null));
		}		
		if (part != null) {
			notify(event, getListeners(part));
		}
		
		// Again for post-listeners
		if (provider == null || provider == event.getDebugContextProvider()) {
			notify(event, getPostListeners(null));
		}
		if (part != null) {
			notify(event, getPostListeners(part));
		}
	}

	protected void notifyPart(IWorkbenchPart part, DebugContextEvent event) {
        if (part != null) {
            notify(event, getListeners(part));
            notify(event, getPostListeners(part));
        }
    }

	protected void notify(final DebugContextEvent event, Object[] listeners) {
		for (int i = 0; i < listeners.length; i++) {
			final IDebugContextListener listener = (IDebugContextListener) listeners[i];
			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void run() throws Exception {
					listener.debugContextChanged(event);
				}
				@Override
				public void handleException(Throwable exception) {
					DebugUIPlugin.log(exception);
				}
			});
		}
	}
	
	protected Object[] getListeners(IWorkbenchPart part) {
        String id = null; 
        if (part != null) { 
            id = getCombinedPartId(part); 
            ListenerList listenerList = fListenersByPartId.get(id); 
            return listenerList != null ? listenerList.getListeners() : new Object[0]; 
        } else { 
			List<Object> retVal = new ArrayList<Object>();
            retVal.addAll(Arrays.asList(fListenersByPartId.get(null).getListeners()) ); 
			outer: for (Iterator<String> itr = fListenersByPartId.keySet().iterator(); itr.hasNext();) {
                String listenerPartId = itr.next(); 
                for (int i = 0; i < fProviders.size(); i++) { 
                    String providerPartId = getCombinedPartId(fProviders.get(i).getPart());
                    if ((listenerPartId == null && providerPartId == null) || 
                        (listenerPartId != null && listenerPartId.equals(providerPartId)))  
                    { 
                        continue outer; 
                    } 
                }
                
				List<Object> toAdd = Arrays.asList(fListenersByPartId.get(listenerPartId).getListeners());
				for (Iterator<Object> addItr = toAdd.iterator(); addItr.hasNext();) {
                	Object element = addItr.next();
                	if (!retVal.contains(element)) {
						retVal.add(element);
					}
                	
                } 
            } 
            return retVal.toArray(); 
        } 
	}
	
	protected Object[] getPostListeners(IWorkbenchPart part) {
		String id = null; 
        if (part != null) { 
            id = getCombinedPartId(part); 
            ListenerList listenerList = fPostListenersByPartId.get(id); 
            return listenerList != null ? listenerList.getListeners() : new Object[0]; 
        } else { 
			List<Object> retVal = new ArrayList<Object>();
            ListenerList postListenersList = fPostListenersByPartId.get(null); 
            if (postListenersList != null) { 
                retVal.addAll( Arrays.asList(postListenersList.getListeners()) ); 
            } 
            
			outer: for (Iterator<String> itr = fPostListenersByPartId.keySet().iterator(); itr.hasNext();) {
                String listenerPartId = itr.next(); 
                for (int i = 0; i < fProviders.size(); i++) { 
                    String providerPartId = getCombinedPartId(fProviders.get(i).getPart());
                    if ((listenerPartId == null && providerPartId == null) || 
                        (listenerPartId != null && listenerPartId.equals(providerPartId)))  
                    { 
                        continue outer; 
                    } 
                } 
                retVal.addAll( Arrays.asList(fPostListenersByPartId.get(listenerPartId).getListeners()) ); 
            } 
            return retVal.toArray(); 
        } 
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService#addDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener, java.lang.String)
	 */
	@Override
	public synchronized void addDebugContextListener(IDebugContextListener listener, String partId) {
		ListenerList list = fListenersByPartId.get(partId);
		if (list == null) {
			list = new ListenerList();
			fListenersByPartId.put(partId, list);
		}
		list.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService#removeDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener, java.lang.String)
	 */
	@Override
	public void removeDebugContextListener(IDebugContextListener listener, String partId) {
		ListenerList list = fListenersByPartId.get(partId);
		if (list != null) {
			list.remove(listener);
			if (list.size() == 0) {
				fListenersByPartId.remove(partId);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService#getActiveContext(java.lang.String)
	 */
	@Override
	public ISelection getActiveContext(String partId) {
		IDebugContextProvider provider = fProvidersByPartId.get(partId);
		if (provider != null) {
			return provider.getActiveContext();
		}
		return getActiveContext();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService#getActiveContext()
	 */
	@Override
	public ISelection getActiveContext() {
		IDebugContextProvider activeProvider = getActiveProvider();
		if (activeProvider != null) {
			return activeProvider.getActiveContext();
		}
		return null;
	}
	
	/**
	 * Returns the active provider or <code>null</code>
	 * 
	 * @return active provider or <code>null</code>
	 */
	private IDebugContextProvider getActiveProvider() {
		if (!fProviders.isEmpty()) {
			return fProviders.get(0);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IDebugContextProvider provider = fProvidersByPartId.get(partRef.getId());
		if (provider != null) {
			boolean canSetActive = true;
			if (provider instanceof IDebugContextProvider2) {
				canSetActive = ((IDebugContextProvider2) provider).isWindowContextProvider();
			}
			
			if (canSetActive) {
				int index = fProviders.indexOf(provider);
				if (index > 0) {
					fProviders.remove(index);
					fProviders.add(0, provider);
					notify(provider);
				}
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public synchronized void partClosed(IWorkbenchPartReference partRef) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.contexts.provisional.IDebugContextEventListener#contextEvent(org.eclipse.debug.internal.ui.contexts.provisional.DebugContextEvent)
	 */
	@Override
	public void debugContextChanged(DebugContextEvent event) {	
		notify(event);
	}
	
	private String getCombinedPartId(IWorkbenchPart part) {
	    if (part == null) {
	        return null;
	    } else if (part.getSite() instanceof IViewSite) { 
            IViewSite site = (IViewSite)part.getSite();
            return getCombinedPartId(site.getId(), site.getSecondaryId());
            
        } else { 
            return part.getSite().getId(); 
        } 
    }	

	private String getCombinedPartId(String id, String secondaryId) {
		return id + (secondaryId != null ? ":" + secondaryId : "");   //$NON-NLS-1$//$NON-NLS-2$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService2#addDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener, java.lang.String, java.lang.String)
	 */
	@Override
	public void addDebugContextListener(IDebugContextListener listener, String partId, String partSecondaryId) {
		addDebugContextListener(listener, getCombinedPartId(partId, partSecondaryId));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService2#removeDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener, java.lang.String, java.lang.String)
	 */
	@Override
	public void removeDebugContextListener(IDebugContextListener listener, String partId, String partSecondaryId) {
		removeDebugContextListener(listener, getCombinedPartId(partId, partSecondaryId));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService2#addPostDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener, java.lang.String, java.lang.String)
	 */
	@Override
	public void addPostDebugContextListener(IDebugContextListener listener, String partId, String partSecondaryId) {
		addPostDebugContextListener(listener, getCombinedPartId(partId, partSecondaryId));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService2#removePostDebugContextListener(org.eclipse.debug.ui.contexts.IDebugContextListener, java.lang.String, java.lang.String)
	 */
	@Override
	public void removePostDebugContextListener(IDebugContextListener listener, String partId, String partSecondaryId) {
		removePostDebugContextListener(listener, getCombinedPartId(partId, partSecondaryId));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.contexts.IDebugContextService2#getActiveContext(java.lang.String, java.lang.String)
	 */
	@Override
	public ISelection getActiveContext(String partId, String partSecondaryId) {		
		return getActiveContext(getCombinedPartId(partId, partSecondaryId));
	} 
}
