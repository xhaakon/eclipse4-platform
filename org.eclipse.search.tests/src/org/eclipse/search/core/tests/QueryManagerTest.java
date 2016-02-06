/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.search.core.tests;

import junit.framework.TestCase;

import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;

import org.eclipse.search2.internal.ui.InternalSearchUI;

public class QueryManagerTest extends TestCase {
	public void testRemoveQuery() {
		ISearchQuery query= new NullQuery();
		InternalSearchUI.getInstance().addQuery(query);
		InternalSearchUI.getInstance().removeQuery(query);
		ISearchQuery[] all= InternalSearchUI.getInstance().getQueries();
		for (int i= 0; i < all.length; i++) {
			if (query == all[i])
				assertTrue(false);
		}
	}

	public void testAddQuery() {
		ISearchQuery query= new NullQuery();
		InternalSearchUI.getInstance().addQuery(query);
		ISearchQuery[] all= NewSearchUI.getQueries();
		for (int i= 0; i < all.length; i++) {
			if (query == all[i])
				return;
		}
		assertTrue(false);
	}

	public void testQueryListener() {
		final boolean [] wasAdded= { false };
		final boolean [] wasRemoved= { false };
		IQueryListener l= new IQueryListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.search.ui.IQueryListener#queryAdded(org.eclipse.search.ui.ISearchQuery)
			 */
			public void queryAdded(ISearchQuery query) {
				wasAdded[0]= true;
			}
			/* (non-Javadoc)
			 * @see org.eclipse.search.ui.IQueryListener#queryRemoved(org.eclipse.search.ui.ISearchQuery)
			 */
			public void queryRemoved(ISearchQuery query) {
				wasRemoved[0]= true;
			}
			public void queryStarting(ISearchQuery query) {
				// not interested
			}
			public void queryFinished(ISearchQuery query) {
				// not interested
			}
		} ;
		NewSearchUI.addQueryListener(l);
		ISearchQuery query= new NullQuery();
		InternalSearchUI.getInstance().addQuery(query);
		assertTrue(wasAdded[0]);
		InternalSearchUI.getInstance().removeQuery(query);
		assertTrue(wasRemoved[0]);
		InternalSearchUI.getInstance().removeQueryListener(l);
		wasAdded[0]= false;
		wasRemoved[0]= false;
		InternalSearchUI.getInstance().addQuery(query);
		assertFalse(wasAdded[0]);
		InternalSearchUI.getInstance().removeQuery(query);
		assertFalse(wasRemoved[0]);
	}
}
