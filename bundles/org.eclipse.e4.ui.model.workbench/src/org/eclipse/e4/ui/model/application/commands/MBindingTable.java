/**
 * Copyright (c) 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      IBM Corporation - initial API and implementation
 */
package org.eclipse.e4.ui.model.application.commands;

import java.util.List;
import org.eclipse.e4.ui.model.application.MApplicationElement;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * <p>
 * A set of Bindings that will be active if the matching <code>MBindingContext</code>
 * is active.
 * </p>
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.e4.ui.model.application.commands.MBindingTable#getBindings <em>Bindings</em>}</li>
 *   <li>{@link org.eclipse.e4.ui.model.application.commands.MBindingTable#getBindingContext <em>Binding Context</em>}</li>
 * </ul>
 *
 * @model
 * @generated
 */
public interface MBindingTable extends MApplicationElement {
	/**
	 * Returns the value of the '<em><b>Bindings</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.e4.ui.model.application.commands.MKeyBinding}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * <strong>Developers</strong>:
	 * Add more detailed documentation by editing this comment in 
	 * org.eclipse.ui.model.workbench/model/UIElements.ecore. 
	 * There is a GenModel/documentation node under each type and attribute.
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Bindings</em>' containment reference list.
	 * @model containment="true"
	 * @generated
	 */
	List<MKeyBinding> getBindings();

	/**
	 * Returns the value of the '<em><b>Binding Context</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * <strong>Developers</strong>:
	 * Add more detailed documentation by editing this comment in 
	 * org.eclipse.ui.model.workbench/model/UIElements.ecore. 
	 * There is a GenModel/documentation node under each type and attribute.
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Binding Context</em>' reference.
	 * @see #setBindingContext(MBindingContext)
	 * @model required="true"
	 * @generated
	 */
	MBindingContext getBindingContext();

	/**
	 * Sets the value of the '{@link org.eclipse.e4.ui.model.application.commands.MBindingTable#getBindingContext <em>Binding Context</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Binding Context</em>' reference.
	 * @see #getBindingContext()
	 * @generated
	 */
	void setBindingContext(MBindingContext value);

} // MBindingTable
