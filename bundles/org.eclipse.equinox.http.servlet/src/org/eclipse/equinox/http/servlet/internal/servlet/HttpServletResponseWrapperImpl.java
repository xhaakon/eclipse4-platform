/*******************************************************************************
 * Copyright (c) 2014 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal.servlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Raymond Augé
 */
public class HttpServletResponseWrapperImpl extends HttpServletResponseWrapper {

	public HttpServletResponseWrapperImpl(HttpServletResponse response) {
		super(response);
	}

	@Override
	public boolean isCommitted() {
		if (this.status != -1) {
			return true;
		}

		return super.isCommitted();
	}

	@Override
	public void sendError(int status) {
		this.status = status;
	}

	@Override
	public void sendError(int status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public int getStatus() {
		return status;
	}

	private int status = -1;
	private String message;

}