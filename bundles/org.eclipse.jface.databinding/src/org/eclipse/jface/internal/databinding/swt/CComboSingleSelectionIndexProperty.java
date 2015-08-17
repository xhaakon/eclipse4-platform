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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;

/**
 * @since 3.3
 *
 */
public class CComboSingleSelectionIndexProperty extends SingleSelectionIndexProperty {
	/**
	 *
	 */
	public CComboSingleSelectionIndexProperty() {
		super(new int[] { SWT.Selection, SWT.DefaultSelection });
	}

	@Override
	int doGetIntValue(Object source) {
		return ((CCombo) source).getSelectionIndex();
	}

	@Override
	void doSetIntValue(Object source, int value) {
		if (value == -1)
			((CCombo) source).deselectAll();
		else
			((CCombo) source).select(value);
	}

	@Override
	public String toString() {
		return "CCombo.selectionIndex <int>"; //$NON-NLS-1$
	}
}
