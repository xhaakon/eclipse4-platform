/*******************************************************************************
 * Copyright (c) 2007, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@gmail.com> - Bug 440810
 ******************************************************************************/

package org.eclipse.ui.internal;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.model.PerspectiveLabelProvider;

/**
 * This handler is used to switch between perspectives using the keyboard.
 * <p>
 * Replacement for CyclePerspectiveAction
 * </p>
 *
 * @since 3.3
 */
public class CyclePerspectiveHandler extends CycleBaseHandler {
	private PerspectiveLabelProvider labelProvider = new PerspectiveLabelProvider(
            false);

	@Override
	protected void addItems(Table table, WorkbenchPage page) {
		IPerspectiveDescriptor perspectives[] = page.getSortedPerspectives();
        for (int i = perspectives.length - 1; i >= 0; i--) {
            TableItem item = new TableItem(table, SWT.NONE);
            IPerspectiveDescriptor desc = perspectives[i];
            String text = labelProvider.getText(desc);
            if(text == null) {
				text = "";//$NON-NLS-1$
			}
            item.setText(text);
            item.setImage(labelProvider.getImage(desc));
            item.setData(desc);
        }

	}

	@Override
	protected ParameterizedCommand getBackwardCommand() {
		final ICommandService commandService = window.getWorkbench().getService(ICommandService.class);
		final Command command = commandService.getCommand(IWorkbenchCommandConstants.WINDOW_PREVIOUS_PERSPECTIVE);
		ParameterizedCommand commandBack = new ParameterizedCommand(command, null);
		return commandBack;
	}

	@Override
	protected ParameterizedCommand getForwardCommand() {
		final ICommandService commandService = window.getWorkbench().getService(ICommandService.class);
		final Command command = commandService.getCommand(IWorkbenchCommandConstants.WINDOW_NEXT_PERSPECTIVE);
		ParameterizedCommand commandF = new ParameterizedCommand(command, null);
		return commandF;
	}

	@Override
	protected String getTableHeader(IWorkbenchPart activePart) {
		return WorkbenchMessages.CyclePerspectiveAction_header;
	}

	@Override
	public void dispose() {
		if (labelProvider!=null) {
			labelProvider.dispose();
			labelProvider = null;
		}
		super.dispose();
	}
}
