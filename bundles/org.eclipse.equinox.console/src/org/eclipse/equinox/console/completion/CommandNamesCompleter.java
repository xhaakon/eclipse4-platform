/*******************************************************************************
 * Copyright (c) 2011 SAP AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Lazar Kirchev, SAP AG - initial API and implementation
 *******************************************************************************/

package org.eclipse.equinox.console.completion;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.felix.service.command.CommandSession;
import org.eclipse.equinox.console.commands.CommandsTracker;
import org.eclipse.equinox.console.completion.common.Completer;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class provides completion for command names. 
 *
 */
public class CommandNamesCompleter implements Completer {

	private CommandSession session;
	private ServiceTracker<CommandsTracker, CommandsTracker> commandsTrackerTracker;
	private static final String COMMANDS = ".commands";
	
	public CommandNamesCompleter(BundleContext context, CommandSession session) {
		this.session = session;
		commandsTrackerTracker = new ServiceTracker<CommandsTracker, CommandsTracker>(context, CommandsTracker.class.getName(), null);
		commandsTrackerTracker.open();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getCandidates(String buffer, int cursor) {
		CommandsTracker commandsTracker = commandsTrackerTracker.getService();
		Set<String> commandNames = null;
		if (commandsTracker != null) {
			commandNames = commandsTracker.getCommands();
		}
		
		// CommandSession.get(".commands") returns the names of all registered commands
		if (commandNames == null || commandNames.isEmpty()) {
			commandNames = (Set<String>) session.get(COMMANDS);
		}
		
		// command names are stored in the session in lower case
		String currentToken = CommandLineParser.getCurrentToken(buffer, cursor).toLowerCase();
		if(currentToken == null || currentToken.equals("")) {
			return new HashMap<String, Integer>();
		}
		
		if (!currentToken.contains(":")) {
			// the current token does not contain a scope qualifier, so remove scopes from possible candidates
			commandNames = clearScopes(commandNames);
		} 
		StringsCompleter completer = new StringsCompleter(commandNames, true);
		return completer.getCandidates(buffer, cursor);
	}
	
	private Set<String> clearScopes(Set<String> commandNames) {
		Set<String> clearedCommandNames = new HashSet<String>();
		
		for(String commandName : commandNames) {
			clearedCommandNames.add(commandName.substring(commandName.indexOf(":") + 1));
		}
		
		return clearedCommandNames;
	}
}
