/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.AbstractDebugCheckboxSelectionDialog;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchViewerComparator;

import com.ibm.icu.text.MessageFormat;

/**
 * This dialog is used to select one or more launch configurations to add to your favorites
 * 
 * @since 3.3.0
 */
public class SelectFavoritesDialog extends AbstractDebugCheckboxSelectionDialog {

	/**
	 * Content provider for table
	 */	
	protected class LaunchConfigurationContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(Object inputElement) {
			ILaunchConfiguration[] all = null;
			try {
				all = LaunchConfigurationManager.filterConfigs(DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations());
			} catch (CoreException e) {
				DebugUIPlugin.log(e);
				return new ILaunchConfiguration[0];
			}
			List<ILaunchConfiguration> list = new ArrayList<ILaunchConfiguration>(all.length);
			ViewerFilter filter = new LaunchGroupFilter(fHistory.getLaunchGroup());
			for (int i = 0; i < all.length; i++) {
				if (filter.select(null, null, all[i])) {
					list.add(all[i]);
				}
			}
			list.removeAll(fCurrentFavoriteSet);
			Object[] objs = list.toArray();
			new WorkbenchViewerComparator().sort(getCheckBoxTableViewer(), objs);
			return objs;
		}

		@Override
		public void dispose() {}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	}	
	
	private LaunchHistory fHistory;
	private List<ILaunchConfiguration> fCurrentFavoriteSet;
	
	/**
	 * Constructor
	 * @param parentShell
	 * @param history
	 * @param favorites
	 */
	public SelectFavoritesDialog(Shell parentShell, LaunchHistory history, List<ILaunchConfiguration> favorites) {
		super(parentShell);
		fHistory = history;
		fCurrentFavoriteSet = favorites;
		setTitle(MessageFormat.format(LaunchConfigurationsMessages.FavoritesDialog_0, new Object[] { getModeLabel() }));
		setShowSelectAllButtons(true);
	}

	/**
	 * Returns a label to use for launch mode with accelerators removed.
	 * 
     * @return label to use for launch mode with accelerators removed
     */
    private String getModeLabel() {
        return DebugUIPlugin.removeAccelerators(fHistory.getLaunchGroup().getLabel());
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.launchConfigurations.AbstractDebugSelectionDialog#getDialogSettingsId()
	 */
	@Override
	protected String getDialogSettingsId() {
		return IDebugUIConstants.PLUGIN_ID + ".SELECT_FAVORITESS_DIALOG"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.launchConfigurations.AbstractDebugSelectionDialog#getViewerInput()
	 */
	@Override
	protected Object getViewerInput() {
		return fHistory.getLaunchGroup().getMode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.launchConfigurations.AbstractDebugSelectionDialog#getContentProvider()
	 */
	@Override
	protected IContentProvider getContentProvider() {
		return new LaunchConfigurationContentProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.launchConfigurations.AbstractDebugSelectionDialog#getHelpContextId()
	 */
	@Override
	protected String getHelpContextId() {
		return IDebugHelpContextIds.SELECT_FAVORITES_DIALOG;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.launchConfigurations.AbstractDebugSelectionDialog#getViewerLabel()
	 */
	@Override
	protected String getViewerLabel() {
		return LaunchConfigurationsMessages.FavoritesDialog_7;
	}
	
}
