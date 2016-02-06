/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     James Blackburn (Broadcom Corp.) - ongoing development
 *******************************************************************************/
package org.eclipse.core.internal.resources.mapping;

import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

/**
 * Concrete implementation of IResourceDelta used for operation validation
 */
public final class ProposedResourceDelta extends PlatformObject implements IResourceDelta {
	protected static int KIND_MASK = 0xFF;

	private HashMap<String, ProposedResourceDelta> children = new HashMap<String, ProposedResourceDelta>(8);
	private IPath movedFromPath;
	private IPath movedToPath;
	private IResource resource;
	private int status;

	public ProposedResourceDelta(IResource resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#accept(org.eclipse.core.resources.IResourceDeltaVisitor)
	 */
	@Override
	public void accept(IResourceDeltaVisitor visitor) throws CoreException {
		accept(visitor, 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#accept(org.eclipse.core.resources.IResourceDeltaVisitor, boolean)
	 */
	@Override
	public void accept(IResourceDeltaVisitor visitor, boolean includePhantoms) throws CoreException {
		accept(visitor, includePhantoms ? IContainer.INCLUDE_PHANTOMS : 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#accept(org.eclipse.core.resources.IResourceDeltaVisitor, int)
	 */
	@Override
	public void accept(IResourceDeltaVisitor visitor, int memberFlags) throws CoreException {
		if (!visitor.visit(this))
			return;
		for (Iterator<ProposedResourceDelta> iter = children.values().iterator(); iter.hasNext();) {
			ProposedResourceDelta childDelta = iter.next();
			childDelta.accept(visitor, memberFlags);
		}
	}

	/**
	 * Adds a child delta to the list of children for this delta node.
	 * @param delta
	 */
	protected void add(ProposedResourceDelta delta) {
		if (children.size() == 0 && status == 0)
			setKind(IResourceDelta.CHANGED);
		children.put(delta.getResource().getName(), delta);
	}

	/**
	 * Adds the given flags to this delta.
	 * @param flags The flags to add
	 */
	protected void addFlags(int flags) {
		//make sure the provided flags don't influence the kind
		this.status |= (flags & ~KIND_MASK);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#findMember(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public IResourceDelta findMember(IPath path) {
		int segmentCount = path.segmentCount();
		if (segmentCount == 0)
			return this;

		//iterate over the path and find matching child delta
		ProposedResourceDelta current = this;
		for (int i = 0; i < segmentCount; i++) {
			current = current.children.get(path.segment(i));
			if (current == null)
				return null;
		}
		return current;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getAffectedChildren()
	 */
	@Override
	public IResourceDelta[] getAffectedChildren() {
		return getAffectedChildren(ADDED | REMOVED | CHANGED, IResource.NONE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getAffectedChildren(int)
	 */
	@Override
	public IResourceDelta[] getAffectedChildren(int kindMask) {
		return getAffectedChildren(kindMask, IResource.NONE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getAffectedChildren(int, int)
	 */
	@Override
	public IResourceDelta[] getAffectedChildren(int kindMask, int memberFlags) {
		List<ProposedResourceDelta> result = new ArrayList<ProposedResourceDelta>();
		for (Iterator<ProposedResourceDelta> iter = children.values().iterator(); iter.hasNext();) {
			ProposedResourceDelta child = iter.next();
			if ((child.getKind() & kindMask) != 0)
				result.add(child);
		}
		return result.toArray(new IResourceDelta[result.size()]);
	}

	/**
	 * Returns the child delta corresponding to the given child resource name,
	 * or <code>null</code>.
	 */
	ProposedResourceDelta getChild(String name) {
		return children.get(name);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getFlags()
	 */
	@Override
	public int getFlags() {
		return status & ~KIND_MASK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getFullPath()
	 */
	@Override
	public IPath getFullPath() {
		return getResource().getFullPath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getKind()
	 */
	@Override
	public int getKind() {
		return status & KIND_MASK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getMarkerDeltas()
	 */
	@Override
	public IMarkerDelta[] getMarkerDeltas() {
		return new IMarkerDelta[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getMovedFromPath()
	 */
	@Override
	public IPath getMovedFromPath() {
		return movedFromPath;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getMovedToPath()
	 */
	@Override
	public IPath getMovedToPath() {
		return movedToPath;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getProjectRelativePath()
	 */
	@Override
	public IPath getProjectRelativePath() {
		return getResource().getProjectRelativePath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDelta#getResource()
	 */
	@Override
	public IResource getResource() {
		return resource;
	}

	public void setFlags(int flags) {
		status = getKind() | (flags & ~KIND_MASK);
	}

	protected void setKind(int kind) {
		status = getFlags() | (kind & KIND_MASK);
	}

	protected void setMovedFromPath(IPath path) {
		movedFromPath = path;
	}

	protected void setMovedToPath(IPath path) {
		movedToPath = path;
	}

	/**
	 * For debugging purposes only.
	 */
	@Override
	public String toString() {
		return "ProposedDelta(" + resource + ')'; //$NON-NLS-1$
	}
}
