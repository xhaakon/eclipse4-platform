/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.text.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.tests.reconciler.ReconcilerTestSuite;
import org.eclipse.jface.text.tests.rules.RulesTestSuite;


/**
 * Test Suite for org.eclipse.jface.text.
 *
 * @since 3.0
 */
public class JFaceTextTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite= new TestSuite("Test Suite for org.eclipse.jface.text"); //$NON-NLS-1$
		suite.addTest(HTML2TextReaderTest.suite());
		suite.addTest(TextHoverPopupTest.suite());
		suite.addTest(TextPresentationTest.suite());
		suite.addTest(DefaultUndoManagerTest.suite());
		suite.addTest(TextViewerTest.suite());
		suite.addTest(TextViewerUndoManagerTest.suite());
		suite.addTest(RulesTestSuite.suite());
		suite.addTest(ReconcilerTestSuite.suite());
		suite.addTest(DefaultPairMatcherTest.suite());
		suite.addTest(DefaultPairMatcherTest2.suite());

		return suite;
	}
}
