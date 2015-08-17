/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ant.internal.core.ant;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;

public class EclipseAntMain extends Main {

	private Project fEclipseAntProject;

	public EclipseAntMain(Project eclipseAntProject) {
		super();
		fEclipseAntProject = eclipseAntProject;
	}

	public static void run(String[] args, Project eclipseAntProject) {
		Main projectHelpMain = new EclipseAntMain(eclipseAntProject);
		projectHelpMain.startAnt(args, null, null);
	}

	/*
	 * @see org.apache.tools.ant.Main#exit(int)
	 */
	@Override
	protected void exit(int exitCode) {
		// disallow system exit
	}

	/*
	 * @see org.apache.tools.ant.Main#addBuildListeners(org.apache.tools.ant.Project)
	 */
	@Override
	protected void addBuildListeners(Project project) {
		for (BuildListener listener : fEclipseAntProject.getBuildListeners()) {
			project.addBuildListener(listener);
		}
	}
}
