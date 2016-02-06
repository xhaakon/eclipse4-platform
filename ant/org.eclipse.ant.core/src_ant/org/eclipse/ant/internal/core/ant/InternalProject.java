/*******************************************************************************
 *  Copyright (c) 2000, 2015 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - derived implementation
 *******************************************************************************/

package org.eclipse.ant.internal.core.ant;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat; // can't use ICU, ant build script
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;

/**
 * A subclass of Project to facilitate "faster" parsing with less garbage generated. This class is not used on Ant 1.6 and newer due to the
 * improvements in lazy loading of these Ant versions.
 * 
 * Only three tasks are loaded (property, taskdef and typedef: three tasks that can be defined outside of a target on Ant 1.5.1 or older).
 * 
 * Datatypes are loaded if requested.
 * 
 * Derived from the original Ant Project class
 */
public class InternalProject extends Project {

	private Hashtable<String, Class<?>> typeNameToClass = null;

	public InternalProject() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Project#init()
	 */
	@Override
	public void init() throws BuildException {
		setJavaVersionProperty();

		try {
			Class<?> taskClass = Class.forName("org.apache.tools.ant.taskdefs.Property"); //$NON-NLS-1$
			addTaskDefinition("property", taskClass); //$NON-NLS-1$
			taskClass = Class.forName("org.apache.tools.ant.taskdefs.Typedef"); //$NON-NLS-1$
			addTaskDefinition("typedef", taskClass); //$NON-NLS-1$
			taskClass = Class.forName("org.apache.tools.ant.taskdefs.Taskdef"); //$NON-NLS-1$
			addTaskDefinition("taskdef", taskClass); //$NON-NLS-1$
		}
		catch (NoClassDefFoundError e) {
			throw new BuildException(InternalAntMessages.InternalAntRunner_Missing_Class, e);
		}
		catch (ClassNotFoundException c) {
			throw new BuildException(InternalAntMessages.InternalAntRunner_Missing_Class, c);
		}

		setSystemProperties();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Project#createDataType(java.lang.String)
	 */
	@Override
	public Object createDataType(String typeName) throws BuildException {
		if (typeNameToClass == null) {
			initializeTypes();
		}
		Class<?> typeClass = typeNameToClass.get(typeName);

		if (typeClass == null) {
			return null;
		}

		Throwable thrown = null;
		try {
			Constructor<?> ctor = null;
			boolean noArg = false;
			// DataType can have a "no arg" constructor or take a single
			// Project argument.
			try {
				ctor = typeClass.getConstructor(new Class[0]);
				noArg = true;
			}
			catch (NoSuchMethodException nse) {
				ctor = typeClass.getConstructor(new Class[] { Project.class });
				noArg = false;
			}

			Object o = null;
			if (noArg) {
				o = ctor.newInstance(new Object[0]);
			} else {
				o = ctor.newInstance(new Object[] { this });
			}
			if (o instanceof ProjectComponent) {
				((ProjectComponent) o).setProject(this);
			}
			return o;
		}
		catch (InvocationTargetException ite) {
			thrown = ite.getTargetException();
		}
		catch (IllegalArgumentException e) {
			thrown = e;
		}
		catch (InstantiationException e) {
			thrown = e;
		}
		catch (IllegalAccessException e) {
			thrown = e;
		}
		catch (NoSuchMethodException nse) {
			thrown = nse;
		}
		catch (NoClassDefFoundError ncdfe) {
			thrown = ncdfe;
		}
		if (thrown != null) {
			String message = MessageFormat.format(InternalAntMessages.InternalProject_could_not_create_type, new Object[] { typeName,
					thrown.toString() });
			throw new BuildException(message, thrown);
		}
		// this line is actually unreachable
		return null;
	}

	/**
	 * Initialize the mapping of data type name to data type classname
	 */
	private void initializeTypes() {
		typeNameToClass = new Hashtable<String, Class<?>>(18);
		try (InputStream in = Project.class.getResourceAsStream("/org/apache/tools/ant/types/defaults.properties")) { //$NON-NLS-1$
			if (in == null) {
				return;
			}
			Properties props = new Properties();
			props.load(in);
			Enumeration<?> enumeration = props.propertyNames();
			while (enumeration.hasMoreElements()) {
				String typeName = (String) enumeration.nextElement();
				String className = props.getProperty(typeName);
				try {
					Class<?> typeClass = Class.forName(className);
					typeNameToClass.put(typeName, typeClass);
				}
				catch (NoClassDefFoundError e) {
					// ignore
				}
				catch (ClassNotFoundException c) {
					// ignore
				}
			}
		}
		catch (IOException ioe) {
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Project#getDataTypeDefinitions()
	 */
	@Override
	public Hashtable<String, Class<?>> getDataTypeDefinitions() {
		if (typeNameToClass == null) {
			initializeTypes();
		}
		return typeNameToClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Project#addDataTypeDefinition(java.lang.String, java.lang.Class)
	 */
	@Override
	public void addDataTypeDefinition(String typeName, Class<?> typeClass) {
		getDataTypeDefinitions();
		typeNameToClass.put(typeName, typeClass);
	}
}
