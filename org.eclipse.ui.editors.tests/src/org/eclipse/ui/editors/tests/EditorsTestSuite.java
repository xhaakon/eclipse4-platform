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
package org.eclipse.ui.editors.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test Suite for org.eclipse.ui.editors.
 *
 * @since 3.0
 */
public class EditorsTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test Suite for org.eclipse.ui.editors"); //$NON-NLS-1$
		//$JUnit-BEGIN$
		suite.addTest(ChainedPreferenceStoreTest.suite());
		suite.addTest(EncodingChangeTests.suite());
		suite.addTest(GotoLineTest.suite());
		suite.addTest(SegmentedModeTest.suite());
		suite.addTest(MarkerAnnotationOrderTest.suite());
		//$JUnit-END$
		return suite;
	}
}
