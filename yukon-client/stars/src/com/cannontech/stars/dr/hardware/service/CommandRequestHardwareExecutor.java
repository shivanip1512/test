package com.cannontech.stars.dr.hardware.service;

import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.hardware.model.Thermostat;

/**
 * Interface used to execute command requests for hardware
 */
public interface CommandRequestHardwareExecutor {

	/**
	 * Method to execute a command for a given hardware and user.  This method
	 * will use the hardware's route if it is not 0 or the energy company default route if the 
	 * hardware's route is 0.
	 * @param hardware - Hardware to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @return Results of executing command
	 * @throws CommandCompletionException
	 * @throws PaoAuthorizationException - When user doesn't have permission to
     *             execute the command
	 */
	public CommandResultHolder execute(LiteStarsLMHardware hardware, String command,
			LiteYukonUser user) throws CommandCompletionException;

	/**
	 * Method to execute a command for a given thermostat and user.  This method
	 * will use the thermostat's route if it is not 0 or the energy company default route if the 
	 * thermostat's route is 0.
	 * @param thermostat - Thermostat to execute command for
	 * @param command - Command to execute
	 * @param user - User executing command
	 * @return Results of executing command
	 * @throws CommandCompletionException
	 * @throws PaoAuthorizationException - When user doesn't have permission to
	 *             execute the command
	 */
	public CommandResultHolder execute(Thermostat thermostat, String command,
			LiteYukonUser yukonUser) throws CommandCompletionException;
}
