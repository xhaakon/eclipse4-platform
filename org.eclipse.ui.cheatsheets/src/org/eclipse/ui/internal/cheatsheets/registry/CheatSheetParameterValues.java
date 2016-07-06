/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.cheatsheets.registry;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.commands.IParameterValues;

/**
 * Provides the parameter values for the open cheat sheet command.
 *
 * @since 3.2
 */
public class CheatSheetParameterValues implements IParameterValues {

	@Override
	public Map<String, String> getParameterValues() {
		Map<String, String> values = new TreeMap<>();

		CheatSheetCollectionElement cheatSheetCollection = CheatSheetRegistryReader
				.getInstance().getCheatSheets();
		populateValues(values, cheatSheetCollection);

		return values;
	}

	private void populateValues(Map<String, String> values,
			CheatSheetCollectionElement cheatSheetCollection) {

		Object[] cheatsheets = cheatSheetCollection.getCheatSheets();
		for (int i = 0; i < cheatsheets.length; i++) {
			Object cheatsheet = cheatsheets[i];
			if (cheatsheet instanceof CheatSheetElement) {
				CheatSheetElement element = (CheatSheetElement) cheatsheet;
				values.put(element.getLabel(null), element.getID());
			}
		}

		Object[] children = cheatSheetCollection.getChildren();
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			if (child instanceof CheatSheetCollectionElement) {
				populateValues(values, (CheatSheetCollectionElement) child);
			}
		}
	}

}
