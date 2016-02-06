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

package org.eclipse.equinox.http.servlet.internal.customizer;

import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.Servlet;
import org.eclipse.equinox.http.servlet.internal.HttpServiceRuntimeImpl;
import org.eclipse.equinox.http.servlet.internal.context.ContextController;
import org.eclipse.equinox.http.servlet.internal.error.HttpWhiteboardFailureException;
import org.eclipse.equinox.http.servlet.internal.registration.ServletRegistration;
import org.eclipse.equinox.http.servlet.internal.util.*;
import org.osgi.framework.*;
import org.osgi.service.http.runtime.dto.DTOConstants;
import org.osgi.service.http.runtime.dto.FailedServletDTO;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class ContextServletTrackerCustomizer
	extends RegistrationServiceTrackerCustomizer<Servlet, AtomicReference<ServletRegistration>> {

	public ContextServletTrackerCustomizer(
		BundleContext bundleContext, HttpServiceRuntimeImpl httpServiceRuntime,
		ContextController contextController) {

		super(bundleContext, httpServiceRuntime);

		this.contextController = contextController;
	}

	@Override
	public AtomicReference<ServletRegistration> addingService(
		ServiceReference<Servlet> serviceReference) {

		AtomicReference<ServletRegistration> result = new AtomicReference<ServletRegistration>();
		if (!httpServiceRuntime.matches(serviceReference)) {
			return result;
		}

		if (!contextController.matches(serviceReference)) {
			return result;
		}

		try {
			result.set(contextController.addServletRegistration(serviceReference));
		}
		catch (HttpWhiteboardFailureException hwfe) {
			httpServiceRuntime.log(hwfe.getMessage(), hwfe);

			recordFailedServletDTO(serviceReference, hwfe.getFailureReason());
		}
		catch (Exception e) {
			httpServiceRuntime.log(e.getMessage(), e);

			recordFailedServletDTO(serviceReference, DTOConstants.FAILURE_REASON_EXCEPTION_ON_INIT);
		}

		return result;
	}

	@Override
	public void modifiedService(
		ServiceReference<Servlet> serviceReference,
		AtomicReference<ServletRegistration> servletReference) {

		removedService(serviceReference, servletReference);
		AtomicReference<ServletRegistration> added = addingService(serviceReference);
		servletReference.set(added.get());
	}

	@Override
	public void removedService(
		ServiceReference<Servlet> serviceReference,
		AtomicReference<ServletRegistration> servletReference) {
		ServletRegistration registration = servletReference.get();
		if (registration != null) {
			// destroy will unget the service object we were using
			registration.destroy();
		}

		contextController.getHttpServiceRuntime().removeFailedServletDTOs(serviceReference);
	}

	private void recordFailedServletDTO(
		ServiceReference<Servlet> serviceReference, int failureReason) {

		FailedServletDTO failedServletDTO = new FailedServletDTO();

		failedServletDTO.asyncSupported = BooleanPlus.from(
			serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED), false);
		failedServletDTO.failureReason = failureReason;
		failedServletDTO.initParams = ServiceProperties.parseInitParams(
			serviceReference, HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX);
		failedServletDTO.name = (String)serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME);
		failedServletDTO.patterns = StringPlus.from(
			serviceReference.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN)).toArray(new String[0]);
		failedServletDTO.serviceId = (Long)serviceReference.getProperty(Constants.SERVICE_ID);
		failedServletDTO.servletContextId = contextController.getServiceId();
		failedServletDTO.servletInfo = Const.BLANK;

		contextController.getHttpServiceRuntime().recordFailedServletDTO(serviceReference, failedServletDTO);
	}

	private ContextController contextController;

}
