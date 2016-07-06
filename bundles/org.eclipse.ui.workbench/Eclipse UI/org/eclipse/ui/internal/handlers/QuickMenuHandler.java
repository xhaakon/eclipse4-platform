/*******************************************************************************
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@gmail.com> - Bug 440810
 ******************************************************************************/

package org.eclipse.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ContributionManager;
import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.QuickMenuCreator;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.progress.UIJob;

/**
 * Support for a command based QuickMenuAction that can pop up a
 * menu-contribution based context menu.
 * <p>
 * This is experimental and should not be moved.
 * </p>
 *
 * @since 3.4
 */
public class QuickMenuHandler extends AbstractHandler implements IMenuListener2 {
	private QuickMenuCreator creator = new QuickMenuCreator() {

		@Override
		protected void fillMenu(IMenuManager menu) {
			if (!(menu instanceof ContributionManager)) {
				return;
			}
			IMenuService service = PlatformUI.getWorkbench()
					.getService(IMenuService.class);
			service.populateContributionManager((ContributionManager) menu,
					locationURI);
			menu.addMenuListener(QuickMenuHandler.this);
		}

	};

	private String locationURI;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		locationURI = event.getParameter("org.eclipse.ui.window.quickMenu.uri"); //$NON-NLS-1$
		if (locationURI == null) {
			throw new ExecutionException("locatorURI must not be null"); //$NON-NLS-1$
		}
		creator.createMenu();
		return null;
	}

	@Override
	public void dispose() {
		if (creator != null) {
			creator.dispose();
			creator = null;
		}
	}

	@Override
	public void menuAboutToHide(final IMenuManager managerM) {
		new UIJob("quickMenuCleanup") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IMenuService service = PlatformUI.getWorkbench()
						.getService(IMenuService.class);
				service.releaseContributions((ContributionManager) managerM);
				return Status.OK_STATUS;
			}

		}.schedule();
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {
		// no-op
	}
}
