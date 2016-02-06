/*******************************************************************************
 * Copyright (c) 2010, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.e4.ui.internal.workbench;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.workbench.modeling.ModelDelta;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

class EMFDeltaMapSet extends ModelDelta {

	private EStructuralFeature feature;
	private Map<String, String> deltaMap;

	EMFDeltaMapSet(EObject object, EStructuralFeature feature, Map<String, String> deltaMap) {
		super(object, feature.getName(), null);
		this.feature = feature;
		this.deltaMap = deltaMap;
	}

	@Override
	public IStatus apply() {
		@SuppressWarnings("unchecked")
		EMap<String, String> map = (EMap<String, String>) ((EObject) getObject()).eGet(feature);
		map.clear();
		map.putAll(deltaMap);
		return Status.OK_STATUS;
	}
}
