/*******************************************************************************
 * Copyright (c) 2014, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * *******************************************************************************/
package org.eclipse.ui.console;


/**
 * A scroll lock provider allows a client to control the scroll lock state of
 * its container, e.g. a view.
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @since 3.6
 */
public interface IScrollLockStateProvider {

	/**
	 * Sets the scroll lock state that got explicitly set by the user, e.g. by
	 * pressing a button that controls the state.
	 *
	 * @param scrollLock <code>true</code> to turn scroll lock on, otherwise
	 *            <code>false</code>
	 */
	public void setScrollLock(boolean scrollLock);


	/**
	 * Returns the scroll lock state that got explicitly set by the user, e.g.
	 * by pressing a button that controls the state.
	 *
	 * @return <code>true</code> if scroll lock is on, <code>false</code>
	 *         otherwise
	 */
	public boolean getScrollLock();

	/**
	 * Sets the auto-scroll lock state, e.g. when the user moves the caret
	 * upwards in a console.
	 *
	 * @param scrollLock <code>true</code> to turn auto-scroll lock on,
	 *            otherwise <code>false</code>
	 */
	public void setAutoScrollLock(boolean scrollLock);

	/**
	 * Returns the auto-scroll lock state.
	 *
	 * @see #setAutoScrollLock(boolean)
	 * @return <code>true</code> if auto-scroll lock is on, <code>false</code>
	 *         otherwise
	 */
	public boolean getAutoScrollLock();
}
