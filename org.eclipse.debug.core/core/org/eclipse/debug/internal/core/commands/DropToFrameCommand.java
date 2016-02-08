/*******************************************************************************
 * Copyright (c) 2006, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.core.commands;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.commands.IDebugCommandRequest;
import org.eclipse.debug.core.commands.IDropToFrameHandler;
import org.eclipse.debug.core.model.IDropToFrame;

/**
 * Default drop to frame command for the standard debug model.
 *
 * @since 3.3
 */
public class DropToFrameCommand extends StepCommand implements IDropToFrameHandler {

	@Override
	protected Object getTarget(Object element) {
		return getAdapter(element, IDropToFrame.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.commands.StepCommand#isSteppable(java.lang.Object)
	 */
	@Override
	protected boolean isSteppable(Object target) throws CoreException {
		return ((IDropToFrame)target).canDropToFrame();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.commands.StepCommand#step(java.lang.Object)
	 */
	@Override
	protected void step(Object target) throws CoreException {
		((IDropToFrame)target).dropToFrame();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.commands.AbstractDebugCommand#getEnabledStateJobFamily(org.eclipse.debug.core.commands.IDebugCommandRequest)
	 */
	@Override
	protected Object getEnabledStateJobFamily(IDebugCommandRequest request) {
		return IDropToFrameHandler.class;
	}
}
