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
package org.eclipse.debug.internal.ui.sourcelookup;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.debug.core.sourcelookup.containers.ZipEntryStorage;
import org.eclipse.debug.internal.core.IInternalDebugCoreConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Workbench adapter for source elements.
 * 
 * @since 3.0
 */
public class SourceElementWorkbenchAdapter implements IWorkbenchAdapter {
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object o) {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(Object o) {
		if (o instanceof LocalFileStorage || o instanceof ZipEntryStorage) {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	@Override
	public String getLabel(Object o) {
		if (o instanceof LocalFileStorage) {
			LocalFileStorage storage = (LocalFileStorage) o;
			IPath path = storage.getFullPath();
			return getQualifiedName(path);
		}
		if (o instanceof ZipEntryStorage) {
			ZipEntryStorage storage = (ZipEntryStorage)o;
			StringBuffer buffer = new StringBuffer();
			buffer.append(storage.getZipEntry().getName());
			buffer.append(" - "); //$NON-NLS-1$
			buffer.append(storage.getArchive().getName());
			return buffer.toString();
		}
		return IInternalDebugCoreConstants.EMPTY_STRING;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object o) {
		return null;
	}
	
	public static String getQualifiedName(IPath path) {
		StringBuffer buffer = new StringBuffer();
		String[] segments = path.segments();
		if (segments.length > 0) {
			buffer.append(path.lastSegment());
			if (segments.length > 1) {
				buffer.append(" - "); //$NON-NLS-1$
				if (path.getDevice() != null) {
					buffer.append(path.getDevice());	
				}
				for (int i = 0; i < segments.length - 1; i++) {
					buffer.append(File.separatorChar);
					buffer.append(segments[i]);
				}
			}
			return buffer.toString();
		}
		return IInternalDebugCoreConstants.EMPTY_STRING;
	}
}
