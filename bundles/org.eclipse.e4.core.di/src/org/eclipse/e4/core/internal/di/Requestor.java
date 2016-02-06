/*******************************************************************************
 * Copyright (c) 2010, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.core.internal.di;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.AnnotatedElement;
import org.eclipse.e4.core.di.IInjector;
import org.eclipse.e4.core.di.annotations.GroupUpdates;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.suppliers.IObjectDescriptor;
import org.eclipse.e4.core.di.suppliers.IRequestor;
import org.eclipse.e4.core.di.suppliers.PrimaryObjectSupplier;

/**
 * @noextend This class is not intended to be subclassed by clients.
 */
abstract public class Requestor<L extends AnnotatedElement> implements IRequestor {

	/** The request location; may be null */
	final protected L location;
	final private WeakReference<Object> objectRef;
	final protected boolean track;
	final private boolean groupUpdates;
	final private boolean isOptional;
	// since objectRef is weak, must maintain object hashCode for stability
	final private int objectHashcode;

	final private IInjector injector;
	final protected PrimaryObjectSupplier primarySupplier;
	private PrimaryObjectSupplier tempSupplier;

	protected Object[] actualArgs;

	private IObjectDescriptor[] objectDescriptors;

	protected abstract IObjectDescriptor[] calcDependentObjects();

	public Requestor(L location, IInjector injector, PrimaryObjectSupplier primarySupplier, PrimaryObjectSupplier tempSupplier, Object requestingObject, boolean track) {
		this.location = location;
		this.injector = injector;
		this.primarySupplier = primarySupplier;
		this.tempSupplier = tempSupplier;
		if (requestingObject != null) {
			if (primarySupplier != null)
				objectRef = primarySupplier.makeReference(requestingObject);
			else
				objectRef = new WeakReference<Object>(requestingObject);
			objectHashcode = requestingObject.hashCode();
		} else {
			objectRef = null;
			objectHashcode = 0;
		}
		this.track = track;
		groupUpdates = (location == null) ? false : location.isAnnotationPresent(GroupUpdates.class);
		isOptional = (location == null) ? false : location.isAnnotationPresent(Optional.class);
	}

	/**
	 * Return the injection location described by this requestor
	 * @return the request location; may be {@code null}
	 */
	public L getLocation() {
		return location;
	}

	public IInjector getInjector() {
		return injector;
	}

	public PrimaryObjectSupplier getPrimarySupplier() {
		return primarySupplier;
	}

	public PrimaryObjectSupplier getTempSupplier() {
		return tempSupplier;
	}

	public void clearTempSupplier() {
		tempSupplier = null; // don't keep temporary suppliers in memory after initial processing
	}

	@Override
	public Object getRequestingObject() {
		if (objectRef == null)
			return null;
		return objectRef.get();
	}

	@Override
	public Class<?> getRequestingObjectClass() {
		Object object = getRequestingObject();
		if (object == null)
			return null;
		return object.getClass();
	}

	public Reference<Object> getReference() {
		return objectRef;
	}

	/**
	 * Determines if the requestor wants to be called whenever one of the dependent object changes.
	 */
	public boolean shouldTrack() {
		return track;
	}

	public boolean shouldGroupUpdates() {
		return groupUpdates;
	}

	public boolean isOptional() {
		return isOptional;
	}

	/**
	 * If actual arguments are resolved for this requestor
	 */
	public boolean isResolved() {
		return (actualArgs != null);
	}

	public void setResolvedArgs(Object[] actualArgs) {
		this.actualArgs = actualArgs;
	}

	@Override
	public boolean isValid() {
		return (getRequestingObject() != null);
	}

	@Override
	public void resolveArguments(boolean initial) {
		((InjectorImpl) injector).resolveArguments(this, initial);
	}

	@Override
	public void disposed(PrimaryObjectSupplier objectSupplier) {
		((InjectorImpl) injector).disposed(objectSupplier);
		// Clear the objectRef if the supplier is disposed see 430041
		if (objectSupplier == primarySupplier) {
			if (objectRef != null) {
				objectRef.clear();
			}
		}
	}

	@Override
	public boolean uninject(Object object, PrimaryObjectSupplier objectSupplier) {
		Object originatingObject = getRequestingObject();
		if (originatingObject == null)
			return false;
		if (originatingObject != object)
			return true;
		injector.uninject(object, objectSupplier);
		return false;
	}

	public IObjectDescriptor[] getDependentObjects() {
		if (objectDescriptors == null)
			objectDescriptors = calcDependentObjects();
		return objectDescriptors;
	}

	/**
	 * Don't hold on to the resolved results as it will prevent
	 * them from being garbage collected.
	 */
	protected void clearResolvedArgs() {
		if (actualArgs == null)
			return;
		for (int i = 0; i < actualArgs.length; i++) {
			actualArgs[i] = null;
		}
		actualArgs = null;
		return;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (location == null ? 0 : location.hashCode());
		result = prime * result + (groupUpdates ? 1231 : 1237);
		result = prime * result + ((injector == null) ? 0 : injector.hashCode());
		result = prime * result + (isOptional ? 1231 : 1237);
		result = prime * result + ((primarySupplier == null) ? 0 : primarySupplier.hashCode());
		result = prime * result + objectHashcode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Requestor<?> other = (Requestor<?>) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (groupUpdates != other.groupUpdates)
			return false;
		if (injector != other.injector)
			return false;
		if (isOptional != other.isOptional)
			return false;
		if (primarySupplier != other.primarySupplier)
			return false;
		if (getRequestingObject() != other.getRequestingObject())
			return false;
		return true;
	}

}
