/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.launchConfigurations;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;

/**
 * creates a filter for the current working sets in use on the workbench to be applied in the launch configuration
 * dialog and the launch history/last launched
 * 
 * @since 3.2
 */
public class WorkingSetsFilter extends ViewerFilter {
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof ILaunchConfigurationType) {
			return true;
		}
		if(element instanceof ILaunchConfiguration) {
			ILaunchConfiguration config = (ILaunchConfiguration)element;
			try {
				IResource[] resources = config.getMappedResources();
				if(resources == null) {
					return true;
				}
				IWorkbenchWindow window = DebugUIPlugin.getActiveWorkbenchWindow();
				if(window == null) {
					return true;
				}
				IWorkbenchPage page = window.getActivePage();
				if(page == null) {
					return true;
				}
				IWorkingSet[] wsets = page.getWorkingSets();
				if(wsets.length < 1) {
					return true;
				}
				//remove breakpoint working sets
				ArrayList<IWorkingSet> ws = new ArrayList<IWorkingSet>();
				for (int i = 0; i < wsets.length; i++) {
					if(!IDebugUIConstants.BREAKPOINT_WORKINGSET_ID.equals(wsets[i].getId())) {
						ws.add(wsets[i]);
					}
				}
				if(ws.isEmpty()) {
					return true;
				}
				for (int i = 0; i < resources.length; i++) {
					if(workingSetContains(ws.toArray(new IWorkingSet[ws.size()]), resources[i])) {
						return true;
					}
				}
			} 
			catch (CoreException e) {}
		}
		return false;
	}

	/**
	 * Determines if the specified group of working sets contains the specified resource.
	 * @param wsets the set of working sets to examine
	 * @param res the resource to check for containment
	 * @return true iff any one of the specified working sets contains the specified resource
	 * @since 3.2
	 */
	public static boolean workingSetContains(IWorkingSet[] wsets, IResource res) {
		ArrayList<IResource> parents = new ArrayList<IResource>();
		parents.add(res);
		while(res != null) {
			res = res.getParent();
			if(res != null) {
				parents.add(res);
			}
		}
		IResource lres = null;
		for(int i = 0; i < wsets.length; i++) {	
			IAdaptable[] elements = wsets[i].getElements();
			for(int j = 0; j < elements.length; j++) {
				lres = elements[j].getAdapter(IResource.class);
				if(lres != null) {
					if(parents.contains(lres)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
