package com.cannontech.amr.disconnect.service.impl;

import java.util.Set;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.user.YukonUserContext;

public interface DisconnectStrategy {

    /**
     * Filter devices that will accept commands
     */
    FilteredDevices filter(Iterable<SimpleDevice> meters);

    /**
     * Send connect, disconnect or arm command to the given devices.
     */
    public CommandCompletionCallback<CommandRequestDevice> execute(DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, YukonUserContext userContext);

    /**
     * Attempt to cancel the command
     */
    void cancel(DisconnectResult result, YukonUserContext userContext);
    
    /**
     * Returns true if the devices can be armed.
     */
    boolean supportsArm(Iterable<SimpleDevice> meters);
}
