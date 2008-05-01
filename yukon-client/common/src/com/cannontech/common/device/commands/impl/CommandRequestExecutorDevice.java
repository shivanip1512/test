package com.cannontech.common.device.commands.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.common.device.commands.DeviceCommandRequestExecutor;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;

/**
 * Implementation class for DeviceCommandRequestExecutor
 */
public class CommandRequestExecutorDevice extends
        CommandRequestExecutorBase<CommandRequestDevice> implements
        DeviceCommandRequestExecutor {

    private PaoDao paoDao;
    private PaoCommandAuthorizationService commandAuthorizationService;
    
    private Logger log = YukonLogManager.getLogger(CommandRequestExecutorDevice.class);

    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setCommandAuthorizationService(
            PaoCommandAuthorizationService commandAuthorizationService) {
        this.commandAuthorizationService = commandAuthorizationService;
    }

    protected void verifyRequest(CommandRequestDevice commandRequest,
            LiteYukonUser user) throws PaoAuthorizationException {

        String command = commandRequest.getCommand();
        int deviceId = commandRequest.getDeviceId();
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(deviceId);
        commandAuthorizationService.verifyAuthorized(user,
                                                     command,
                                                     liteYukonPAO);
    }

    protected Request buildRequest(CommandRequestDevice commandRequest) {
        Request request = new Request();
        request.setCommandString(commandRequest.getCommand());
        request.setDeviceID(commandRequest.getDeviceId());
        long requestId = RandomUtils.nextInt();
        request.setUserMessageID(requestId);
        int priority = commandRequest.isBackgroundPriority() ? getDefaultBackgroundPriority()
                : getDefaultForegroundPriority();
        request.setPriority(priority);
        log.debug("Built request '" + commandRequest.getCommand() + "' for device " + commandRequest.getDeviceId() + " with user id " + requestId);
        return request;
    }

    public CommandResultHolder execute(YukonDevice device, String command,
            LiteYukonUser user) throws Exception {

        CommandRequestDevice cmdRequest = new CommandRequestDevice();
        cmdRequest.setDeviceId(device.getDeviceId());

        String commandStr = command;
        commandStr += " update";
        commandStr += " noqueue";
        cmdRequest.setCommand(commandStr);
        return execute(cmdRequest, user);
    }

}
