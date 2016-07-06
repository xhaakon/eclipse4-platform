/*******************************************************************************
 * Copyright (c) 2008, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.views.markers;

import org.eclipse.swt.widgets.Composite;

/**
 * {@link ProblemsSeverityAndDescriptionConfigurationArea} is the
 * configuration area for the problems view.
 * @since 3.4
 *
 */
public class ProblemsSeverityAndDescriptionConfigurationArea extends
		SeverityAndDescriptionConfigurationArea {

	@Override
	public void createContents(Composite parent) {

		super.createContents(parent);
		createSeverityGroup(parent);

	}

}
