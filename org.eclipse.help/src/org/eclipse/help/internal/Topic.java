/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal;

import org.eclipse.help.ICriteria;
import org.eclipse.help.ITopic;
import org.eclipse.help.ITopic2;
import org.w3c.dom.Element;

public class Topic extends UAElement implements ITopic2 {

	public static final String NAME = "topic"; //$NON-NLS-1$
	public static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	public static final String ATTRIBUTE_LABEL = "label"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	public static final String ATTRIBUTE_SORT= "sort"; //$NON-NLS-1$

	public Topic() {
		super(NAME);
	}

	public Topic(ITopic src) {
		super(NAME, src);
		setHref(src.getHref());
		setLabel(src.getLabel());
		appendChildren(src.getChildren());
	}

	@Override
	public String getIcon(){
		return getAttribute(ATTRIBUTE_ICON);
	}

	@Override
	public boolean isSorted(){
		return "true".equalsIgnoreCase(getAttribute(ATTRIBUTE_SORT)); //$NON-NLS-1$
	}

	public Topic(Element src) {
		super(src);
	}

	@Override
	public String getHref() {
		return getAttribute(ATTRIBUTE_HREF);
	}

	@Override
	public String getLabel() {
		return getAttribute(ATTRIBUTE_LABEL);
	}

	@Override
	public ITopic[] getSubtopics() {
		return getChildren(ITopic.class);
	}

	@Override
	public ICriteria[] getCriteria() {
		return getChildren(ICriteria.class);
	}

	public void setHref(String href) {
		setAttribute(ATTRIBUTE_HREF, href);
	}

	public void setLabel(String label) {
		setAttribute(ATTRIBUTE_LABEL, label);
	}

}
