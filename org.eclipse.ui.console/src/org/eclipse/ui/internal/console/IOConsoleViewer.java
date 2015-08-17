/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     vogella GmbH - Bug 287303 - [patch] Add Word Wrap action to Console View
 *******************************************************************************/
package org.eclipse.ui.internal.console;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IScrollLockStateProvider;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.TextConsoleViewer;

/**
 * Viewer used to display an IOConsole
 *
 * @since 3.1
 */
public class IOConsoleViewer extends TextConsoleViewer {
    /**
     * will always scroll with output if value is true.
     */
    private boolean fAutoScroll = true;

    private boolean fWordWrap = false;

    private IDocumentListener fDocumentListener;

    public IOConsoleViewer(Composite parent, TextConsole console) {
        super(parent, console);
    }

	/**
	 * Constructs a new viewer in the given parent for the specified console.
	 *
	 * @param parent the containing composite
	 * @param console the IO console
	 * @param scrollLockStateProvider the scroll lock state provider
	 * @since 3.6
	 */
	public IOConsoleViewer(Composite parent, TextConsole console, IScrollLockStateProvider scrollLockStateProvider) {
		super(parent, console, scrollLockStateProvider);
	}

    public boolean isAutoScroll() {
        return fAutoScroll;
    }

    public void setAutoScroll(boolean scroll) {
        fAutoScroll = scroll;
    }

    public boolean isWordWrap() {
        return fWordWrap;
    }

    public void setWordWrap(boolean wordwrap) {
        fWordWrap = wordwrap;
        getTextWidget().setWordWrap(wordwrap);
    }

    @Override
	protected void handleVerifyEvent(VerifyEvent e) {
        IDocument doc = getDocument();
        String[] legalLineDelimiters = doc.getLegalLineDelimiters();
        String eventString = e.text;
        try {
            IConsoleDocumentPartitioner partitioner = (IConsoleDocumentPartitioner) doc.getDocumentPartitioner();
            if (!partitioner.isReadOnly(e.start)) {
                boolean isCarriageReturn = false;
                for (int i = 0; i < legalLineDelimiters.length; i++) {
                    if (e.text.equals(legalLineDelimiters[i])) {
                        isCarriageReturn = true;
                        break;
                    }
                }

                if (!isCarriageReturn) {
                    super.handleVerifyEvent(e);
                    return;
                }
            }

            int length = doc.getLength();
            if (e.start == length) {
                super.handleVerifyEvent(e);
            } else {
                try {
                    doc.replace(length, 0, eventString);
                    updateWidgetCaretLocation(length);
                } catch (BadLocationException e1) {
                }
                e.doit = false;
            }
        } finally {
            StyledText text = (StyledText) e.widget;
            text.setCaretOffset(text.getCharCount());
        }
    }

    /*
     * Update the Text widget location to new location
     */
	private void updateWidgetCaretLocation(int documentCaret) {
		int widgetCaret = modelOffset2WidgetOffset(documentCaret);
		if (widgetCaret == -1) {
			// try to move it to the closest spot
			IRegion region = getModelCoverage();
			if (region != null) {
				if (documentCaret <= region.getOffset()) {
					widgetCaret = 0;
				} else if (documentCaret >= region.getOffset() + region.getLength()) {
					widgetCaret = getVisibleRegion().getLength();
				}
			}
		}
		if (widgetCaret != -1) {
			// there is a valid widget caret
			getTextWidget().setCaretOffset(widgetCaret);
			getTextWidget().showSelection();
		}
	}

    /**
     * makes the associated text widget uneditable.
     */
    public void setReadOnly() {
        ConsolePlugin.getStandardDisplay().asyncExec(new Runnable() {
            @Override
			public void run() {
                StyledText text = getTextWidget();
                if (text != null && !text.isDisposed()) {
                    text.setEditable(false);
                }
            }
        });
    }

    /**
     * @return <code>false</code> if text is editable
     */
    public boolean isReadOnly() {
        return !getTextWidget().getEditable();
    }

    @Override
	public void setDocument(IDocument document) {
        IDocument oldDocument= getDocument();

        super.setDocument(document);

        if (oldDocument != null) {
            oldDocument.removeDocumentListener(getDocumentListener());
        }
        if (document != null) {
            document.addDocumentListener(getDocumentListener());
        }
    }

    private IDocumentListener getDocumentListener() {
        if (fDocumentListener == null) {
            fDocumentListener= new IDocumentListener() {
				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
                }

                @Override
				public void documentChanged(DocumentEvent event) {
                    if (fAutoScroll) {
                        revealEndOfDocument();
                    }
                }
            };
        }
        return fDocumentListener;
    }
}
