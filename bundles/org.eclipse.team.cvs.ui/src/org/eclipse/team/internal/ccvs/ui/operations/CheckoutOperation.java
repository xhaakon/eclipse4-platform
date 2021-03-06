/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFolder;
import org.eclipse.team.internal.ccvs.ui.Policy;
import org.eclipse.ui.IWorkbenchPart;

public abstract class CheckoutOperation extends RemoteOperation {

	public CheckoutOperation(IWorkbenchPart part, ICVSRemoteFolder[] remoteFolders) {
		super(part, remoteFolders);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.team.internal.ccvs.ui.operations.CVSOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProgressMonitor monitor) throws CVSException, InterruptedException {
		ICVSRemoteFolder[] folders = getRemoteFolders();
		checkout(folders, monitor);
	}
	
	/**
	 * This method invokes <code>checkout(ICVSRemoteFolder, IProgressMonitor)</code>
	 * for each remote folder of the operation.
	 * @param folders the remote folders for the operation
	 * @param monitor the progress monitor
	 * @throws CVSException if an error occured that should prevent the remaining
	 * folders from being checked out
	 */
	protected void checkout(ICVSRemoteFolder[] folders, IProgressMonitor monitor) throws CVSException {
		monitor.beginTask(null, folders.length * 100);
		for (int i = 0; i < folders.length; i++) {
			ICVSRemoteFolder folder = folders[i];
			IStatus result = checkout(folder, Policy.subMonitorFor(monitor, 100));
			collectStatus(result);
			Policy.checkCanceled(monitor);
		}
		monitor.done();
	}

	protected ICVSRemoteFolder[] getRemoteFolders() {
		return (ICVSRemoteFolder[])getRemoteResources();
	}
	
	/**
	 * Checkout the selected remote folders in a form appropriate for the operation subclass.
	 * @param folders
	 * @param monitor
	 */
	protected abstract IStatus checkout(ICVSRemoteFolder folder, IProgressMonitor monitor)  throws CVSException;
	
	/* (non-Javadoc)
	 * @see org.eclipse.team.internal.ccvs.ui.operations.CVSOperation#canRunAsJob()
	 */
	public boolean canRunAsJob() {
		return true;
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.team.ui.TeamOperation#isKeepOneProgressServiceEntry()
     */
    public boolean isKeepOneProgressServiceEntry() {
        // Keep the last repository provider operation in the progress service
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.team.ui.TeamOperation#getGotoAction()
     */
    protected IAction getGotoAction() {
        return getShowConsoleAction();
    }
}
