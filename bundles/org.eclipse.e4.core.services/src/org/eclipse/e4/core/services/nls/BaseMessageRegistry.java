/*******************************************************************************
 * Copyright (c) 2014 Dirk Fauth and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@googlemail.com> - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.core.services.nls;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import org.eclipse.e4.core.internal.services.ServicesActivator;
import org.osgi.service.log.LogService;

/**
 * Using this MessageRegistry allows to register controls for attributes in a
 * Messages class. These controls will automatically get updated in case of
 * Locale changes.
 * <p>
 * When updating the dependencies from Java 7 to Java 8, this class can be
 * replaced by a more modern variant that makes use of functional interfaces and
 * method references as shown in the above linked blog post.
 * </p>
 *
 * <p>
 * To use the registry you need to implement a subclass of
 * <code>BaseMessageRegistry</code> that is typed to the messages class that it
 * is related to. The main thing to do is to override
 * <code>updateMessages(M)</code> while getting the messages instance injected.
 * </p>
 *
 * <pre>
 * &#064;Creatable
 * public class ExampleMessageRegistry
 * 		extends
 * 			BaseMessageRegistry&lt;ExampleMessages&gt; {
 *
 * 	&#064;Override
 * 	&#064;Inject
 * 	public void updateMessages(@Translation ExampleMessages messages) {
 * 		super.updateMessages(messages);
 * 	}
 * }
 * </pre>
 *
 * <p>
 * Note that the registry instance is annotated with &#064;Creatable so it is
 * created per requestor and is making use of DI.
 * </p>
 *
 * @param <M>
 *            the message class type
 * @since 2.0
 */
public class BaseMessageRegistry<M> {

	private static LogService logService = ServicesActivator.getDefault().getLogService();

	private M messages;

	private final Map<MessageConsumer, MessageSupplier> bindings = new HashMap<MessageConsumer, MessageSupplier>();

	/**
	 * Register a consumer and a function that is acting as the supplier of the translation value.
	 * <p>
	 * This method allows to register a binding using method references and lambdas if used in an
	 * environment that already uses Java 8.
	 * </p>
	 *
	 * <pre>
	 * &#064;Inject
	 * ExampleMessageRegistry registry;
	 *
	 * Label myFirstLabel = new Label(parent, SWT.WRAP);
	 * registry.register(myFirstLabel::setText, (m) -&gt; m.firstLabelMessage);
	 * </pre>
	 *
	 * @param consumer
	 *            The consumer of the message.
	 * @param function
	 *            The function that supplies the message.
	 */
	public void register(MessageConsumer consumer, final MessageFunction<M> function) {
		register(consumer, new MessageSupplier() {

			@Override
			public String get() {
				return function.apply(messages);
			}
		});
	}

	/**
	 * Register a binding for the given consumer and supplier.
	 *
	 * <p>
	 * Unless you don't want to anonymously implement the consumer and supplier interfaces yourself,
	 * use the register methods that take the Control instance and String(s) as parameters.
	 * </p>
	 *
	 * @param consumer
	 *            The consumer of the message.
	 * @param supplier
	 *            The supplier of the message.
	 *
	 * @see BaseMessageRegistry#register(Object, String, String)
	 * @see BaseMessageRegistry#registerProperty(Object, String, String)
	 */
	public void register(MessageConsumer consumer, MessageSupplier supplier) {
		//set the value to the control
		consumer.accept(supplier.get());
		//remember the control and the supplier
		bindings.put(consumer, supplier);
	}

	/**
	 * Binds a method of an object to a message. Doing this the specified method will be called on
	 * the instance with the message String as parameter that is retrieved via message key out of
	 * the local Messages instance.
	 *
	 * @param control
	 *            The control for which a message binding should be created
	 * @param method
	 *            The method that should be bound. Methods that can be bound need to accept one
	 *            String parameter.
	 * @param messageKey
	 *            The key of the message property that should be bound
	 *
	 * @see BaseMessageRegistry#registerProperty(Object, String, String)
	 */
	public void register(final Object control, final String method, final String messageKey) {
		MessageConsumer consumer = createConsumer(control, method);
		MessageSupplier supplier = createSupplier(messageKey);
		//only register if consumer and supplier were created
		if (consumer != null && supplier != null)
			register(consumer, supplier);
	}

	/**
	 * Binds the setter of a property of an object to a message. Doing this the setter of the given
	 * property will be called on the instance with the message String as parameter that is
	 * retrieved via message key out of the local Messages instance.
	 *
	 * @param control
	 *            The control for which a message binding should be created
	 * @param property
	 *            The property of the control which should be bound
	 * @param messageKey
	 *            The key of the message property that should be bound
	 *
	 * @see BaseMessageRegistry#register(Object, String, String)
	 */
	public void registerProperty(final Object control, final String property, final String messageKey) {
		MessageConsumer consumer = createConsumer(control, "set" + Character.toUpperCase(property.charAt(0)) + property.substring(1));
		MessageSupplier supplier = createSupplier(messageKey);
		//only register if consumer and supplier were created
		if (consumer != null && supplier != null)
			register(consumer, supplier);
	}

	/**
	 * This method performs the localization update for all bound objects.
	 * <p>
	 * Typically this method is overriden by a concrete implementation where the Messages instance
	 * is injected via &#064;Inject and &#064;Translation.
	 * </p>
	 *
	 * @param messages
	 *            The new Messages instance that should be used to update the localization.
	 */
	public void updateMessages(M messages) {
		//remember the current message instance
		this.messages = messages;
		//iterate over all registered consumer
		for (Map.Entry<MessageConsumer, MessageSupplier> entry : bindings.entrySet()) {
			entry.getKey().accept(entry.getValue().get());
		}
	}

	/**
	 *
	 * @param control
	 *            The control on which the created consumer should operate
	 * @param method
	 *            The method the created consumer should call to set the new value
	 * @return A MessageConsumer that sets a value to the property of the given control
	 */
	protected MessageConsumer createConsumer(final Object control, final String method) {
		MessageConsumer consumer = null;

		try {
			final Method m = control.getClass().getMethod(method, String.class);
			if (m != null) {

				consumer = new MessageConsumer() {

					@SuppressWarnings({"unchecked", "rawtypes"})
					@Override
					public void accept(final String value) {
						try {
							// ensure the method is accessible so the registry
							// also works well with protected or package
							// protected classes
							if (System.getSecurityManager() == null) {
								m.setAccessible(true);
								m.invoke(control, value);
							} else {
								AccessController
										.doPrivileged(new PrivilegedAction() {

											@Override
											public Object run() {
												m.setAccessible(true);
												try {
													m.invoke(control, value);
												} catch (Exception e) {
													// if anything fails on
													// invoke we unregister the
													// binding to avoid further
													// issues e.g. this can
													// happen in case of
													// disposed SWT controls
													bindings.remove(this);
													if (logService != null)
														logService
																.log(LogService.LOG_INFO,
																		"Error on invoke '"
																				+ m.getName()
																				+ "' on '"
																				+ control
																						.getClass()
																				+ "' with error message '"
																				+ e.getMessage()
																				+ "'. Binding is removed.");
												}
												return null;
											}

										});
							}
						} catch (Exception e) {
							//if anything fails on invoke we unregister the binding
							//to avoid further issues
							//e.g. this can happen in case of disposed SWT controls
							bindings.remove(this);
							if (logService != null)
								logService.log(LogService.LOG_INFO,
										"Error on invoke '" + m.getName() + "' on '"
												+ control.getClass()
												+ "' with error message '" + e.getMessage()
												+ "'. Binding is removed.");
						}
					}
				};

			}
		} catch (NoSuchMethodException e) {
			if (logService != null)
				logService.log(LogService.LOG_WARNING,
						"The method '" + e.getMessage()
								+ "' does not exist. Binding is not created!");
		} catch (SecurityException e) {
			if (logService != null)
				logService.log(
						LogService.LOG_WARNING,
						"Error on accessing method '" + method + "' on class '"
								+ control.getClass() + "' with error message '" + e.getMessage()
								+ "'. Binding is not created!");
		}

		return consumer;
	}

	/**
	 *
	 * @param messageKey
	 *            The name of the field that should be accessed
	 * @return A MessageSupplier that returns the message value for the given message key
	 */
	protected MessageSupplier createSupplier(final String messageKey) {
		MessageSupplier supplier = null;

		try {
			final Field f = messages.getClass().getField(messageKey);
			if (f != null) {
				supplier = new MessageSupplier() {

					@Override
					public String get() {
						String message = null;
						try {
							message = (String) f.get(messages);
						} catch (Exception e) {
							// if anything fails on invoke we unregister the binding
							// to avoid further issues
							// e.g. this can happen in case of disposed SWT controls
							bindings.remove(this);
							if (logService != null)
								logService.log(
										LogService.LOG_INFO,
										"Error on invoke '" + f.getName() + "' on '"
												+ messages.getClass() + "' with error message '"
												+ e.getMessage() + "'. Binding is removed.");
						}

						return message;
					}
				};
			}
		} catch (NoSuchFieldException e) {
			if (logService != null)
				logService.log(LogService.LOG_WARNING, "The class '"
						+ this.messages.getClass().getName()
						+ "' does not contain a field with name '" + e.getMessage()
					+ "'. Binding is not created!");
		} catch (SecurityException e) {
			if (logService != null)
				logService.log(
						LogService.LOG_WARNING,
						"Error on accessing field '" + messageKey + "' on class '"
								+ messages.getClass() + "' with error message '" + e.getMessage()
								+ "'. Binding is not created!");
		}

		return supplier;
	}

	@PreDestroy
	void unregister() {
		this.bindings.clear();
	}
}
