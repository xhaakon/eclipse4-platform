/*******************************************************************************
 * Copyright (c) 2005, 2014 Cognos Incorporated, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cognos Incorporated - initial API and implementation
 *     IBM Corporation - bug fixes and enhancements
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.servlet;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequestWrapper;

//This class unwraps the request so it can be processed by the underlying servlet container.
public class RequestDispatcherAdaptor implements RequestDispatcher {

	private RequestDispatcher requestDispatcher;

	public RequestDispatcherAdaptor(RequestDispatcher requestDispatcher) {
		this.requestDispatcher = requestDispatcher;
	}

	public void forward(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		while (true) {
			if (req instanceof HttpServletRequestBuilder.RequestGetter) {
				req = ((HttpServletRequestBuilder.RequestGetter) req).getOriginalRequest();
				break;
			}

			if (req instanceof HttpServletRequestWrapper) {
				req = ((HttpServletRequestWrapper)req).getRequest();
				continue;
			}

			break;
		}

		requestDispatcher.forward(req, resp);
	}

	public void include(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
		if (req instanceof HttpServletRequestBuilder.RequestGetter)
			req = ((HttpServletRequestBuilder.RequestGetter) req).getOriginalRequest();

		requestDispatcher.include(req, resp);
	}
}
