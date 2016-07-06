/*******************************************************************************
 * Copyright (c) 2002, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.cheatsheets.registry;

import java.lang.reflect.Constructor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.cheatsheets.AbstractItemExtensionElement;
import org.eclipse.ui.internal.cheatsheets.CheatSheetPlugin;
import org.eclipse.ui.internal.cheatsheets.ICheatSheetResource;
import org.eclipse.ui.internal.cheatsheets.Messages;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.osgi.framework.Bundle;

/**
 *	Instances represent registered cheat sheet item extensions.
 */
public class CheatSheetItemExtensionElement extends WorkbenchAdapter implements IAdaptable {
	private String className;
	private String itemAttribute;
	private IConfigurationElement configurationElement;
	private final Class[] stringArray = { String.class };

	/**
	 *	Create a new instance of this class
	 *
	 */
	public CheatSheetItemExtensionElement() {
	}

	/**
	 * Returns an object which is an instance of the given class
	 * associated with this object. Returns <code>null</code> if
	 * no such object can be found.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return (T) this;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 *
	 * @return IConfigurationElement
	 */
	public IConfigurationElement getConfigurationElement() {
		return configurationElement;
	}

	/**
	 *	Answer the classname parameter of this element
	 *
	 *	@return java.lang.String
	 */
	public String getClassName() {
		return className;
	}

	/**
	 *	Answer the itemAttribute parameter of this element
	 *
	 *	@return java.lang.String
	 */
	public String getItemAttribute() {
		return itemAttribute;
	}

	/**
	 *
	 * @param newConfigurationElement IConfigurationElement
	 */
	public void setConfigurationElement(IConfigurationElement newConfigurationElement) {
		configurationElement = newConfigurationElement;
	}

	/**
	 *	Set the className parameter of this element
	 *
	 *	@param value java.lang.String
	 */
	public void setClassName(String value) {
		className = value;
	}

	/**
	 *	Set the itemAttribute parameter of this element
	 *
	 *	@param value java.lang.String
	 */
	public void setItemAttribute(String value) {
		itemAttribute = value;
	}

	public AbstractItemExtensionElement createInstance() {
		Class<?> extClass = null;
		AbstractItemExtensionElement extElement = null;
		String pluginId = configurationElement.getContributor().getName();

		try {
			Bundle bundle = Platform.getBundle(pluginId);
			extClass = bundle.loadClass(className);
		} catch (Exception e) {
			String message = NLS.bind(Messages.ERROR_LOADING_CLASS, (new Object[] {className}));
			IStatus status = new Status(IStatus.ERROR, ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID, IStatus.OK, message, e);
			CheatSheetPlugin.getPlugin().getLog().log(status);
		}
		try {
			if (extClass != null) {
				Constructor c = extClass.getConstructor(stringArray);
				extElement = (AbstractItemExtensionElement) c.newInstance(itemAttribute);
			}
		} catch (Exception e) {
			String message = NLS.bind(Messages.ERROR_CREATING_CLASS, (new Object[] {className}));
			IStatus status = new Status(IStatus.ERROR, ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID, IStatus.OK, message, e);
			CheatSheetPlugin.getPlugin().getLog().log(status);
		}

		if (extElement != null){
			return extElement;
		}

		return null;
	}
}
