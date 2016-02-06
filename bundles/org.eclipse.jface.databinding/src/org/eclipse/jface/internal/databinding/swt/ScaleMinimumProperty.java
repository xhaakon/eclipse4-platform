/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 194734)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.swt.widgets.Scale;

/**
 * @since 3.3
 *
 */
public class ScaleMinimumProperty extends WidgetIntValueProperty {
	@Override
	int doGetIntValue(Object source) {
		return ((Scale) source).getMinimum();
	}

	@Override
	void doSetIntValue(Object source, int value) {
		((Scale) source).setMinimum(value);
	}

	@Override
	public String toString() {
		return "Scale.minimum <int>"; //$NON-NLS-1$
	}
}
