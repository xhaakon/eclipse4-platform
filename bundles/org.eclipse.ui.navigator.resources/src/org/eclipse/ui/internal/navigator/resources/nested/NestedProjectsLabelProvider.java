/*******************************************************************************
 * Copyright (c) 2014, 2015 Red Hat Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mickael Istria (Red Hat Inc.) - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.navigator.resources.nested;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class NestedProjectsLabelProvider extends LabelProvider {

	private WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();

	@Override
	public String getText(Object element) {
		if (! (element instanceof IProject)) {
			return null;
		}
		IProject project = (IProject)element;
		IPath location = project.getLocation();
		if (location != null && !location.lastSegment().equals(project.getName())) {
			return labelProvider.getText(element) + " (in " + location.lastSegment() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}
}
