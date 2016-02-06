/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.e4.compatibility.E4Util;
import org.eclipse.ui.internal.util.Util;

/**
 * A dynamic contribution item which supports to switch to other Contexts.
 */
public class ShowFastViewContribution extends ContributionItem {
	public static final String FAST_VIEW = "FastView"; //$NON-NLS-1$

	private IWorkbenchWindow window;

	/**
	 * Create a new ToolBar item.
	 */
	public ShowFastViewContribution(IWorkbenchWindow window, String id) {
		super("showFastViewContr"); //$NON-NLS-1$
		this.window = window;
	}

	/**
	 * Lagacy constructor...automatically points to the legacy FastViewBar
	 *
	 * @param window
	 */
	public ShowFastViewContribution(IWorkbenchWindow window) {
		this(window, "showFastViewContr"); //$NON-NLS-1$
	}

	private void updateItem(ToolItem item, IViewReference ref) {
		if (item.getImage() != ref.getTitleImage()) {
			item.setImage(ref.getTitleImage());
		}

		if (!Util.equals(item.getToolTipText(), ref.getTitle())) {
			item.setToolTipText(ref.getTitle());
		}
	}

	public static ToolItem getItem(ToolBar toSearch, IWorkbenchPartReference ref) {
		ToolItem[] items = toSearch.getItems();

		for (int i = 0; i < items.length; i++) {
			ToolItem item = items[i];

			if (item.getData(FAST_VIEW) == ref) {
				return item;
			}
		}

		return null;
	}

	/**
	 * The default implementation of this <code>IContributionItem</code> method
	 * does nothing. Subclasses may override.
	 */
	@Override
	public void fill(ToolBar parent, int index) {
		// Get page.
		WorkbenchPage page = (WorkbenchPage) window.getActivePage();
		if (page == null) {
			return;
		}

		// Get views...
		List fvs = new ArrayList();

		// Create tool item for each view.
		for (Iterator fvIter = fvs.iterator(); fvIter.hasNext();) {
			final IViewReference ref = (IViewReference) fvIter.next();
			final ToolItem item = new ToolItem(parent, SWT.CHECK, index);
			updateItem(item, ref);
			item.setData(FAST_VIEW, ref);

			final IPropertyListener propertyListener = new IPropertyListener() {

				@Override
				public void propertyChanged(Object source, int propId) {
					if (propId == IWorkbenchPartConstants.PROP_TITLE) {
						if (!item.isDisposed()) {
							updateItem(item, ref);
						}
					}
				}

			};

			ref.addPropertyListener(propertyListener);

			item.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					ref.removePropertyListener(propertyListener);
				}
			});

			// Select the active fast view's icon.
			item.setSelection(false);

			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					showView(ref);
				}
			});
			index++;
		}
	}

	/**
	 * Returns whether the contribution is dynamic.
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * Open a view.
	 */
	private void showView(IViewReference ref) {
		// WorkbenchPage page = (WorkbenchPage) ref.getPage();
		// TODO compat: what to do about fast views
		E4Util.unsupported("ShowFastViewContribution:showView"); //$NON-NLS-1$
	}
}
