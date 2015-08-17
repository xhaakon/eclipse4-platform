/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ant.internal.core.ant;

import java.util.Arrays;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.DefaultExecutor;
import org.eclipse.ant.internal.core.IAntCoreConstants;

public class EclipseDefaultExecutor extends DefaultExecutor {

	private static final EclipseSingleCheckExecutor SUB_EXECUTOR = new EclipseSingleCheckExecutor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Executor#executeTargets(org.apache.tools.ant.Project, java.lang.String[])
	 */
	@Override
	public void executeTargets(Project project, String[] targetNames) throws BuildException {
		Vector<String> v = new Vector<String>();
		v.addAll(Arrays.asList(targetNames));
		project.addReference(IAntCoreConstants.TARGET_VECTOR_NAME, v);
		super.executeTargets(project, targetNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Executor#getSubProjectExecutor()
	 */
	@Override
	public Executor getSubProjectExecutor() {
		return SUB_EXECUTOR;
	}
}
