/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.e4.migration;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.IWorkbenchConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPlugin;

@SuppressWarnings("restriction")
public class ApplicationBuilder {

	@Inject
	private MApplication application;

	@Inject
	private WorkbenchMementoReader reader;

	@Inject
	private IModelBuilderFactory builderFactory;

	@Inject
	@Preference(nodePath = "org.eclipse.ui.workbench")
	private IEclipsePreferences preferences;

	private Map<String, String> minMaxPersistedState;

	void createApplication() {
		List<MWindow> windows = application.getChildren();
		for (WindowReader windowReader : reader.getWindowReaders()) {
			WindowBuilder windowBuilder = builderFactory.createWindowBuilder(windowReader);
			MWindow window = windowBuilder.createWindow();
			windows.add(window);
			if (windowBuilder.isSelected()) {
				application.setSelectedElement(window);
			}
			Object list = window.getTransientData().remove(WindowBuilder.PERSPECTIVES);
			if (list instanceof List<?>) {
				List<MPerspective> perspectiveList = (List<MPerspective>) list;
				for (MPerspective perspective : perspectiveList) {
					importToolbarsLocation(perspective);
				}
			}
		}
		addClosedPerspectives();
		addMRU();
	}

	private void addClosedPerspectives() {
		String perspProp = preferences.get(IWorkbenchConstants.TAG_PERSPECTIVES, null);
		if (perspProp == null) {
			return;
		}
		List<MUIElement> snippets = application.getSnippets();
		for (String perspName : perspProp.split(" ")) { //$NON-NLS-1$
			IMemento memento = null;
			try {
				memento = XMLMemento.createReadRoot(new StringReader(preferences.get(perspName + "_persp", ""))); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (WorkbenchException e) {
				WorkbenchPlugin.log("Loading custom perspective failed: " + perspName, e); //$NON-NLS-1$
			}
			snippets.add(builderFactory.createPerspectiveBuilder(new PerspectiveReader(memento)).createPerspective());
		}
	}

	private void addMRU() {
		application.getPersistedState().put(Workbench.MEMENTO_KEY,
				new MementoSerializer(reader.getMruMemento()).serialize());
	}

	private void importToolbarsLocation(MPerspective persp) {
		String trimsData = persp.getPersistedState().get("trims"); //$NON-NLS-1$
		if (trimsData == null || trimsData.trim().isEmpty()) {
			return;
		}
		persp.getPersistedState().remove("trims"); //$NON-NLS-1$
		Map<String, String> minMaxPersState = getMinMaxPersistedState();
		if (minMaxPersState == null) {
			return;
		}
		minMaxPersState.put(persp.getElementId(), trimsData);
	}

	private Map<String, String> getMinMaxPersistedState() {
		if (minMaxPersistedState != null) {
			return minMaxPersistedState;
		}
		for (MAddon addon : application.getAddons()) {
			if ("MinMax Addon".equals(addon.getElementId())) { //$NON-NLS-1$
				minMaxPersistedState = addon.getPersistedState();
				break;
			}
		}
		return minMaxPersistedState;
	}
}
