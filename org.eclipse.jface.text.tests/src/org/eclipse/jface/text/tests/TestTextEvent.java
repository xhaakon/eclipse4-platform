/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.tests;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.TextEvent;


public class TestTextEvent extends TextEvent {

	TestTextEvent(DocumentEvent event, String replacedText) {
		super(event.getOffset(), event.getLength(), event.getText(), replacedText, event, true);
	}

	TestTextEvent(String text) {
		super(0, 0, text, (String) null, (DocumentEvent) null, true);
	}
}
