/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.workbench.texteditor.tests.rulers;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @since 3.0
 */
public class RulerTestSuite {

	public static Test suite() {
		TestSuite suite= new TestSuite("Test for org.eclipse.ui.internal.texteditor.rulers");
		//$JUnit-BEGIN$
		suite.addTestSuite(DAGTest.class);
		//$JUnit-END$
		return suite;
	}
}
