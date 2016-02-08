/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.actions;


import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.IInternalDebugCoreConstants;
import org.eclipse.debug.internal.ui.contextlaunching.LaunchingResourceManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.actions.RelaunchLastAction;

/**
 * Re-launches the last profile-mode launch
 * 
 * This menu item appears in the main 'Run' menu
 * 
 * @see RelaunchLastAction
 * @see RunLastAction
 * @see DebugLastAction
 * 
 */
public class ProfileLastAction extends RelaunchLastAction {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.RelaunchLastAction#getMode()
	 */
	@Override
	public String getMode() {
		return ILaunchManager.PROFILE_MODE;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.actions.RelaunchLastAction#getLaunchGroupId()
	 */
	@Override
	public String getLaunchGroupId() {
		return IDebugUIConstants.ID_PROFILE_LAUNCH_GROUP;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.RelaunchLastAction#getText()
	 */
	@Override
	protected String getText() {
		if(LaunchingResourceManager.isContextLaunchEnabled()) {
			return ActionMessages.ProfileLastAction_1;
		}
		else {
			return ActionMessages.ProfileLastAction_0;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.RelaunchLastAction#getTooltipText()
	 */
	@Override
	protected String getTooltipText() {
		return IInternalDebugCoreConstants.EMPTY_STRING;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.actions.RelaunchLastAction#getCommandId()
	 */
	@Override
	protected String getCommandId() {
		return "org.eclipse.debug.ui.commands.ProfileLast"; //$NON-NLS-1$
	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.actions.RelaunchLastAction#getDescription()
	 */
	@Override
	protected String getDescription() {
		if(LaunchingResourceManager.isContextLaunchEnabled()) {
			return ActionMessages.ProfileLastAction_2;
		}
		else {
			return ActionMessages.ProfileLastAction_3;
		}
	}	
}
