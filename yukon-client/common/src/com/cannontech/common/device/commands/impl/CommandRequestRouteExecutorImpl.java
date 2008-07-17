package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CommandRequestRoute;
import com.cannontech.common.device.commands.CommandRequestRouteExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;

/**
 * Implementation class for CommandRequestRouteExecutor
 */
public class CommandRequestRouteExecutorImpl extends
        CommandRequestExecutorBase<CommandRequestRoute> implements
        CommandRequestRouteExecutor {

    private Logger log = YukonLogManager.getLogger(CommandRequestRouteExecutorImpl.class);

    protected void verifyRequest(CommandRequestRoute commandRequest,
            LiteYukonUser user) throws PaoAuthorizationException {
        // Do nothing, currently no way to see if a user is authorized to send a
        // command
    }

    protected Request buildRequest(CommandRequestRoute commandRequest) {
        Request request = new Request();
        request.setCommandString(commandRequest.getCommand());
        request.setRouteID(commandRequest.getRouteId());
        long requestId = RandomUtils.nextInt();
        request.setUserMessageID(requestId);
        int priority = commandRequest.isBackgroundPriority() ? getDefaultBackgroundPriority()
                : getDefaultForegroundPriority();
        request.setPriority(priority);
        log.debug("Built request '" + commandRequest.getCommand() + "' for route " + commandRequest.getRouteId() + " with user id " + requestId);
        return request;
    }

	@Override
	public CommandResultHolder execute(int routeId, String command,
			LiteYukonUser user) throws CommandCompletionException, PaoAuthorizationException {
		
		CommandRequestRoute commandRequest = new CommandRequestRoute();
        commandRequest.setCommand(command);
        commandRequest.setRouteId(routeId);
		
        return execute(commandRequest, user);
	}
}
