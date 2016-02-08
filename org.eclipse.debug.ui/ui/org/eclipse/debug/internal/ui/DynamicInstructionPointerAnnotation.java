/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui;

 
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jface.text.source.Annotation;

/**
 * Client specified instruction pointer annotation.
 */
public class DynamicInstructionPointerAnnotation extends Annotation {

	/**
	 * The frame for this instruction pointer annotation.  This is necessary only so that
	 * instances of this class can be distinguished by equals().
	 */
	private IStackFrame fStackFrame;
		
	/**
	 * 
	 * @param frame
	 * @param markerAnnotationSpecificationId
	 * @param text
	 */
	public DynamicInstructionPointerAnnotation(IStackFrame frame, String markerAnnotationSpecificationId, String text) {
		super(markerAnnotationSpecificationId, false, text);
		fStackFrame = frame;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof DynamicInstructionPointerAnnotation) {
			return getStackFrame().equals(((DynamicInstructionPointerAnnotation)other).getStackFrame());			
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getStackFrame().hashCode();
	}

	/**
	 * Returns the stack frame associated with this annotation
	 * 
	 * @return the stack frame associated with this annotation
	 */
	private IStackFrame getStackFrame() {
		return fStackFrame;
	}

}
