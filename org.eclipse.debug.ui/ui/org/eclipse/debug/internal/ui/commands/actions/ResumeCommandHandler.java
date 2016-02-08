/*******************************************************************************
 * Copyright (c) 2011, 2013 Wind River Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Wind River Systems - initial API and implementation
 *     IBM Corporation - bug fixing
 *******************************************************************************/
package org.eclipse.debug.internal.ui.commands.actions;

import org.eclipse.debug.core.commands.IResumeHandler;
import org.eclipse.debug.ui.actions.DebugCommandHandler;

/**
 * Default handler for command.  It ensures that the keyboard accelerator works even
 * if the menu action set is not enabled.
 * 
 * @since 3.8
 */
public class ResumeCommandHandler extends DebugCommandHandler {

    @Override
	protected Class<IResumeHandler> getCommandType() {
        return IResumeHandler.class;
    }
    
}
