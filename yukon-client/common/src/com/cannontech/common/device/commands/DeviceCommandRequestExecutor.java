package com.cannontech.common.device.commands;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute command requests for devices
 */
public interface DeviceCommandRequestExecutor extends
        CommandRequestExecutor<CommandRequestDevice> {

    /**
     * Method to execute a command for a given device
     * @param device - Device to execute command for
     * @param command - Command to execute
     * @param user - User executing the command
     * @return Results of command execution
     * @throws Exception
     */
    public CommandResultHolder execute(YukonDevice device, String command,
            LiteYukonUser user) throws Exception;

}
