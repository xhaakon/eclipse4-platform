/**********************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.core.externaltools.internal.model;

import org.eclipse.osgi.util.NLS;

public class ExternalToolsModelMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.core.externaltools.internal.model.ExternalToolsModelMessages";//$NON-NLS-1$
    
	public static String ExternalToolBuilder_Running__0_____1;
	public static String ExternalToolBuilder_0;
	
	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, ExternalToolsModelMessages.class);
	}
}