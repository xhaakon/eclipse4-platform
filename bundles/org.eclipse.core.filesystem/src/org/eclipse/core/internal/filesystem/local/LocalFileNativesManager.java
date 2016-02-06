/*******************************************************************************
 * Copyright (c) 2010, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sergey Prigogin (Google) - [458005] Provide a mechanism for disabling file system natives so that Java 7 filesystem.java7 classes can be tested
 *******************************************************************************/
package org.eclipse.core.internal.filesystem.local;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.internal.filesystem.local.unix.UnixFileHandler;
import org.eclipse.core.internal.filesystem.local.unix.UnixFileNatives;

/**
 * Dispatches methods backed by native code to the appropriate platform specific 
 * implementation depending on a library provided by a fragment. Failing this it tries
 * to use Java 7 NIO/2 API's (if they are available).
 * <p>
 * Use of native libraries can be disabled by adding -Declipse.filesystem.useNatives=false to VM
 * arguments.
 */
public class LocalFileNativesManager {
	private static final NativeHandler DEFAULT = new NativeHandler() {
		@Override
		public boolean putFileInfo(String fileName, IFileInfo info, int options) {
			return false;
		}

		@Override
		public int getSupportedAttributes() {
			return 0;
		}

		@Override
		public FileInfo fetchFileInfo(String fileName) {
			return new FileInfo();
		}
	};

	private static NativeHandler DELEGATE = DEFAULT;

	static {
		boolean nativesAllowed = Boolean.valueOf(System.getProperty("eclipse.filesystem.useNatives", "true")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
		if (nativesAllowed && UnixFileNatives.isUsingNatives()) {
			DELEGATE = new UnixFileHandler();
		} else if (nativesAllowed && LocalFileNatives.isUsingNatives()) {
			DELEGATE = new LocalFileHandler();
		} else {
			try {
				Class<?> c = LocalFileNativesManager.class.getClassLoader().loadClass("org.eclipse.core.internal.filesystem.java7.HandlerFactory"); //$NON-NLS-1$
				DELEGATE = (NativeHandler) c.getMethod("getHandler", (Class<?>) null).invoke(null, (Object) null); //$NON-NLS-1$
			} catch (ClassNotFoundException e) {
				// Class was missing?
				// Leave the delegate as default
			} catch (LinkageError e) {
				// Maybe the bundle was somehow loaded, the class was there but the bytecodes were the wrong version?
				// Leave the delegate as default
			} catch (IllegalAccessException e) {
				// We could not instantiate the object because we have no access
				// Leave delegate as default
			} catch (ClassCastException e) {
				// The handler does not inherit from the correct class
				// Leave delegate as default
			} catch (InvocationTargetException e) {
				// Exception was thrown from the getHandler method
				// Leave delegate as default
			} catch (NoSuchMethodException e) {
				// The getHandler method was not found
				// Leave delegate as default
			}
		}
	}

	public static int getSupportedAttributes() {
		return DELEGATE.getSupportedAttributes();
	}

	public static FileInfo fetchFileInfo(String fileName) {
		return DELEGATE.fetchFileInfo(fileName);
	}

	public static boolean putFileInfo(String fileName, IFileInfo info, int options) {
		return DELEGATE.putFileInfo(fileName, info, options);
	}

	public static boolean isUsingNatives() {
		return DELEGATE != DEFAULT;
	}
}
