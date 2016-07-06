/*******************************************************************************
 * Copyright (c) 2014 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Istvan Sajtos <istvan.sajtos@liferay.com> - Bug 490608
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal.servlet;

import java.util.Locale;
import javax.servlet.http.*;

/**
 * @author Istvan Sajtos
 */
public class IncludeDispatchResponseWrapper extends HttpServletResponseWrapper {

	public IncludeDispatchResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void addCookie(Cookie cookie) {
		return;
	}

	@Override
	public void addDateHeader(String name, long date) {
		return;
	}

	@Override
	public void addHeader(String name, String value) {
		return;
	}

	@Override
	public void addIntHeader(String name, int value) {
		return;
	}

	@Override
	public void sendError(int sc) {
		return;
	}

	@Override
	public void sendError(int sc, String msg) {
		return;
	}

	@Override
	public void sendRedirect(String location) {
		return;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		return;
	}

	@Override
	public void setContentLength(int len) {
		return;
	}

	@Override
	public void setContentType(String type) {
		return;
	}

	@Override
	public void setDateHeader(String name, long date) {
		return;
	}

	@Override
	public void setHeader(String name, String value) {
		return;
	}

	@Override
	public void setIntHeader(String name, int value) {
		return;
	}

	@Override
	public void setLocale(Locale loc) {
		return;
	}

	@Override
	public void setStatus(int sc, String sm) {
		return;
	}

	@Override
	public void setStatus(int sc) {
		return;
	}

	@Override
	public void reset() {
		return;
	}

	@Override
	public void setBufferSize(int size) {
		return;
	}

}