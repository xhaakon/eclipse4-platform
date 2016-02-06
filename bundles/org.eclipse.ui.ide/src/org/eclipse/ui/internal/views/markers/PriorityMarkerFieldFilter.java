/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.views.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.markers.MarkerFieldFilter;
import org.eclipse.ui.views.markers.MarkerItem;
import org.eclipse.ui.views.markers.internal.ProblemFilter;

/**
 * PriorityMarkerFieldFilter is the field filter for priority in markers
 *
 * @since 3.4
 *
 */
public class PriorityMarkerFieldFilter extends CompatibilityFieldFilter {

	final static int PRIORITY_HIGH = 1 << IMarker.PRIORITY_HIGH;
	final static int PRIORITY_NORMAL = 1 << IMarker.PRIORITY_NORMAL;
	final static int PRIORITY_LOW = 1 << IMarker.PRIORITY_LOW;

	private static final String TAG_SELECTED_PRIORITIES = "selectedPriorities"; //$NON-NLS-1$

	/**
	 * Tag for the priority value.
	 */
	private static final String TAG_PRIORITY = "priority"; //$NON-NLS-1$

	int selectedPriorities = PRIORITY_HIGH + PRIORITY_LOW + PRIORITY_NORMAL;

	/**
	 * Create a new instance of the receiver
	 */
	public PriorityMarkerFieldFilter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.internal.provisional.views.markers.api.MarkerFieldFilter#loadSettings(org.eclipse.ui.IMemento)
	 */
	@Override
	public void loadSettings(IMemento memento) {
		Integer priority = memento.getInteger(TAG_SELECTED_PRIORITIES);
		if (priority == null)
			return;
		selectedPriorities = priority.intValue();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.views.markers.CompatibilityFieldFilter#loadLegacySettings(org.eclipse.ui.IMemento, org.eclipse.ui.internal.views.markers.MarkerContentGenerator)
	 */
	@Override
	void loadLegacySettings(IMemento memento, MarkerContentGenerator generator) {

		String setting = memento.getString(TAG_PRIORITY);

		if (setting != null) {
			try {
				selectedPriorities = Integer.parseInt(setting);
			} catch (NumberFormatException eNumberFormat) {
			}
		}



	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.views.markers.CompatibilityFieldFilter#initialize(org.eclipse.ui.views.markers.internal.ProblemFilter)
	 */
	@Override
	public void initialize(ProblemFilter problemFilter) {
		//There is no problem filter support for priority

	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.MarkerFieldFilter#saveSettings(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveSettings(IMemento memento) {
		memento.putInteger(TAG_SELECTED_PRIORITIES, selectedPriorities);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.MarkerFieldFilter#select(org.eclipse.ui.views.markers.MarkerItem)
	 */
	@Override
	public boolean select(MarkerItem item) {

		if (selectedPriorities == 0)
			return true;
		IMarker marker = item.getMarker();
		if (marker == null)
			return false;
		int markerPriority = 1 << marker.getAttribute(IMarker.PRIORITY,
				IMarker.PRIORITY_NORMAL);

		switch (markerPriority) {
		case PRIORITY_HIGH:
			return (selectedPriorities & PRIORITY_HIGH) > 0;
		case PRIORITY_NORMAL:
			return (selectedPriorities & PRIORITY_NORMAL) > 0;
		case PRIORITY_LOW:
			return (selectedPriorities & PRIORITY_LOW) > 0;

		default:
			return true;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.MarkerFieldFilter#populateWorkingCopy(org.eclipse.ui.views.markers.MarkerFieldFilter)
	 */
	@Override
	public void populateWorkingCopy(MarkerFieldFilter copy) {
		super.populateWorkingCopy(copy);
		((PriorityMarkerFieldFilter)copy).selectedPriorities = selectedPriorities;
	}
}
