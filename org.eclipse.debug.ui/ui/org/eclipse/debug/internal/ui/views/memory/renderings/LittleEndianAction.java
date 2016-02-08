/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.views.memory.renderings;

import org.eclipse.debug.core.model.MemoryByte;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * Show integers in little endian.
 * @since 3.0
 */
public class LittleEndianAction implements IObjectActionDelegate {

	AbstractIntegerRendering fRendering;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {

		if (fRendering == null)
			return;
		
		if (fRendering.getDisplayEndianess() != RenderingsUtil.LITTLE_ENDIAN){
			fRendering.setDisplayEndianess(RenderingsUtil.LITTLE_ENDIAN);
			fRendering.refresh();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection == null)
			return;
		
		if (selection instanceof IStructuredSelection)
		{
			Object obj = ((IStructuredSelection)selection).getFirstElement();
			if (obj == null)
				return;
			
			if (obj instanceof AbstractIntegerRendering)
			{
				fRendering = (AbstractIntegerRendering)obj;
			}
			
			int endianess = RenderingsUtil.ENDIANESS_UNKNOWN;
			if (fRendering.getDisplayEndianess() == RenderingsUtil.ENDIANESS_UNKNOWN)
			{
				MemoryByte[] selectedBytes = fRendering.getSelectedAsBytes();
					
				for (int i=0; i<selectedBytes.length; i++)
				{
					if (!selectedBytes[i].isEndianessKnown())
					{
						endianess = RenderingsUtil.ENDIANESS_UNKNOWN;
						break;
					}
					if (i==0)
					{
						endianess = selectedBytes[i].isBigEndian()?RenderingsUtil.BIG_ENDIAN:RenderingsUtil.LITTLE_ENDIAN;
					}
					else
					{
						int byteEndianess = selectedBytes[i].isBigEndian()?RenderingsUtil.BIG_ENDIAN:RenderingsUtil.LITTLE_ENDIAN;
						if (endianess != byteEndianess)
						{
							endianess = RenderingsUtil.ENDIANESS_UNKNOWN;
							break;
						}
					}
				}
			}
			else
				endianess = fRendering.getDisplayEndianess();
			
			if (endianess == RenderingsUtil.LITTLE_ENDIAN)
				action.setChecked(true);
			else
				action.setChecked(false);
		}		
	}

}
