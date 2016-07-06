/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.ide.dialogs;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemMessages;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;

/**
 * FileSystemSelectionArea is the area used to select the file system.
 * @since 3.2
 *
 */

public class FileSystemSelectionArea {

	private Label fileSystemTitle;
	private ComboViewer fileSystems;

	/**
	 * Create a new instance of the receiver.
	 */
	public FileSystemSelectionArea(){

	}

	/**
	 * Create the contents of the receiver in composite.
	 * @param composite
	 */
	public void createContents(Composite composite) {

		fileSystemTitle = new Label(composite, SWT.NONE);
		fileSystemTitle.setText(FileSystemMessages.FileSystemSelection_title);
		fileSystemTitle.setFont(composite.getFont());

		fileSystems = new ComboViewer(composite, SWT.READ_ONLY);
		fileSystems.getControl().setFont(composite.getFont());

		fileSystems.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((FileSystemConfiguration) element).getLabel();
			}
		});

		fileSystems.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void dispose() {
				// Nothing to do
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return FileSystemSupportRegistry.getInstance()
						.getConfigurations();
			}

			@Override
			public void inputChanged(org.eclipse.jface.viewers.Viewer viewer,
					Object oldInput, Object newInput) {
				// Nothing to do
			}

		});

		fileSystems.setInput(this);
		fileSystems.setSelection(new StructuredSelection(
				FileSystemSupportRegistry.getInstance()
						.getDefaultConfiguration()));
	}

	/**
	 * Return the selected configuration.
	 * @return FileSystemConfiguration or <code>null</code> if nothing
	 * is selected.
	 */
	public FileSystemConfiguration getSelectedConfiguration() {
		ISelection selection = fileSystems.getSelection();

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			if (structured.size() == 1) {
				return (FileSystemConfiguration) structured.getFirstElement();
			}
		}

		return null;
	}

	/**
	 * Set the enablement state of the widget.
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		fileSystemTitle.setEnabled(enabled);
		fileSystems.getControl().setEnabled(enabled);

	}
}
