/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.actions.breakpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IWatchpoint;

/**
 * Toggles access attribute of a watchpoint.
 */
public class AccessWatchpointToggleAction extends ModifyWatchpointAction {

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.actions.ModifyWatchpointAction#isEnabled(org.eclipse.debug.core.model.IWatchpoint)
     */
    @Override
	protected boolean isEnabled(IWatchpoint watchpoint) {
        return watchpoint.supportsAccess();
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.actions.ModifyWatchpointAction#toggleWatchpoint(org.eclipse.debug.core.model.IWatchpoint, boolean)
     */
    @Override
	protected void toggleWatchpoint(IWatchpoint watchpoint, boolean b) throws CoreException {
        watchpoint.setAccess(b);
    }

    /* (non-Javadoc)
     * @see org.eclipse.debug.internal.ui.actions.ModifyWatchpointAction#isChecked(org.eclipse.debug.core.model.IWatchpoint)
     */
    @Override
	protected boolean isChecked(IWatchpoint watchpoint) {
        try {
            return watchpoint.isAccess();
        } catch (CoreException e) {
        }
        return false;
    }

}
