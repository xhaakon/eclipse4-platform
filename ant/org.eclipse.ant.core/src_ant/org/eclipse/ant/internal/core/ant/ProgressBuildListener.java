/*******************************************************************************
 *  Copyright (c) 2000, 2013 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ant.internal.core.ant;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.CallTarget;
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.ant.internal.core.IAntCoreConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Reports progress and checks for cancellation of a script execution.
 */
public class ProgressBuildListener implements BuildListener {

	protected Map<Project, ProjectMonitors> projects;
	protected Project mainProject;
	protected Project parentProject;
	private Thread currentTaskThread;

	/**
	 * Contains the progress monitor instances for the various projects in a chain.
	 */
	protected class ProjectMonitors {
		/**
		 * This field is null for the main project
		 */
		private Target mainTarget;
		private IProgressMonitor mainMonitor;
		private IProgressMonitor targetMonitor;
		private IProgressMonitor taskMonitor;

		protected IProgressMonitor getMainMonitor() {
			return mainMonitor;
		}

		protected Target getMainTarget() {
			return mainTarget;
		}

		protected IProgressMonitor getTargetMonitor() {
			return targetMonitor;
		}

		protected IProgressMonitor getTaskMonitor() {
			return taskMonitor;
		}

		protected void setMainMonitor(IProgressMonitor mainMonitor) {
			this.mainMonitor = mainMonitor;
		}

		protected void setMainTarget(Target mainTarget) {
			this.mainTarget = mainTarget;
		}

		protected void setTargetMonitor(IProgressMonitor targetMonitor) {
			this.targetMonitor = targetMonitor;
		}

		protected void setTaskMonitor(IProgressMonitor taskMonitor) {
			this.taskMonitor = taskMonitor;
		}

	}

	public ProgressBuildListener(Project project, List<String> targetNames, IProgressMonitor monitor) {
		projects = new HashMap<Project, ProjectMonitors>();
		mainProject = project;
		ProjectMonitors monitors = new ProjectMonitors();
		IProgressMonitor localmonitor = monitor;
		if (localmonitor == null) {
			localmonitor = new NullProgressMonitor();
		}
		monitors.setMainMonitor(localmonitor);
		projects.put(mainProject, monitors);
		ArrayList<Target> targets = new ArrayList<Target>(targetNames.size());
		for (String targetName : targetNames) {
			Target target = mainProject.getTargets().get(targetName);
			if (target != null) {
				targets.add(target);
			}
		}
		int work = computeWork(targets);
		monitors.getMainMonitor().beginTask(IAntCoreConstants.EMPTY_STRING, work);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void buildStarted(BuildEvent event) {
		checkCanceled();
	}

	protected int computeWork(List<Target> targets) {
		int result = 0;
		for (int i = 0; i < targets.size(); i++) {
			result = result + countTarget(targets.get(i), new ArrayList<String>());
		}
		return result;
	}

	protected int countTarget(Target target, List<String> alreadySeen) {
		int result = 1;
		Project project = target.getProject();
		Hashtable<String, Target> targets = project.getTargets();
		String targetName;
		Target dependency;
		for (Enumeration<String> dependencies = target.getDependencies(); dependencies.hasMoreElements();) {
			targetName = dependencies.nextElement();
			if (alreadySeen.contains(targetName)) { // circular dependency or common dependency
				return result;
			}
			alreadySeen.add(targetName);
			dependency = targets.get(targetName);
			if (dependency != null) {
				result = result + countTarget(dependency, alreadySeen);
			}
		}
		// we have to handle antcall tasks as well
		Task[] tasks = target.getTasks();
		for (int i = 0; i < tasks.length; i++) {
			if (tasks[i] instanceof CallTarget) {
				// As we do not have access to the information (at least in Ant 1.4.1)
				// describing what target is executed by this antcall task, we assume
				// a scenario where it depends on all targets of the project but itself.
				result = result + (targets.size() - 1);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void buildFinished(BuildEvent event) {
		ProjectMonitors monitors = projects.get(mainProject);
		monitors.getMainMonitor().done();
		Set<Project> keys = projects.keySet();
		Iterator<Project> itr = keys.iterator();
		while (itr.hasNext()) {
			Project project = itr.next();
			project.removeBuildListener(this);
			project.getReferences().remove(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void targetStarted(BuildEvent event) {
		checkCanceled();
		Project currentProject = event.getProject();
		if (currentProject == null) {
			return;
		}
		Target target = event.getTarget();
		ProjectMonitors monitors = projects.get(currentProject);

		// if monitors is null we are in a new script
		if (monitors == null) {
			monitors = createMonitors(currentProject, target);
		}

		monitors.setTargetMonitor(subMonitorFor(monitors.getMainMonitor(), 1));
		int work = (target != null) ? target.getTasks().length : 100;
		monitors.getTargetMonitor().beginTask(IAntCoreConstants.EMPTY_STRING, work);
	}

	protected ProjectMonitors createMonitors(Project currentProject, Target target) {
		ProjectMonitors monitors = new ProjectMonitors();
		// remember the target so we can remove this monitors object later
		monitors.setMainTarget(target);
		ArrayList<Target> targets = new ArrayList<Target>(1);
		targets.add(target);
		int work = computeWork(targets);
		ProjectMonitors parentMonitors = null;
		if (parentProject == null) {
			parentMonitors = projects.get(mainProject);
			monitors.setMainMonitor(subMonitorFor(parentMonitors.getMainMonitor(), 1));
		} else {
			parentMonitors = projects.get(parentProject);
			parentProject = null;
			monitors.setMainMonitor(subMonitorFor(parentMonitors.getTaskMonitor(), 1));
		}
		monitors.getMainMonitor().beginTask(IAntCoreConstants.EMPTY_STRING, work);
		projects.put(currentProject, monitors);
		return monitors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void targetFinished(BuildEvent event) {
		checkCanceled();
		Project currentProject = event.getProject();
		if (currentProject == null) {
			return;
		}
		ProjectMonitors monitors = projects.get(currentProject);
		if (monitors == null) {
			return;
		}
		monitors.getTargetMonitor().done();
		// if this is not the main project test if we are done with this project
		if ((currentProject != mainProject) && (monitors.getMainTarget() == event.getTarget())) {
			monitors.getMainMonitor().done();
			projects.remove(currentProject);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void taskStarted(BuildEvent event) {
		checkCanceled();
		Project currentProject = event.getProject();
		if (currentProject == null) {
			return;
		}
		currentProject.getReferences().remove(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
		ProjectMonitors monitors = projects.get(currentProject);
		if (monitors == null) {
			return;
		}
		Task task = event.getTask();
		if (task == null) {
			return;
		}
		currentTaskThread = Thread.currentThread();
		monitors.setTaskMonitor(subMonitorFor(monitors.getTargetMonitor(), 1));
		monitors.getTaskMonitor().beginTask(IAntCoreConstants.EMPTY_STRING, 1);
		// If this script is calling another one, track the project chain.
		if (task instanceof Ant) {
			parentProject = currentProject;
		} else {
			currentProject.addReference(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR, monitors.getTaskMonitor());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void taskFinished(BuildEvent event) {
		checkCanceled();
		Project project = event.getProject();
		if (project == null) {
			return;
		}
		project.getReferences().remove(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
		ProjectMonitors monitors = projects.get(project);
		if (monitors == null) {
			return;
		}
		monitors.getTaskMonitor().done();
		currentTaskThread = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
	 */
	@Override
	public void messageLogged(BuildEvent event) {
		checkCanceled();
	}

	protected void checkCanceled() {
		// only cancel if the current task thread matches the current thread
		// do not want to throw an exception in a separate thread or process
		// see bug 32657
		if (currentTaskThread != null && currentTaskThread != Thread.currentThread()) {
			return;
		}
		ProjectMonitors monitors = projects.get(mainProject);
		if (monitors.getMainMonitor().isCanceled()) {
			currentTaskThread = null;
			throw new OperationCanceledException(InternalAntMessages.ProgressBuildListener_Build_cancelled);
		}
	}

	protected IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks) {
		if (monitor == null) {
			return new NullProgressMonitor();
		}
		if (monitor instanceof NullProgressMonitor) {
			return monitor;
		}
		return new SubProgressMonitor(monitor, ticks);
	}
}
