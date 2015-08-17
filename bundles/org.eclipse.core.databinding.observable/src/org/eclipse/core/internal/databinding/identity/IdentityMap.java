/*******************************************************************************
 * Copyright (c) 2008, 2010 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 215531)
 *     Matthew Hall - bug 228125
 *         (through ViewerElementMap.java)
 *     Matthew Hall - bugs 262269, 303847
 ******************************************************************************/

package org.eclipse.core.internal.databinding.identity;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.internal.databinding.observable.Util;
import org.eclipse.core.runtime.Assert;

/**
 * A {@link Map} whose keys are added, removed and compared by identity. The
 * keys in the map are compared using <code>==</code> instead of
 * {@link #equals(Object)}.
 * <p>
 * This class is <i>not</i> a strict implementation the {@link Map} interface.
 * It intentionally violates the {@link Map} contract, which requires the use of
 * {@link #equals(Object)} when comparing keys.
 *
 * @since 1.2
 */
public class IdentityMap implements Map {
	private Map wrappedMap;

	/**
	 * Constructs an IdentityMap.
	 */
	public IdentityMap() {
		this.wrappedMap = new HashMap();
	}

	/**
	 * Constructs an IdentityMap containing all the entries in the specified
	 * map.
	 *
	 * @param map
	 *            the map whose entries are to be added to this map.
	 */
	public IdentityMap(Map map) {
		this();
		Assert.isNotNull(map);
		putAll(map);
	}

	@Override
	public void clear() {
		wrappedMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return wrappedMap.containsKey(IdentityWrapper.wrap(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return wrappedMap.containsValue(value);
	}

	@Override
	public Set entrySet() {
		final Set wrappedEntrySet = wrappedMap.entrySet();
		return new Set() {
			@Override
			public boolean add(Object o) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean addAll(Collection c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				wrappedEntrySet.clear();
			}

			@Override
			public boolean contains(Object o) {
				for (Iterator iterator = iterator(); iterator.hasNext();)
					if (iterator.next().equals(o))
						return true;
				return false;
			}

			@Override
			public boolean containsAll(Collection c) {
				for (Iterator iterator = c.iterator(); iterator.hasNext();)
					if (!contains(iterator.next()))
						return false;
				return true;
			}

			@Override
			public boolean isEmpty() {
				return wrappedEntrySet.isEmpty();
			}

			@Override
			public Iterator iterator() {
				final Iterator wrappedIterator = wrappedEntrySet.iterator();
				return new Iterator() {
					@Override
					public boolean hasNext() {
						return wrappedIterator.hasNext();
					}

					@Override
					public Object next() {
						final Map.Entry wrappedEntry = (Map.Entry) wrappedIterator
								.next();
						return new Map.Entry() {
							@Override
							public Object getKey() {
								return ((IdentityWrapper) wrappedEntry.getKey())
										.unwrap();
							}

							@Override
							public Object getValue() {
								return wrappedEntry.getValue();
							}

							@Override
							public Object setValue(Object value) {
								return wrappedEntry.setValue(value);
							}

							@Override
							public boolean equals(Object obj) {
								if (obj == this)
									return true;
								if (obj == null || !(obj instanceof Map.Entry))
									return false;
								Map.Entry that = (Map.Entry) obj;
								return this.getKey() == that.getKey()
										&& Util.equals(this.getValue(), that
												.getValue());
							}

							@Override
							public int hashCode() {
								return wrappedEntry.hashCode();
							}
						};
					}

					@Override
					public void remove() {
						wrappedIterator.remove();
					}
				};
			}

			@Override
			public boolean remove(Object o) {
				final Map.Entry unwrappedEntry = (Map.Entry) o;
				final IdentityWrapper wrappedKey = IdentityWrapper
						.wrap(unwrappedEntry.getKey());
				Map.Entry wrappedEntry = new Map.Entry() {
					@Override
					public Object getKey() {
						return wrappedKey;
					}

					@Override
					public Object getValue() {
						return unwrappedEntry.getValue();
					}

					@Override
					public Object setValue(Object value) {
						throw new UnsupportedOperationException();
					}

					@Override
					public boolean equals(Object obj) {
						if (obj == this)
							return true;
						if (obj == null || !(obj instanceof Map.Entry))
							return false;
						Map.Entry that = (Map.Entry) obj;
						return Util.equals(wrappedKey, that.getKey())
								&& Util
										.equals(this.getValue(), that
												.getValue());
					}

					@Override
					public int hashCode() {
						return wrappedKey.hashCode()
								^ (getValue() == null ? 0 : getValue()
										.hashCode());
					}
				};
				return wrappedEntrySet.remove(wrappedEntry);
			}

			@Override
			public boolean removeAll(Collection c) {
				boolean changed = false;
				for (Iterator iterator = c.iterator(); iterator.hasNext();)
					changed |= remove(iterator.next());
				return changed;
			}

			@Override
			public boolean retainAll(Collection c) {
				boolean changed = false;
				Object[] toRetain = c.toArray();
				outer: for (Iterator iterator = iterator(); iterator.hasNext();) {
					Object entry = iterator.next();
					for (int i = 0; i < toRetain.length; i++)
						if (entry.equals(toRetain[i]))
							continue outer;
					iterator.remove();
					changed = true;
				}
				return changed;
			}

			@Override
			public int size() {
				return wrappedEntrySet.size();
			}

			@Override
			public Object[] toArray() {
				return toArray(new Object[size()]);
			}

			@Override
			public Object[] toArray(Object[] a) {
				int size = size();
				if (a.length < size) {
					a = (Object[]) Array.newInstance(a.getClass()
							.getComponentType(), size);
				}
				int i = 0;
				for (Iterator iterator = iterator(); iterator.hasNext();) {
					a[i] = iterator.next();
					i++;
				}
				return a;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj == null || !(obj instanceof Set))
					return false;
				Set that = (Set) obj;
				return this.size() == that.size() && containsAll(that);
			}

			@Override
			public int hashCode() {
				return wrappedEntrySet.hashCode();
			}
		};
	}

	@Override
	public Object get(Object key) {
		return wrappedMap.get(IdentityWrapper.wrap(key));
	}

	@Override
	public boolean isEmpty() {
		return wrappedMap.isEmpty();
	}

	@Override
	public Set keySet() {
		final Set wrappedKeySet = wrappedMap.keySet();
		return new Set() {
			@Override
			public boolean add(Object o) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean addAll(Collection c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				wrappedKeySet.clear();
			}

			@Override
			public boolean contains(Object o) {
				return wrappedKeySet.contains(IdentityWrapper.wrap(o));
			}

			@Override
			public boolean containsAll(Collection c) {
				for (Iterator iterator = c.iterator(); iterator.hasNext();)
					if (!wrappedKeySet.contains(IdentityWrapper.wrap(iterator
							.next())))
						return false;
				return true;
			}

			@Override
			public boolean isEmpty() {
				return wrappedKeySet.isEmpty();
			}

			@Override
			public Iterator iterator() {
				final Iterator wrappedIterator = wrappedKeySet.iterator();
				return new Iterator() {
					@Override
					public boolean hasNext() {
						return wrappedIterator.hasNext();
					}

					@Override
					public Object next() {
						return ((IdentityWrapper) wrappedIterator.next())
								.unwrap();
					}

					@Override
					public void remove() {
						wrappedIterator.remove();
					}
				};
			}

			@Override
			public boolean remove(Object o) {
				return wrappedKeySet.remove(IdentityWrapper.wrap(o));
			}

			@Override
			public boolean removeAll(Collection c) {
				boolean changed = false;
				for (Iterator iterator = c.iterator(); iterator.hasNext();)
					changed |= wrappedKeySet.remove(IdentityWrapper
							.wrap(iterator.next()));
				return changed;
			}

			@Override
			public boolean retainAll(Collection c) {
				boolean changed = false;
				Object[] toRetain = c.toArray();
				outer: for (Iterator iterator = iterator(); iterator.hasNext();) {
					Object element = iterator.next();
					for (int i = 0; i < toRetain.length; i++)
						if (element == toRetain[i])
							continue outer;
					// element not contained in collection, remove.
					remove(element);
					changed = true;
				}
				return changed;
			}

			@Override
			public int size() {
				return wrappedKeySet.size();
			}

			@Override
			public Object[] toArray() {
				return toArray(new Object[wrappedKeySet.size()]);
			}

			@Override
			public Object[] toArray(Object[] a) {
				int size = wrappedKeySet.size();
				IdentityWrapper[] wrappedArray = (IdentityWrapper[]) wrappedKeySet
						.toArray(new IdentityWrapper[size]);
				Object[] result = a;
				if (a.length < size) {
					result = (Object[]) Array.newInstance(a.getClass()
							.getComponentType(), size);
				}
				for (int i = 0; i < size; i++)
					result[i] = wrappedArray[i].unwrap();
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj == this)
					return true;
				if (obj == null || !(obj instanceof Set))
					return false;
				Set that = (Set) obj;
				return this.size() == that.size() && containsAll(that);
			}

			@Override
			public int hashCode() {
				return wrappedKeySet.hashCode();
			}
		};
	}

	@Override
	public Object put(Object key, Object value) {
		return wrappedMap.put(IdentityWrapper.wrap(key), value);
	}

	@Override
	public void putAll(Map other) {
		for (Iterator iterator = other.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			wrappedMap.put(IdentityWrapper.wrap(entry.getKey()), entry
					.getValue());
		}
	}

	@Override
	public Object remove(Object key) {
		return wrappedMap.remove(IdentityWrapper.wrap(key));
	}

	@Override
	public int size() {
		return wrappedMap.size();
	}

	@Override
	public Collection values() {
		return wrappedMap.values();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null || !(obj instanceof Map))
			return false;
		Map that = (Map) obj;
		return this.entrySet().equals(that.entrySet());
	}

	@Override
	public int hashCode() {
		return wrappedMap.hashCode();
	}
}
