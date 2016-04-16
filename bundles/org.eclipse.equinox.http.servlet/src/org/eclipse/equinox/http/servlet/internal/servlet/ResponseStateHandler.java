/*******************************************************************************
 * Copyright (c) 2014, 2015 Raymond Augé and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package org.eclipse.equinox.http.servlet.internal.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.registration.EndpointRegistration;
import org.eclipse.equinox.http.servlet.internal.registration.FilterRegistration;

/**
 * @author Raymond Augé
 */
public class ResponseStateHandler {

	public ResponseStateHandler(
		HttpServletRequest request, HttpServletResponse response,
		DispatchTargets dispatchTargets, DispatcherType dispatcherType) {

		this.request = request;
		this.response = response;
		this.dispatchTargets = dispatchTargets;
		this.dispatcherType = dispatcherType;
	}

	public void processRequest() throws IOException, ServletException {
		List<ServletRequestListener> servletRequestListeners = getServletRequestListener();
		EndpointRegistration<?> endpoint = dispatchTargets.getServletRegistration();
		List<FilterRegistration> filters = dispatchTargets.getMatchingFilterRegistrations();

		endpoint.addReference();

		for (FilterRegistration filterRegistration : filters) {
			filterRegistration.addReference();
		}

		ServletRequestEvent servletRequestEvent = null;

		try {

			if ((dispatcherType == DispatcherType.REQUEST) && !servletRequestListeners.isEmpty()) {
				servletRequestEvent = new ServletRequestEvent(endpoint.getServletContext(), request);
				for (ServletRequestListener servletRequestListener : servletRequestListeners) {
					servletRequestListener.requestInitialized(servletRequestEvent);
				}
			}

			if (endpoint.getServletContextHelper().handleSecurity(request, response)) {
				if (filters.isEmpty()) {
					endpoint.service(request, response);
				}
				else {
					Collections.sort(filters);

					FilterChain chain = new FilterChainImpl(
						filters, endpoint, dispatcherType);

					chain.doFilter(request, response);
				}
			}
		}
		catch (Exception e) {
			if (!(e instanceof IOException) &&
				!(e instanceof RuntimeException) &&
				!(e instanceof ServletException)) {

				e = new ServletException(e);
			}

			setException(e);

			if (dispatcherType != DispatcherType.REQUEST) {
				throwException(e);
			}
		}
		finally {
			endpoint.removeReference();

			for (FilterRegistration filterRegistration : filters) {
				filterRegistration.removeReference();
			}

			if (dispatcherType == DispatcherType.REQUEST) {
				handleErrors();

				for (ServletRequestListener servletRequestListener : servletRequestListeners) {
					servletRequestListener.requestDestroyed(servletRequestEvent);
				}
			}
		}
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	private List<ServletRequestListener> getServletRequestListener() {
		return dispatchTargets.getContextController().getEventListeners().get(ServletRequestListener.class);
	}

	private void handleErrors() throws IOException, ServletException {
		if (exception != null) {
			handleException();
		}
		else {
			handleResponseCode();
		}
	}

	private void handleException() throws IOException, ServletException {
		if (!(response instanceof HttpServletResponseWrapper)) {
			throw new IllegalStateException("Response isn't a wrapper"); //$NON-NLS-1$
		}

		HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper)response;

		HttpServletResponseWrapperImpl wrapperImpl = null;

		while (true) {
			if (wrapper instanceof HttpServletResponseWrapperImpl) {
				wrapperImpl = (HttpServletResponseWrapperImpl)wrapper;
			}
			else if (wrapper.getResponse() instanceof HttpServletResponseWrapper) {
				wrapper = (HttpServletResponseWrapper)wrapper.getResponse();

				continue;
			}

			break;
		}

		if (wrapperImpl == null) {
			throw new IllegalStateException("Can't locate response impl"); //$NON-NLS-1$
		}

		HttpServletResponse wrappedResponse = (HttpServletResponse)wrapperImpl.getResponse();

		if (wrappedResponse.isCommitted()) {
			throwException(exception);
		}

		ContextController contextController = dispatchTargets.getContextController();
		Class<? extends Exception> clazz = exception.getClass();
		String className = clazz.getName();

		DispatchTargets errorDispatchTargets = contextController.getDispatchTargets(
			className, null, null, null, null, null, Match.EXACT, null);

		if (errorDispatchTargets == null) {
			throwException(exception);
		}

		request.setAttribute(RequestDispatcher.ERROR_EXCEPTION, exception);
		request.setAttribute(RequestDispatcher.ERROR_EXCEPTION_TYPE, className);
		request.setAttribute(RequestDispatcher.ERROR_MESSAGE, exception.getMessage());
		request.setAttribute(
			RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
		request.setAttribute(
			RequestDispatcher.ERROR_SERVLET_NAME,
			errorDispatchTargets.getServletRegistration().getName());
		request.setAttribute(
			RequestDispatcher.ERROR_STATUS_CODE,
			HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		HttpServletResponseWrapper wrapperResponse =
			new HttpServletResponseWrapperImpl(wrappedResponse);

		ResponseStateHandler responseStateHandler = new ResponseStateHandler(
			request, wrapperResponse, errorDispatchTargets, DispatcherType.ERROR);

		responseStateHandler.processRequest();

		wrappedResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	private void handleResponseCode() throws IOException, ServletException {
		if (!(response instanceof HttpServletResponseWrapper)) {
			throw new IllegalStateException("Response isn't a wrapper"); //$NON-NLS-1$
		}

		HttpServletResponseWrapper wrapper = (HttpServletResponseWrapper)response;

		int status = wrapper.getStatus();

		if (status < HttpServletResponse.SC_BAD_REQUEST) {
			return;
		}

		HttpServletResponseWrapperImpl wrapperImpl = null;

		while (true) {
			if (wrapper instanceof HttpServletResponseWrapperImpl) {
				wrapperImpl = (HttpServletResponseWrapperImpl)wrapper;
			}
			else if (wrapper.getResponse() instanceof HttpServletResponseWrapper) {
				wrapper = (HttpServletResponseWrapper)wrapper.getResponse();

				continue;
			}

			break;
		}

		if (wrapperImpl == null) {
			throw new IllegalStateException("Can't locate response impl"); //$NON-NLS-1$
		}

		HttpServletResponse wrappedResponse = (HttpServletResponse)wrapperImpl.getResponse();

		if (status == -1) {
			// There's nothing more we can do here.
			return;
		}
		if (wrappedResponse.isCommitted()) {
			// the response is committed already, but we need to propagate the error code anyway
			wrappedResponse.sendError(status, wrapperImpl.getMessage());
			// There's nothing more we can do here.
			return;
		}

		ContextController contextController = dispatchTargets.getContextController();

		DispatchTargets errorDispatchTargets = contextController.getDispatchTargets(
			String.valueOf(status), null, null, null, null, null, Match.EXACT, null);

		if (errorDispatchTargets == null) {
			wrappedResponse.sendError(status, wrapperImpl.getMessage());

			return;
		}

		request.setAttribute(
			RequestDispatcher.ERROR_MESSAGE, wrapperImpl.getMessage());
		request.setAttribute(
			RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
		request.setAttribute(
			RequestDispatcher.ERROR_SERVLET_NAME,
			errorDispatchTargets.getServletRegistration().getName());
		request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, status);

		HttpServletResponseWrapper wrapperResponse =
			new HttpServletResponseWrapperImpl(wrappedResponse);

		ResponseStateHandler responseStateHandler = new ResponseStateHandler(
			request, wrapperResponse, errorDispatchTargets, DispatcherType.ERROR);

		wrappedResponse.setStatus(status);

		responseStateHandler.processRequest();
	}

	private void throwException(Exception e)
		throws IOException, ServletException {

		if (e instanceof RuntimeException) {
			throw (RuntimeException)e;
		}
		else if (e instanceof IOException) {
			throw (IOException)e;
		}
		else if (e instanceof ServletException) {
			throw (ServletException)e;
		}
	}

	private DispatchTargets dispatchTargets;
	private DispatcherType dispatcherType;
	private Exception exception;
	private HttpServletRequest request;
	private HttpServletResponse response;

}