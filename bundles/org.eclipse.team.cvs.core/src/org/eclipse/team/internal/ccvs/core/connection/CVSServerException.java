/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.core.connection;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.CVSStatus;

/**
 * Client has received an error response from the server. 
 */
public class CVSServerException extends CVSException {
	
	private static final long serialVersionUID = 1L;

    /**
	 * Return true if the exception from the cvs server is the no tag error, and false
	 * otherwise.
	 */
	public boolean isNoTagException() {
		IStatus status = getStatus();
		if ( ! status.isMultiStatus())
			return false;
		IStatus[] children = ((MultiStatus)status).getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i].getCode() == CVSStatus.NO_SUCH_TAG) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return true if the exceptions status contains any error status messages
	 */
	public boolean containsErrors() {
		IStatus status = getStatus();
		if ( ! status.isMultiStatus())
			return status.getSeverity() == IStatus.ERROR;
		IStatus[] children = ((MultiStatus)status).getChildren();
		for (int i=0;i<children.length;i++) {
			if (children[i].getSeverity() == IStatus.ERROR)
				return true;
		}
		return false;
	}
	
	/** The status should have a status code of CVSStatus.SERVER_ERROR
	 * @param status
	 */
	public CVSServerException(IStatus status) {
		super(status);
	}
}
