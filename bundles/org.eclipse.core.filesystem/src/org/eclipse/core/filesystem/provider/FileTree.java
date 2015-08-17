/*******************************************************************************
 * Copyright (c) 2005, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.filesystem.provider;

import org.eclipse.core.filesystem.*;

/**
 * The abstract superclass of all {@link IFileTree} implementations.  
 * <p>
 * Clients may subclass this class to provide a file tree for their particular
 * file system.
 * </p>
 * 
 * @since org.eclipse.core.filesystem 1.0
 */
public abstract class FileTree implements IFileTree {

	/**
	 * The root of the file tree
	 */
	protected IFileStore treeRoot;

	/**
	 * Creates a new file tree with tree root as the root
	 * @param treeRoot	the file store that is to act as the root of their FileTree
	 */
	public FileTree(IFileStore treeRoot) {
		this.treeRoot = treeRoot;
	}

	@Override
	public IFileStore getTreeRoot() {
		return treeRoot;
	}

	@Override
	public abstract IFileInfo[] getChildInfos(IFileStore store);

	@Override
	public abstract IFileInfo getFileInfo(IFileStore store);

	@Override
	public abstract IFileStore[] getChildStores(IFileStore store);
}
