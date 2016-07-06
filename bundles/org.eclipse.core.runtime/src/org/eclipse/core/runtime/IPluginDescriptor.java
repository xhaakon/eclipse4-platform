/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <Lars.Vogel@vogella.com> - Bug 478773
 *******************************************************************************/
package org.eclipse.core.runtime;

import java.net.URL;
import java.util.*;

/**
 * This interface was only support by plug-ins which explicitly require the
 * org.eclipse.core.runtime.compatibility plug-in.
 * </p>
 * <p>
 * It is not used anymore as Eclipse 4.6 removed this plug-in.
 *
 * @deprecated
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
@Deprecated
public interface IPluginDescriptor {
	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public IExtension getExtension(String extensionName);

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public IExtensionPoint getExtensionPoint(String extensionPointId);

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public IExtensionPoint[] getExtensionPoints();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public IExtension[] getExtensions();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public URL getInstallURL();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public String getLabel();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public Plugin getPlugin() throws CoreException;

	/**
	 * Not supported anymore as Eclipse 4.6
	 * @deprecated
	 * Use
	 * <pre>
	 *     bundle.loadClass(className)
	 * </pre>
	 * where <code>bundle</code> is the bundle associated with
	 * the relevant plug-in.
	 */
	@Deprecated
	public ClassLoader getPluginClassLoader();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public IPluginPrerequisite[] getPluginPrerequisites();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public String getProviderName();

	/**
	 * Not supported anymore as Eclipse 4.6
	 * @deprecated
	 * Use
	 * <pre>
	 *     Platform.getResourceBundle(bundle)
	 * </pre>
	 * where <code>bundle</code> is the bundle associated with
	 * the relevant plug-in.
	 */
	@Deprecated
	public ResourceBundle getResourceBundle() throws MissingResourceException;

	/**
	 * Not supported anymore as Eclipse 4.6
	 * @deprecated
	 * Use
	 * <pre>
	 *     Platform.getResourceString(bundle, value)
	 * </pre>
	 * where <code>bundle</code> is the bundle associated with
	 * the relevant plug-in.
	 */
	@Deprecated
	public String getResourceString(String value);

	/**
	 * Not supported anymore as Eclipse 4.6
	 * @deprecated
	 * Use
	 * <pre>
	 *     Platform.getResourceString(bundle, value, resourceBundle)
	 * </pre>
	 * where <code>bundle</code> is the bundle associated with
	 * the relevant plug-in.
	 */
	@Deprecated
	public String getResourceString(String value, ResourceBundle resourceBundle);

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public ILibrary[] getRuntimeLibraries();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public String getUniqueIdentifier();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public PluginVersionIdentifier getVersionIdentifier();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public boolean isPluginActivated();

	/**
	 * Not supported anymore as Eclipse 4.6
	 */
	@Deprecated
	public URL find(IPath path);

	/**
	 * Not supported anymore as Eclipse 4.6
	 * @deprecated
	 * Use
	 * <pre>
	 *     Platform.find(bundle, path, override)
	 * </pre>
	 * where <code>bundle</code> is the bundle associated with
	 * the relevant plug-in.
	 */
	@Deprecated
	public URL find(IPath path, Map<String,String> override);
}
