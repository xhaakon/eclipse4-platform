/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.workbench.texteditor.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.ui.workbench.texteditor.tests.revisions.ChangeRegionTest;
import org.eclipse.ui.workbench.texteditor.tests.revisions.HunkComputerTest;
import org.eclipse.ui.workbench.texteditor.tests.revisions.RangeTest;
import org.eclipse.ui.workbench.texteditor.tests.rulers.RulerTestSuite;


/**
 * Test Suite for org.eclipse.ui.workbench.texteditor.
 *
 * @since 3.0
 */
public class WorkbenchTextEditorTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test Suite for org.eclipse.ui.workbench.texteditor"); //$NON-NLS-1$
		//$JUnit-BEGIN$
		suite.addTest(FindReplaceDialogTest.suite());
        suite.addTest(HippieCompletionTest.suite());
        suite.addTest(RangeTest.suite());
        suite.addTest(ChangeRegionTest.suite());
        suite.addTest(RulerTestSuite.suite());
        suite.addTest(HunkComputerTest.suite());
        suite.addTest(ScreenshotTest.suite());

		//$JUnit-END$
		return suite;
	}
}
