/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.content;

import org.eclipse.core.runtime.QualifiedName;

import java.util.*;
import org.eclipse.core.internal.runtime.RuntimeLog;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferenceNodeVisitor;
import org.eclipse.osgi.util.NLS;
import org.osgi.service.prefs.BackingStoreException;

/**
 * This class is a sidekick for ContentTypeManager that provides mechanisms for
 * creating content types from the extension registry (which ContentTypeManager
 *  is oblivious to).
 */
public class ContentTypeBuilder {
	public static final String PT_CONTENTTYPES = "contentTypes"; //$NON-NLS-1$
	private ContentTypeCatalog catalog;

	private static String getUniqueId(String namespace, String baseTypeId) {
		if (baseTypeId == null)
			return null;
		int separatorPosition = baseTypeId.lastIndexOf('.');
		// base type is defined in the same namespace
		if (separatorPosition == -1)
			baseTypeId = namespace + '.' + baseTypeId;
		return baseTypeId;
	}

	private static QualifiedName parseQualifiedName(String namespace, String value) {
		if (value == null)
			return null;
		int separatorPosition = value.lastIndexOf('.');
		// base type is defined in the same namespace
		if (separatorPosition == -1)
			return new QualifiedName(namespace, value);
		if (separatorPosition == 0 || separatorPosition == value.length() - 1)
			// invalid value specified
			return null;
		namespace = value.substring(0, separatorPosition);
		String simpleValue = value.substring(separatorPosition + 1);
		return new QualifiedName(namespace, simpleValue);
	}

	private static byte parsePriority(String priority) {
		if (priority == null)
			return ContentType.PRIORITY_NORMAL;
		if (priority.equals("high")) //$NON-NLS-1$
			return ContentType.PRIORITY_HIGH;
		if (priority.equals("low")) //$NON-NLS-1$
			return ContentType.PRIORITY_LOW;
		if (!priority.equals("normal")) //$NON-NLS-1$
			return ContentType.PRIORITY_NORMAL;
		//TODO: should log - INVALID PRIORITY
		return ContentType.PRIORITY_NORMAL;
	}

	protected ContentTypeBuilder(ContentTypeCatalog catalog) {
		this.catalog = catalog;
	}

	private void addFileAssociation(IConfigurationElement fileAssociationElement, ContentType target) {
		String[] fileNames = Util.parseItems(fileAssociationElement.getAttributeAsIs("file-names")); //$NON-NLS-1$
		for (int i = 0; i < fileNames.length; i++)
			target.internalAddFileSpec(fileNames[i], IContentType.FILE_NAME_SPEC | ContentType.SPEC_PRE_DEFINED);
		String[] fileExtensions = Util.parseItems(fileAssociationElement.getAttributeAsIs("file-extensions")); //$NON-NLS-1$
		for (int i = 0; i < fileExtensions.length; i++)
			target.internalAddFileSpec(fileExtensions[i], IContentType.FILE_EXTENSION_SPEC | ContentType.SPEC_PRE_DEFINED);
	}

	/**
	 * Builds all content types found in the extension registry.
	 */
	public void buildCatalog() {
		IConfigurationElement[] allContentTypeCEs = getConfigurationElements();
		for (int i = 0; i < allContentTypeCEs.length; i++)
			if (allContentTypeCEs[i].getName().equals("content-type")) //$NON-NLS-1$
				registerContentType(allContentTypeCEs[i]);
		for (int i = 0; i < allContentTypeCEs.length; i++)
			if (allContentTypeCEs[i].getName().equals("file-association")) //$NON-NLS-1$
				registerFileAssociation(allContentTypeCEs[i]);
		applyPreferences();
	}

	/**
	 * Applies any existing preferences to content types as a batch operation.
	 */
	private void applyPreferences() {
		try {
			final ContentTypeCatalog localCatalog = catalog;
			final IEclipsePreferences root = localCatalog.getManager().getPreferences();
			root.accept(new IPreferenceNodeVisitor() {
				@Override
				public boolean visit(IEclipsePreferences node) {
					if (node == root)
						return true;
					ContentType contentType = localCatalog.internalGetContentType(node.name());
					if (contentType != null)
						contentType.processPreferences(node);
					// content type nodes don't have any children anyway
					return false;
				}
			});
		} catch (BackingStoreException bse) {
			ContentType.log(ContentMessages.content_errorLoadingSettings, bse);
		}
	}

	/**
	 * @throws CoreException if mandatory attributes are missing in the markup
	 */
	private ContentType createContentType(IConfigurationElement contentTypeCE) throws CoreException {
		String namespace = contentTypeCE.getContributor().getName();
		String simpleId = contentTypeCE.getAttributeAsIs("id"); //$NON-NLS-1$
		String name = contentTypeCE.getAttribute("name"); //$NON-NLS-1$

		if (simpleId == null)
			missingMandatoryAttribute(ContentMessages.content_missingIdentifier, namespace);
		String uniqueId;
		if (simpleId.lastIndexOf('.') == -1)
			uniqueId = namespace + '.' + simpleId;
		else
			uniqueId = simpleId;
		if (name == null)
			missingMandatoryAttribute(ContentMessages.content_missingName, uniqueId);

		byte priority = parsePriority(contentTypeCE.getAttributeAsIs("priority")); //$NON-NLS-1$);
		String[] fileNames = Util.parseItems(contentTypeCE.getAttributeAsIs("file-names")); //$NON-NLS-1$
		String[] fileExtensions = Util.parseItems(contentTypeCE.getAttributeAsIs("file-extensions")); //$NON-NLS-1$
		String baseTypeId = getUniqueId(namespace, contentTypeCE.getAttributeAsIs("base-type")); //$NON-NLS-1$
		String aliasTargetTypeId = getUniqueId(namespace, contentTypeCE.getAttributeAsIs("alias-for")); //$NON-NLS-1$
		IConfigurationElement[] propertyCEs = null;
		Map<QualifiedName, String> defaultProperties = null;
		if ((propertyCEs = contentTypeCE.getChildren("property")).length > 0) { //$NON-NLS-1$
			defaultProperties = new HashMap<QualifiedName, String>();
			for (int i = 0; i < propertyCEs.length; i++) {
				String defaultValue = propertyCEs[i].getAttributeAsIs("default"); //$NON-NLS-1$
				if (defaultValue == null)
					// empty string means: default value is null
					defaultValue = ""; //$NON-NLS-1$
				String propertyKey = propertyCEs[i].getAttributeAsIs("name"); //$NON-NLS-1$
				QualifiedName qualifiedKey = parseQualifiedName(namespace, propertyKey);
				if (qualifiedKey == null) {
					if (ContentTypeManager.DEBUGGING) {
						String message = NLS.bind(ContentMessages.content_invalidProperty, propertyKey, getUniqueId(namespace, simpleId));
						ContentType.log(message, null);
					}
					continue;
				}
				defaultProperties.put(qualifiedKey, defaultValue);
			}
		}
		String defaultCharset = contentTypeCE.getAttributeAsIs("default-charset"); //$NON-NLS-1$
		if (defaultCharset != null)
			if (defaultProperties == null)
				defaultProperties = Collections.singletonMap(IContentDescription.CHARSET, defaultCharset);
			else if (!defaultProperties.containsKey(IContentDescription.CHARSET))
				defaultProperties.put(IContentDescription.CHARSET, defaultCharset);
		return ContentType.createContentType(catalog, uniqueId, name, priority, fileExtensions, fileNames, baseTypeId, aliasTargetTypeId, defaultProperties, contentTypeCE);
	}

	// Store this around for performance
	private final static IConfigurationElement[] emptyConfArray = new IConfigurationElement[0];

	/**
	 * Gets configuration elements for both "backward compatible" extension point
	 * 		org.eclipse.core.runtime.contentTypes
	 * and "new" extension point controlled by this plugin:
	 * 		org.eclipse.core.contenttype.contentTypes
	 */
	protected IConfigurationElement[] getConfigurationElements() {
		IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry == null)
			return emptyConfArray;
		IConfigurationElement[] oldConfigElements = emptyConfArray;
		IConfigurationElement[] newConfigElements = emptyConfArray;
		// "old" extension point
		IExtensionPoint oldPoint = registry.getExtensionPoint(IContentConstants.RUNTIME_NAME, PT_CONTENTTYPES);
		if (oldPoint != null)
			oldConfigElements = oldPoint.getConfigurationElements();
		// "new" extension point
		IExtensionPoint newPoint = registry.getExtensionPoint(IContentConstants.CONTENT_NAME, PT_CONTENTTYPES);
		if (newPoint != null)
			newConfigElements = newPoint.getConfigurationElements();

		IConfigurationElement[] allContentTypeCEs = new IConfigurationElement[oldConfigElements.length + newConfigElements.length];
		System.arraycopy(oldConfigElements, 0, allContentTypeCEs, 0, oldConfigElements.length);
		System.arraycopy(newConfigElements, 0, allContentTypeCEs, oldConfigElements.length, newConfigElements.length);

		return allContentTypeCEs;
	}

	private void missingMandatoryAttribute(String messageKey, String argument) throws CoreException {
		String message = NLS.bind(messageKey, argument);
		throw new CoreException(new Status(IStatus.ERROR, ContentMessages.OWNER_NAME, 0, message, null));
	}

	private void registerContentType(IConfigurationElement contentTypeCE) {
		try {
			ContentType contentType = createContentType(contentTypeCE);
			catalog.addContentType(contentType);
		} catch (CoreException e) {
			// failed validation
			RuntimeLog.log(e.getStatus());
		}
	}

	/* Adds extra file associations to existing content types. If the content
	 * type has not been added, the file association is ignored.
	 */
	private void registerFileAssociation(IConfigurationElement fileAssociationElement) {
		//TODO: need to ensure the config. element is valid
		String contentTypeId = getUniqueId(fileAssociationElement.getContributor().getName(), fileAssociationElement.getAttribute("content-type")); //$NON-NLS-1$
		ContentType target = catalog.internalGetContentType(contentTypeId);
		if (target == null)
			return;
		addFileAssociation(fileAssociationElement, target);
	}
}
