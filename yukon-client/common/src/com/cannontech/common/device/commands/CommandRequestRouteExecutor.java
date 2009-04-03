package com.cannontech.common.device.commands;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute command requests for a route
 */
public interface CommandRequestRouteExecutor extends
		CommandRequestExecutor<CommandRequestRoute> {

	/**
	 * Convenience method to execute a command on a given route for a user (this method
     * will block until command execution is complete)
	 * @param routeId - Route to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @return Results of executing command
	 * @throws CommandCompletionException
	 * @throws PaoAuthorizationException - When user doesn't have permission to
     *             execute the command
	 */
	public CommandResultHolder execute(int routeId, String command,
			LiteYukonUser user) throws CommandCompletionException;
	
	/**
	 * Convenience method to execute a command on a given route for a user
	 * @param routeId - Route to execute command for
	 * @param command - Command to execute
	 * @param callback - Callback called as results of executing command come in
	 * @param user - User executing command
	 * @throws CommandCompletionException
	 * @throws PaoAuthorizationException - When user doesn't have permission to
	 *             execute the command
	 */
	public void execute(int routeId, String command,
			CommandCompletionCallback<? super CommandRequestRoute> callback,
			LiteYukonUser user);
}
