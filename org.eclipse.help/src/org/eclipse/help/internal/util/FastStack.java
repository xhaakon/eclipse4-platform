/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.util;

import java.util.ArrayList;

/**
 * Fast Stack is similar to {@link java.util.Stack}, but simplified for speed.
 * It uses ArrayList as an underlying collection. The methods in this class are
 * not thread safe.
 */
public class FastStack<T> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	private int last = -1;

	public FastStack() {
		super();
	}

	public final T push(T item) {
		super.add(item);
		last++;
		return item;
	}

	public final T pop() {
		return super.remove(last--);
	}

	public final T peek() {
		return super.get(last);
	}

	public final boolean empty() {
		return last < 0;
	}
}
