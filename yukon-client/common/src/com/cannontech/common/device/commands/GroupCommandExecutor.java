package com.cannontech.common.device.commands;

import java.util.Set;

import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Interface used to execute a command for a group of devices
 */
public interface GroupCommandExecutor {

    /**
     * Method to execute a command for each device in a set
     * @param deviceIds - Ids of devices to execute command for
     * @param command - Command to execute
     * @param emailAddresses - Addresses to send results to
     * @param emailSubject - Subject of result email
     * @param user - User executing commands
     * @throws PaoAuthorizationException - When user doesn't have permissing to
     *             execute the command
     */
    public void execute(Set<Integer> deviceIds, String command,
            String emailAddresses, String emailSubject, LiteYukonUser user)
            throws PaoAuthorizationException;

}
