package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;

/**
 * Implementation class for CommandRequestDeviceExecutor
 */
public class CommandRequestDeviceExecutorImpl extends
        CommandRequestExecutorBase<CommandRequestDevice> implements
        CommandRequestDeviceExecutor {

    private Logger log = YukonLogManager.getLogger(CommandRequestDeviceExecutorImpl.class);

    protected Request buildRequest(CommandRequestDevice commandRequest) {
        Request request = new Request();
        request.setCommandString(commandRequest.getCommand());
        request.setDeviceID(commandRequest.getDevice().getDeviceId());
        long requestId = RandomUtils.nextInt();
        request.setUserMessageID(requestId);
        int priority = commandRequest.isBackgroundPriority() ? getDefaultBackgroundPriority()
                : getDefaultForegroundPriority();
        request.setPriority(priority);
        log.debug("Built request '" + commandRequest.getCommand() + "' for device " + commandRequest.getDevice() + " with user id " + requestId);
        return request;
    }

    public CommandResultHolder execute(YukonDevice device, String command,
            LiteYukonUser user) throws Exception {

        CommandRequestDevice cmdRequest = new CommandRequestDevice();
        cmdRequest.setDevice(device);

        String commandStr = command;
        commandStr += " update";
        commandStr += " noqueue";
        cmdRequest.setCommand(commandStr);
        return execute(cmdRequest, user);
    }

}
