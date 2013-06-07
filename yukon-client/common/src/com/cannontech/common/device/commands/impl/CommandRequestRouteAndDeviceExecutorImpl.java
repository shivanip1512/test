package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.CommandRequestRouteAndDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.messaging.message.porter.RequestMessage;

public class CommandRequestRouteAndDeviceExecutorImpl extends CommandRequestExecutorBase<CommandRequestRouteAndDevice> implements CommandRequestRouteAndDeviceExecutor {

    private Logger log = YukonLogManager.getLogger(CommandRequestRouteAndDeviceExecutorImpl.class);
    
    @Override
    protected void adjustRequest(RequestMessage request, CommandRequestRouteAndDevice commandRequest) {

        long requestId = RandomUtils.nextInt();
        int routeId = commandRequest.getRouteId();
        int deviceId = commandRequest.getDevice().getPaoIdentifier().getPaoId();
        
        request.setRouteId(routeId);
        request.setDeviceId(deviceId);
        request.setUserMessageId(RandomUtils.nextInt());
        
        String debugStr = "Built request: " +
        "command = " + request.getCommandString() + " " +
        "routeId = " + routeId + " " +
        "deviceId = " + deviceId + " " +
        "userMessageId = " + requestId;
        log.debug(debugStr);
    }
    
    @Override
    protected CommandRequestType getCommandRequestType() {
    	return CommandRequestType.DEVICE_ROUTE;
    }
    
    @Override
    public void applyIdsToCommandRequestExecutionResult(CommandRequestRouteAndDevice commandRequest, CommandRequestExecutionResult commandRequestExecutionResult) {
        commandRequestExecutionResult.setDeviceId(commandRequest.getDevice().getPaoIdentifier().getPaoId());
        commandRequestExecutionResult.setRouteId(commandRequest.getRouteId());
    }
}
