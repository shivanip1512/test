package com.cannontech.stars.dr.hardware.service;

import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestExecutionTemplate;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

/**
 * Interface used to execute command requests for hardware
 */
public interface LmHardwareCommandRequestExecutor {

	/**
	 * Method to execute a command (asynchronously) for a given hardware and user.  This method
	 * will use the hardware's route if it is not 0 or the energy company default route if the 
	 * hardware's route is 0. Logs results.
	 * @param hardware - Hardware to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @throws CommandCompletionException
	 */
	public void execute(LiteLmHardwareBase hardware, String command, LiteYukonUser user) throws CommandCompletionException;

    /**
     * Execute a command asynchronously, using the specified callback for command completion. As in
     * the other method, this method will use the hardware's route if it isn't 0 or the energy
     * company default route otherwise.
     * @throws CommandCompletionException
     */
    public void execute(LiteLmHardwareBase hardware, 
                        String command, 
                        LiteYukonUser user, 
                        CommandCompletionCallback<CommandRequestRoute> callback) throws CommandCompletionException;

    /**
     * Execute a command asynchronously, using the specified execution template and callback for
     * command completion. As in the other method, this method will use the hardware's route if it
     * isn't 0 or the energy company default route otherwise.
     * @throws CommandCompletionException
     */
    public void executeWithTemplate(CommandRequestExecutionTemplate<CommandRequestRoute> template,
            LiteLmHardwareBase hardware, 
            String command,
            CommandCompletionCallback<CommandRequestRoute> callback) throws CommandCompletionException;

	/**
	 * Method to execute a command (asynchronously) for a given device and user.  This method
	 * will use the device's route if it is not 0 or the energy company default route if the 
	 * device's route is 0. Logs results.
	 * @param inventoryId - id of inventory to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @throws CommandCompletionException
	 */
	public void execute(int inventoryId, String command, LiteYukonUser yukonUser) throws CommandCompletionException;

	/**
	 * Attempts to execute only on the route provided, used by batch file command process.
	 */
	public void executeOnRoute(String command, int routeId, LiteYukonUser user) throws CommandCompletionException;
	
}