package com.cannontech.stars.dr.hardware.service;

import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.hardware.model.Thermostat;

/**
 * Interface used to execute command requests for hardware
 */
public interface CommandRequestHardwareExecutor {

	/**
	 * Method to execute a command (asynchronously) for a given hardware and user.  This method
	 * will use the hardware's route if it is not 0 or the energy company default route if the 
	 * hardware's route is 0. Logs results.
	 * @param hardware - Hardware to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @throws CommandCompletionException
	 */
	public void execute(LiteStarsLMHardware hardware, String command,
			LiteYukonUser user) throws CommandCompletionException;

    /**
     * Execute a command asynchronously, using the specified callback for command completion. As in
     * the other method, this method will use the hardware's route if it isn't 0 or the energy
     * company default route otherwise.
     * @throws CommandCompletionException
     */
    public void execute(LiteStarsLMHardware hardware, String command, LiteYukonUser user,
            CommandCompletionCallback<CommandRequestRoute> callback)
            throws CommandCompletionException;

	/**
	 * Method to execute a command (asynchronously) for a given thermostat and user.  This method
	 * will use the thermostat's route if it is not 0 or the energy company default route if the 
	 * thermostat's route is 0. Logs results.
	 * @param thermostat - Thermostat to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @throws CommandCompletionException
	 */
	public void execute(Thermostat thermostat, String command,
			LiteYukonUser yukonUser) throws CommandCompletionException;
}
