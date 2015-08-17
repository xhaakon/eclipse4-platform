/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
import org.eclipse.core.runtime.content.IContentDescription;

/**
 * A content description for which all  properties have default values.
 */
public final class DefaultDescription extends BasicDescription {

	public DefaultDescription(IContentTypeInfo contentTypeInfo) {
		super(contentTypeInfo);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DefaultDescription))
			return false;
		// see ContentType.equals()
		return contentTypeInfo.equals(((DefaultDescription) obj).contentTypeInfo);
	}

	/**
	 * @see IContentDescription
	 */
	@Override
	public String getCharset() {
		return (String) getProperty(CHARSET);
	}

	/**
	 * @see IContentDescription
	 */
	@Override
	public Object getProperty(QualifiedName key) {
		return contentTypeInfo.getDefaultProperty(key);
	}

	@Override
	public int hashCode() {
		return contentTypeInfo.getContentType().hashCode();
	}

	/**
	 * @see IContentDescription
	 */
	@Override
	public boolean isRequested(QualifiedName key) {
		return false;
	}

	/**
	 * @see IContentDescription
	 */
	@Override
	public void setProperty(QualifiedName key, Object value) {
		throw new IllegalStateException();
	}

	@Override
	public String toString() {
		return "{default} : " + contentTypeInfo.getContentType(); //$NON-NLS-1$
	}
}
