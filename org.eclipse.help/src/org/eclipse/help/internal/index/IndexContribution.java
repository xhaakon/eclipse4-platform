/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.index;

import org.eclipse.help.IIndex;
import org.eclipse.help.IIndexContribution;

public class IndexContribution implements IIndexContribution {

	private String id;
	private IIndex index;
	private String locale;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public IIndex getIndex() {
		return index;
	}

	@Override
	public String getLocale() {
		return locale;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIndex(IIndex index) {
		this.index = index;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}
}
