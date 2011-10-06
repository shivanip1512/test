package com.cannontech.common.device.commands.impl;

import java.util.Collections;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;

/**
 * Implementation class for CommandRequestRouteExecutor
 */
public class CommandRequestRouteExecutorImpl extends
        CommandRequestExecutorBase<CommandRequestRoute> implements
        CommandRequestRouteExecutor {

    private Logger log = YukonLogManager.getLogger(CommandRequestRouteExecutorImpl.class);

    @Override
    protected void adjustRequest(Request request, CommandRequestRoute commandRequest) {
        
        long requestId = RandomUtils.nextInt();
        int routeId = commandRequest.getRouteId();
        
        request.setRouteID(routeId);
        request.setUserMessageID(requestId);

        String debugStr = "Built request: " +
        "command = " + request.getCommandString() + " " +
        "routeId = " + routeId + " " +
        "userMessageId = " + requestId;
        log.debug(debugStr);
    }

	@Override
	public CommandResultHolder execute(int routeId, String command, DeviceRequestType type, LiteYukonUser user) throws CommandCompletionException {
		
		CommandRequestRoute commandRequest = new CommandRequestRoute();
        commandRequest.setCommandCallback(new CommandCallbackBase(command));
        commandRequest.setRouteId(routeId);
		
        return execute(commandRequest, type, user);
	}

	@Override
	public CommandRequestExecutionIdentifier execute(int routeId, String command,
			CommandCompletionCallback<? super CommandRequestRoute> callback,
			DeviceRequestType type, LiteYukonUser user) {
		
		CommandRequestRoute commandRequest = new CommandRequestRoute();
        commandRequest.setCommandCallback(new CommandCallbackBase(command));
        commandRequest.setRouteId(routeId);
        
        return execute(Collections.singletonList(commandRequest), callback, type, user);
	}
	
	@Override
    protected CommandRequestType getCommandRequestType() {
    	return CommandRequestType.ROUTE;
    }
	
	@Override
    public void applyIdsToCommandRequestExecutionResult(CommandRequestRoute commandRequest, CommandRequestExecutionResult commandRequestExecutionResult) {
        commandRequestExecutionResult.setRouteId(commandRequest.getRouteId());
    }
}
