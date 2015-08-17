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
 * CompletionFieldFilter is the field filter for marker fields.
 *
 * @since 3.4
 *
 */
public class CompletionFieldFilter extends CompatibilityFieldFilter {

	final static int COMPLETED = 2;
	final static int NOT_COMPLETED = 1;
	private static int ALL_SELECTED = COMPLETED + NOT_COMPLETED;
	private int completion = ALL_SELECTED;
	private static String COMPLETION_ATTRIBUTE = "completion"; //$NON-NLS-1$
	/**
	 * Tag for the done value.
	 */
	private static final String TAG_DONE = "done"; //$NON-NLS-1$

	/**
	 * Create a new instance of the receiver.
	 */
	public CompletionFieldFilter() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.provisional.views.markers.api.MarkerFieldFilter#loadSettings(org.eclipse.ui.IMemento)
	 */
	@Override
	public void loadSettings(IMemento memento) {
		Integer completionValue = memento.getInteger(COMPLETION_ATTRIBUTE);
		if (completionValue == null)
			return;
		completion = completionValue.intValue();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.views.markers.CompatibilityFieldFilter#loadLegacySettings(org.eclipse.ui.IMemento, org.eclipse.ui.internal.views.markers.MarkerContentGenerator)
	 */
	@Override
	void loadLegacySettings(IMemento memento, MarkerContentGenerator generator) {

		String setting = memento.getString(TAG_DONE);

		if (setting != null) {
			completion = Boolean.valueOf(setting).booleanValue() ? COMPLETED : NOT_COMPLETED;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.views.markers.CompatibilityFieldFilter#initialize(org.eclipse.ui.views.markers.internal.ProblemFilter)
	 */
	@Override
	public void initialize(ProblemFilter problemFilter) {
		//Problem filters have no completion value

	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.MarkerFieldFilter#saveSettings(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveSettings(IMemento memento) {
		memento.putInteger(COMPLETION_ATTRIBUTE, completion);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.MarkerFieldFilter#select(org.eclipse.ui.views.markers.MarkerItem)
	 */
	@Override
	public boolean select(MarkerItem item) {

		if (completion == ALL_SELECTED)
			return true;

		if (item.getAttributeValue(IMarker.USER_EDITABLE, true)) {
			if (item.getAttributeValue(IMarker.DONE, false))
				return (completion & COMPLETED) > 0;
			return (completion & NOT_COMPLETED) > 0;
		}

		return false;

	}

	/**
	 * Get the completion settings.
	 * @return int
	 * @see #COMPLETED
	 * @see #NOT_COMPLETED
	 */
	int getCompletion() {
		return completion;
	}

	/**
	 * Set the completion settings.
	 * @param completion the completion value
	 * @see #COMPLETED
	 * @see #NOT_COMPLETED
	 */
	void setCompletion(int completion) {
		this.completion = completion;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.markers.MarkerFieldFilter#populateWorkingCopy(org.eclipse.ui.views.markers.MarkerFieldFilter)
	 */
	@Override
	public void populateWorkingCopy(MarkerFieldFilter copy) {
		super.populateWorkingCopy(copy);
		((CompletionFieldFilter)copy).setCompletion(getCompletion());
	}

}
