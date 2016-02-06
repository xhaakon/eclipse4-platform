/**********************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.ui.externaltools.internal.variables;

import org.eclipse.osgi.util.NLS;

public class VariableMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.ui.externaltools.internal.variables.VariableMessages";//$NON-NLS-1$

	public static String BuildProjectResolver_3;
	public static String SystemPathResolver_0;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, VariableMessages.class);
	}
}