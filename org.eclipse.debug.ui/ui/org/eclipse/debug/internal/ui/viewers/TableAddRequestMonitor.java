/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.viewers;


/**
 * @since 3.2
 *
 */
public class TableAddRequestMonitor extends AsynchronousRequestMonitor {
	
	protected Object[] fElements;

	/**
	 * @param node
	 * @param model
	 */
	TableAddRequestMonitor(ModelNode parent, Object elements[], AsynchronousModel model) {
		super(parent, model);
		fElements = elements;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.viewers.AsynchronousRequestMonitor#performUpdate()
	 */
	@Override
	protected void performUpdate() {
		((AsynchronousTableModel)getModel()).added(fElements);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.viewers.AsynchronousRequestMonitor#contains(org.eclipse.debug.internal.ui.viewers.model.provisional.viewers.AsynchronousRequestMonitor)
	 */
	@Override
	protected boolean contains(AsynchronousRequestMonitor update) {
		return false;
	}

}
