/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.text.tests;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.AssertionFailedException;


/**
 * Helper class for accessing classes and members which cannot
 * be accessed using standard Java access control like private
 * or package visible elements.
 *
 * @since 3.1
 */
public class Accessor {

	/** The class to access. */
	private Class<?> fClass;
	/** The instance to access. */
	private Object fInstance;

	/**
	 * Creates an accessor for the given <code>instance</code> and
	 * <code>clazz</code>. Only non-inherited members of that particular
	 * class can be accessed.
	 *
	 * @param instance the instance
	 * @param clazz the class
	 */
	public Accessor(Object instance, Class<?> clazz) {
		Assert.isNotNull(instance);
		Assert.isNotNull(clazz);
		fInstance= instance;
		fClass= clazz;
	}

	/**
	 * Creates an accessor for the given <code>instance</code> and
	 * <code>className</code>. Only non-inherited members of that particular
	 * class can be accessed.
	 *
	 * @param instance the instance
	 * @param className the name of the class
	 * @param classLoader the class loader to use, e.g. <code>getClass().getClassLoader()</code>
	 */
	public Accessor(Object instance, String className, ClassLoader classLoader) {
		Assert.isNotNull(instance);
		Assert.isNotNull(className);
		Assert.isNotNull(classLoader);
		fInstance= instance;
		try {
			fClass= Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			fail(e);
		} catch (ExceptionInInitializerError e) {
			fail(e);
		}
	}

	/**
	 * Creates an accessor for the given class.
	 * <p>
	 * In order to get the type information from the given
	 * arguments they must all be instanceof Object. Use
	 * {@link #Accessor(String, ClassLoader, Class[], Object[])} if this
	 * is not the case.</p>
	 *
	 * @param className the name of the class
	 * @param classLoader the class loader to use, e.g. <code>getClass().getClassLoader()</code>
	 * @param constructorArgs the constructor arguments which must all be instance of Object
	 */
	public Accessor(String className, ClassLoader classLoader, Object[] constructorArgs) {
		this(className, classLoader, getTypes(constructorArgs), constructorArgs);
	}

	/**
	 * Creates an accessor for the given class.
	 *
	 * @param className the name of the class
	 * @param classLoader the class loader to use, e.g. <code>getClass().getClassLoader()</code>
	 * @param constructorTypes the types of the constructor arguments
	 * @param constructorArgs the constructor arguments
	 */
	public Accessor(String className, ClassLoader classLoader, Class<?>[] constructorTypes, Object[] constructorArgs) {
		try {
			fClass= Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			fail(e);
		} catch (ExceptionInInitializerError e) {
			fail(e);
		}
		Constructor<?> constructor= null;
		try {
			constructor= fClass.getDeclaredConstructor(constructorTypes);
		} catch (SecurityException e) {
			fail(e);
		} catch (NoSuchMethodException e) {
			fail(e);
		}
		Assert.isNotNull(constructor);
		constructor.setAccessible(true);
		try {
			fInstance= constructor.newInstance(constructorArgs);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (InvocationTargetException e) {
			fail(e);
		} catch (InstantiationException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
	}

	/**
	 * Creates an accessor for the given class.
	 * <p>
	 * This constructor is used to access static stuff.
	 * </p>
	 *
	 * @param className the name of the class
	 * @param classLoader the class loader to use, e.g. <code>getClass().getClassLoader()</code>
	 */
	public Accessor(String className, ClassLoader classLoader) {
		try {
			fClass= Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			fail(e);
		} catch (ExceptionInInitializerError e) {
			fail(e);
		}
	}

	/**
	 * Invokes the method with the given method name and arguments.
	 * <p>
	 * In order to get the type information from the given
	 * arguments all those arguments must be instance of Object. Use
	 * {@link #invoke(String, Class[], Object[])} if this
	 * is not the case.</p>
	 *
	 * @param methodName the method name
	 * @param arguments the method arguments which must all be instance of Object
	 * @return the method return value
	 */
	public Object invoke(String methodName, Object[] arguments) {
		return invoke(methodName, getTypes(arguments), arguments);
	}

	/**
	 * Invokes the method with the given method name and arguments.
	 *
	 * @param methodName the method name
	 * @param types the argument types
	 * @param arguments the method arguments
	 * @return the method return value
	 */
	public Object invoke(String methodName, Class<?>[] types, Object[] arguments) {
		Method method= null;
		try {
			method= fClass.getDeclaredMethod(methodName, types);
		} catch (SecurityException e) {
			fail(e);
		} catch (NoSuchMethodException e) {
			fail(e);
		}
		Assert.isNotNull(method);
		method.setAccessible(true);
		try {
			return method.invoke(fInstance, arguments);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (InvocationTargetException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
		return null;
	}

	/**
	 * Assigns the given value to the field with the given name.
	 *
	 * @param fieldName the field name
	 * @param value the value to assign to the field
	 */
	public void set(String fieldName, Object value) {
		Field field= getField(fieldName);
		try {
			field.set(fInstance, value);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
	}

	/**
	 * Assigns the given value to the field with the given name.
	 *
	 * @param fieldName the field name
	 * @param value the value to assign to the field
	 */
	public void set(String fieldName, boolean value) {
		Field field= getField(fieldName);
		try {
			field.setBoolean(fInstance, value);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
	}

	/**
	 * Assigns the given value to the field with the given name.
	 *
	 * @param fieldName the field name
	 * @param value the value to assign to the field
	 */
	public void set(String fieldName, int value) {
		Field field= getField(fieldName);
		try {
			field.setInt(fInstance, value);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
	}

	/**
	 * Returns the value of the field with the given name.
	 *
	 * @param fieldName the field name
	 * @return the value of the field
	 */
	public Object get(String fieldName) {
		Field field= getField(fieldName);
		try {
			return field.get(fInstance);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
		// Unreachable code
		return null;
	}

	/**
	 * Returns the value of the field with the given name.
	 *
	 * @param fieldName the field name
	 * @return the value of the field
	 */
	public boolean getBoolean(String fieldName) {
		Field field= getField(fieldName);
		try {
			return field.getBoolean(fInstance);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
		// Unreachable code
		return false;
	}

	/**
	 * Returns the value of the field with the given name.
	 *
	 * @param fieldName the field name
	 * @return the value of the field
	 */
	public int getInt(String fieldName) {
		Field field= getField(fieldName);
		try {
			return field.getInt(fInstance);
		} catch (IllegalArgumentException e) {
			fail(e);
		} catch (IllegalAccessException e) {
			fail(e);
		}
		// Unreachable code
		return 0;
	}

	public Field getField(String fieldName) {
		Field field= null;
		try {
			field= fClass.getDeclaredField(fieldName);
		} catch (SecurityException e) {
			fail(e);
		} catch (NoSuchFieldException e) {
			fail(e);
		}
		field.setAccessible(true);
		return field;
	}

	private static Class<?>[] getTypes(Object[] objects) {
		if (objects == null)
			return null;

		int length= objects.length;
		Class<?>[] classes= new Class[length];
		for (int i= 0; i < length; i++) {
			Assert.isNotNull(objects[i]);
			classes[i]= objects[i].getClass();
		}
		return classes;
	}

	private void fail(Throwable e) {
		AssertionFailedException afe= new AssertionFailedException(e.getLocalizedMessage());
		afe.initCause(e);
		throw afe;
	}
}
