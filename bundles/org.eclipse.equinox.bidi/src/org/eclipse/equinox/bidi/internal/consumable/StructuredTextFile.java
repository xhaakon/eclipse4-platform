/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.equinox.bidi.internal.consumable;

import org.eclipse.equinox.bidi.custom.StructuredTextTypeHandler;

/**
 *  Handler adapted to processing directory and file paths.
 */
public class StructuredTextFile extends StructuredTextTypeHandler {

	public StructuredTextFile() {
		super(":/\\."); //$NON-NLS-1$
	}
}
