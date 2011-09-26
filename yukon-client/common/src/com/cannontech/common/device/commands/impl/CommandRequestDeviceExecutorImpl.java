package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;

/**
 * Implementation class for CommandRequestDeviceExecutor
 */
public class CommandRequestDeviceExecutorImpl extends
        CommandRequestExecutorBase<CommandRequestDevice> implements
        CommandRequestDeviceExecutor {

    private Logger log = YukonLogManager.getLogger(CommandRequestDeviceExecutorImpl.class);

    @Override
    protected void adjustRequest(Request request, CommandRequestDevice commandRequest) {
        
        long requestId = RandomUtils.nextInt();
        int deviceId = commandRequest.getDevice().getPaoIdentifier().getPaoId();
        
        request.setDeviceID(deviceId);
        request.setUserMessageID(requestId);
        
        String debugStr = "Built request: " +
        "command = " + request.getCommandString() + " " +
        "deviceid = " + deviceId + " " +
        "userMessageId = " + requestId;
        log.debug(debugStr);
    }

    @Override
    public CommandResultHolder execute(YukonDevice device, String command, DeviceRequestType type, LiteYukonUser user) throws Exception {

        CommandRequestDevice cmdRequest = new CommandRequestDevice();
        cmdRequest.setDevice(new SimpleDevice(device.getPaoIdentifier()));

        cmdRequest.setCommandCallback(new PorterCommandCallback(command));
        return execute(cmdRequest, type, user);
    }

    @Override
    protected CommandRequestType getCommandRequestType() {
    	return CommandRequestType.DEVICE;
    }
    
    @Override
    public void applyIdsToCommandRequestExecutionResult(CommandRequestDevice commandRequest, CommandRequestExecutionResult commandRequestExecutionResult) {
        commandRequestExecutionResult.setDeviceId(commandRequest.getDevice().getPaoIdentifier().getPaoId());
    }
}
