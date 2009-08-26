package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandRequestRouteAndDevice;
import com.cannontech.common.device.commands.CommandRequestRouteAndDeviceExecutor;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.message.porter.message.Request;

public class CommandRequestRouteAndDeviceExecutorImpl extends CommandRequestExecutorBase<CommandRequestRouteAndDevice> implements CommandRequestRouteAndDeviceExecutor {

    private Logger log = YukonLogManager.getLogger(CommandRequestRouteAndDeviceExecutorImpl.class);
    
    @Override
    protected Request buildRequest(CommandRequestRouteAndDevice commandRequest) {

        String command = commandRequest.getCommand();
        int routeId = commandRequest.getRouteId();
        int deviceId = commandRequest.getDevice().getPaoIdentifier().getPaoId();
        long requestId = RandomUtils.nextInt();
        int priority = commandRequest.isBackgroundPriority() ? getDefaultBackgroundPriority() : getDefaultForegroundPriority();
        
        Request request = new Request();
        request.setCommandString(command);
        request.setRouteID(routeId);
        request.setDeviceID(deviceId);
        request.setUserMessageID(requestId);
        request.setPriority(priority);
        
        log.debug("Built request '" + command + "' for route " + routeId + ", deviceID " + deviceId + " with msg id " + requestId);
        
        return request;
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
