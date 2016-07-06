/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manuel Selva <manuel.selva@st.com> - Bug 197922
 *******************************************************************************/

package org.eclipse.ui.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;

/**
 * Provide a Handler for the Restart Workbench command.
 *
 */
public class RestartWorkbenchHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event){

		PlatformUI.getWorkbench().restart(true);
		return null;
	}
}
