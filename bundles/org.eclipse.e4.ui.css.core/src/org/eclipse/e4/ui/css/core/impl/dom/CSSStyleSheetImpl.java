/*******************************************************************************
 * Copyright (c) 2008, 2013 Angelo Zerr and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *     IBM Corporation - ongoing development
 *******************************************************************************/

package org.eclipse.e4.ui.css.core.impl.dom;

import org.eclipse.e4.ui.css.core.exceptions.DOMExceptionImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheet;

public class CSSStyleSheetImpl extends AbstractCSSNode implements CSSStyleSheet {

	private CSSRuleList rules = null;

	public CSSStyleSheetImpl() {
		super();
	}

	// W3C CSSStyleSheet API methods

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleSheet#deleteRule(int)
	 */
	@Override
	public void deleteRule(int position) throws DOMException {
		try {
			((CSSRuleListImpl) rules).remove(position);
		} catch (IndexOutOfBoundsException ex) {
			throw new DOMExceptionImpl(DOMException.INDEX_SIZE_ERR, DOMExceptionImpl.ARRAY_OUT_OF_BOUNDS, ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleSheet#getCssRules()
	 */
	@Override
	public CSSRuleList getCssRules() {
		return rules;
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleSheet#getOwnerRule()
	 */
	@Override
	public CSSRule getOwnerRule() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.css.CSSStyleSheet#insertRule(String, int)
	 */
	@Override
	public int insertRule(String arg0, int arg1) throws DOMException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}


	// org.w3c.dom.stylesheet.StyleSheet API methods

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getDisabled()
	 */
	@Override
	public boolean getDisabled() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getHref()
	 */
	@Override
	public String getHref() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getMedia()
	 */
	@Override
	public MediaList getMedia() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getOwnerNode()
	 */
	@Override
	public Node getOwnerNode() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getParentStyleSheet()
	 */
	@Override
	public StyleSheet getParentStyleSheet() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	/*
	 * (non-Javadoc)
	 * @see org.w3c.dom.stylesheet.StyleSheet#setDisabled(boolean)
	 */
	@Override
	public void setDisabled(boolean disabled) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	// Additional

	public void setRuleList(CSSRuleList rules) {
		this.rules = rules;
	}
}
