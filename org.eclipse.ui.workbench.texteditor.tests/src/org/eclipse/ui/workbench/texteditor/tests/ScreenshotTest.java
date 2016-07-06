/*******************************************************************************
 * Copyright (c) 2012, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.workbench.texteditor.tests;

import java.io.File;
import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.core.runtime.Platform;

import org.eclipse.jface.util.Util;

import org.eclipse.ui.PlatformUI;

public class ScreenshotTest {

	@Rule
	public TestName testName = new TestName();

	@Test
	public void testScreenshot() throws Exception {
		takeScreenshot(ScreenshotTest.class, testName.getMethodName(), System.out);
	}

	@Test
	public void testWindowsTaskManagerScreenshots() throws Exception {
		if (! Util.isWindows())
			return;
		
		Display display= Display.getDefault();
		
        Event event= new Event();
        event.type= SWT.KeyDown;
        event.keyCode= SWT.CTRL;
        System.out.println("* CTRL " + display.post(event));
        event.keyCode= SWT.SHIFT;
        System.out.println("* SHIFT " + display.post(event));
        event.character= SWT.ESC;
        event.keyCode= SWT.ESC;
        System.out.println("* ESC " + display.post(event));
        
        event.type= SWT.KeyUp;
        System.out.println("* ESC up " + display.post(event));
        event.character= 0;
        event.keyCode= SWT.SHIFT;
        System.out.println("* SHIFT up " + display.post(event));
        event.keyCode= SWT.CTRL;
        System.out.println("* CTRL up " + display.post(event));
        
        runEventQueue();
        takeScreenshot(ScreenshotTest.class, testName.getMethodName() + 2, System.out);
        
        event.type= SWT.KeyDown;
        event.character= SWT.ESC;
        event.keyCode= SWT.ESC;
        System.out.println("* ESC " + display.post(event));
        event.type= SWT.KeyUp;
        System.out.println("* ESC up " + display.post(event));
        
        runEventQueue();
        takeScreenshot(ScreenshotTest.class, testName.getMethodName() + 3, System.out);
	}
	
	/**
	 * Takes a screenshot and dumps other debugging information to the given stream.
	 * 
	 * @param testClass test class that takes the screenshot
	 * @param name screenshot identifier (e.g. test name)
	 * @param out print stream to use for diagnostics.
	 * @return file system path to the screenshot file
	 */
	public static String takeScreenshot(Class<?> testClass, String name, PrintStream out) {
		File resultsHtmlDir= getJunitReportOutput(); // ends up in testresults/linux.gtk.x86_6.0/<class>.<test>.png
		
		if (resultsHtmlDir == null) { // Fallback. Warning: uses same file location on all test platforms:
			File eclipseDir= new File("").getAbsoluteFile(); // eclipse-testing/test-eclipse/eclipse
			if (isRunByGerritHudsonJob())
				resultsHtmlDir= new File(eclipseDir, "/../").getAbsoluteFile(); // ends up in the workspace root
			else
				resultsHtmlDir= new File(eclipseDir, "../../results/html/").getAbsoluteFile(); // ends up in testresults/html/<class>.<test>.png
		}
		
		Display display= PlatformUI.getWorkbench().getDisplay();
		
		// Wiggle the mouse:
		Event mouseMove= new Event();
		mouseMove.x= 10;
		mouseMove.y= 10;
		display.post(mouseMove);
		runEventQueue();
		mouseMove.x= 20;
		mouseMove.y= 20;
		display.post(mouseMove);
		runEventQueue();
		
		// Dump focus control, parents, and shells:
		Control focusControl = display.getFocusControl();
		out.println("FocusControl: ");
		if (focusControl == null) {
			System.out.println("  null!");
		} else {
			StringBuffer indent = new StringBuffer("  ");
			do {
				out.println(indent.toString() + focusControl);
				focusControl = focusControl.getParent();
				indent.append("  ");
			} while (focusControl != null);
		}
		Shell[] shells = display.getShells();
		if (shells.length > 0) {
			out.println("Shells: ");
			for (int i = 0; i < shells.length; i++) {
				Shell shell = shells[i];
				out.print(display.getActiveShell() == shell ? "  active, " : "  inactive, ");
				out.print((shell.isVisible() ? "visible: " : "invisible: ") + shell);
				out.println(" @ " + shell.getBounds().toString());
			}
		}
		
		// Take a screenshot:
		GC gc = new GC(display);
		Rectangle displayBounds= display.getBounds();
		out.println("Display @ " + displayBounds);
		final Image image = new Image(display, displayBounds);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		resultsHtmlDir.mkdirs();
		String filename = new File(
				resultsHtmlDir.getAbsolutePath(), 
				testClass.getName() + "." + name + ".png").getAbsolutePath();
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] { image.getImageData() };
		loader.save(filename, SWT.IMAGE_PNG);
		out.println("Screenshot saved to: " + filename);
		image.dispose();
		return filename;
	}

	private static File getJunitReportOutput() {
		String[] args= Platform.getCommandLineArgs();
		for (int i= 0; i < args.length - 1; i++) {
			if ("-junitReportOutput".equals(args[i])) { // see library.xml and org.eclipse.test.EclipseTestRunner
				return new File(args[i + 1]).getAbsoluteFile();
			}
		}
		return null;
	}

	public static boolean isRunByGerritHudsonJob() {
		return System.getProperty("user.dir").indexOf("eclipse.platform.text-Gerrit") != -1;
	}

	private static void runEventQueue() {
		Display display= PlatformUI.getWorkbench().getDisplay();
		for (int i= 0; i < 10; i++) { // workaround for https://bugs.eclipse.org/323272
			while (display.readAndDispatch()) {
				// do nothing
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
}
