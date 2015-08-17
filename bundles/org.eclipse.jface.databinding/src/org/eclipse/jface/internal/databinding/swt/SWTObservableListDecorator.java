/*******************************************************************************
 * Copyright (c) 2008, 2009 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 *     Matthew Hall - bugs 190881, 264286, 281723
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.swt;

import org.eclipse.core.databinding.observable.list.DecoratingObservableList;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.swt.ISWTObservableList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * @since 3.3
 *
 */
public class SWTObservableListDecorator extends DecoratingObservableList
		implements ISWTObservableList {
	private Widget widget;

	/**
	 * @param decorated
	 * @param widget
	 */
	public SWTObservableListDecorator(IObservableList decorated, Widget widget) {
		super(decorated, true);
		this.widget = widget;
		WidgetListenerUtil.asyncAddListener(widget, SWT.Dispose,
				disposeListener);
	}

	private Listener disposeListener = new Listener() {
		@Override
		public void handleEvent(Event event) {
			SWTObservableListDecorator.this.dispose();
		}
	};

	@Override
	public synchronized void dispose() {
		WidgetListenerUtil.asyncRemoveListener(widget, SWT.Dispose,
				disposeListener);
		this.widget = null;
		super.dispose();
	}

	/**
	 * @return Returns the widget.
	 */
	@Override
	public Widget getWidget() {
		return widget;
	}
}
