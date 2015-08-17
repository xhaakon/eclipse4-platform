/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.navigator.filters;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.navigator.CommonNavigatorMessages;
import org.eclipse.ui.internal.navigator.StringMatcher;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.INavigatorFilterService;

/**
 * @since 3.2
 *
 */
public class CommonFiltersTab extends CustomizationTab {

	private static final String ALL = "*"; //$NON-NLS-1$

	private String initialFilterTextValue = CommonNavigatorMessages.CommonFilterSelectionDialog_enter_name_of_filte_;

	private Text filterText;

	private ILabelProvider filterLabelProvider = new CommonFilterLabelProvider();

	private CommonFilterContentProvider filterContentProvider = new CommonFilterContentProvider();

	private TablePatternFilter patternFilter = new TablePatternFilter();

	protected CommonFiltersTab(Composite parent,
			INavigatorContentService aContentService) {
		super(parent, aContentService);
		createControl();
	}

	private void createControl() {

		createInstructionsLabel(CommonNavigatorMessages.CommonFilterSelectionDialog_Select_the_filters_to_apply);

		createPatternFilterText(this);

		createTable();

		getTableViewer().setContentProvider(filterContentProvider);
		getTableViewer().setLabelProvider(filterLabelProvider);
		getTableViewer().setSorter(new CommonFilterSorter());
		getTableViewer().setInput(getContentService());

		getTableViewer().addFilter(patternFilter);

		updateFiltersCheckState();

	}

	private void createPatternFilterText(Composite composite) {
		filterText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		GridData filterTextGridData = new GridData(GridData.FILL_HORIZONTAL);
		filterText.setLayoutData(filterTextGridData);
		filterText.setText(initialFilterTextValue);
		filterText.setFont(composite.getFont());

		filterText.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					@Override
					public void getName(AccessibleEvent e) {
						String filterTextString = filterText.getText();
						if (filterTextString.length() == 0) {
							e.result = initialFilterTextValue;
						} else {
							e.result = filterTextString;
						}
					}
				});

		filterText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (initialFilterTextValue.equals(filterText.getText().trim())) {
					filterText.selectAll();
				}
			}
		});

		filterText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				super.mouseUp(e);
				if (initialFilterTextValue.equals(filterText.getText().trim())) {
					filterText.selectAll();
				}
			}
		});

		filterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// on a CR we want to transfer focus to the list
				boolean hasItems = getTable().getItemCount() > 0;
				if (hasItems && e.keyCode == SWT.ARROW_DOWN) {
					getTable().setFocus();
				} else if (e.character == SWT.CR) {
					return;
				}
			}
		});

		// enter key set focus to tree
		filterText.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					if (getTableViewer().getTable().getItemCount() == 0) {
						Display.getCurrent().beep();
					} else {
						// if the initial filter text hasn't changed, do not try
						// to match
						boolean hasFocus = getTable().setFocus();
						boolean textChanged = !initialFilterTextValue
								.equals(filterText.getText().trim());
						if (hasFocus && textChanged
								&& filterText.getText().trim().length() > 0) {
							TableItem item = getFirstHighlightedItem(getTable()
									.getItems());
							if (item != null) {
								getTable().setSelection(
										new TableItem[] { item });
								ISelection sel = getTableViewer()
										.getSelection();
								getTableViewer().setSelection(sel, true);
							}
						}
					}
				}
			}

			private TableItem getFirstHighlightedItem(TableItem[] items) {
				for (int i = 0; i < items.length; i++) {
					if (patternFilter.match(items[i].getText())) {
						return items[i];
					}
				}
				return null;
			}
		});

		filterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textChanged();
			}
		});
	}

	void setInitialFocus() {
		filterText.forceFocus();
	}

	private void textChanged() {
		patternFilter.setPattern(filterText.getText());
		getTableViewer().refresh();

		Set<Object> checkedItems = getCheckedItems();
		for (Iterator<Object> iterator = checkedItems.iterator(); iterator.hasNext();) {
			getTableViewer().setChecked(iterator.next(), true);
		}
	}

	private void updateFiltersCheckState() {
		Object[] children = filterContentProvider
				.getElements(getContentService());
		ICommonFilterDescriptor filterDescriptor;
		INavigatorFilterService filterService = getContentService()
				.getFilterService();
		for (int i = 0; i < children.length; i++) {
			filterDescriptor = (ICommonFilterDescriptor) children[i];
			if(filterService.isActive(filterDescriptor.getId())) {
				getTableViewer().setChecked(children[i], true);
				getCheckedItems().add(children[i]);
			} else {
				getTableViewer().setChecked(children[i], false);
			}
		}
	}

	private class TablePatternFilter extends ViewerFilter {

		private StringMatcher matcher = null;

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			return match(filterLabelProvider.getText(element));
		}

		protected void setPattern(String newPattern) {
			if (newPattern == null || newPattern.trim().length() == 0) {
				matcher = new StringMatcher(ALL, true, false);
			} else {
				String patternString = ALL + newPattern + ALL;
				matcher = new StringMatcher(patternString, true, false);
			}

		}

		/**
		 * Answers whether the given String matches the pattern.
		 *
		 * @param input
		 *            the String to test
		 *
		 * @return whether the string matches the pattern
		 */
		protected boolean match(String input) {
			if (input == null) {
				return false;
			}
			return matcher == null || matcher.match(input);
		}
	}

	private class CommonFilterSorter extends ViewerSorter {

		@Override
		public void sort(Viewer viewer, Object[] elements) {
			Arrays.sort(elements, new Comparator() {
				@Override
				public int compare(Object o1, Object o2) {
					ICommonFilterDescriptor lvalue = (ICommonFilterDescriptor) o1;
					ICommonFilterDescriptor rvalue = (ICommonFilterDescriptor) o2;

					return lvalue.getName().compareTo(rvalue.getName());
				}
			});

		}

	}

}
