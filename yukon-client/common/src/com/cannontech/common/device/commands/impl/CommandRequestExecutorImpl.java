package com.cannontech.common.device.commands.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequest;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.BasicServerConnection;

public class CommandRequestExecutorImpl implements CommandRequestExecutor {
    private BasicServerConnection porterConnection;
    private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    private PaoDao paoDao;
    private PaoCommandAuthorizationService commandAuthorizationService;
    
    private int defaultForegroundPriority = 14;
    private int defaultBackgroundPriority = 6;
    
    private Logger log = YukonLogManager.getLogger(CommandRequestExecutorImpl.class);

    private final class CommandResultMessageListener implements MessageListener {
        private final CommandCompletionCallback callback;
        private Map<Long, Request> pendingUserMessageIds;

        private CommandResultMessageListener(List<Request> requests, CommandCompletionCallback callback) {
            this.callback = callback;
            pendingUserMessageIds = new HashMap<Long, Request>(requests.size());
            for (Request request : requests) {
                pendingUserMessageIds.put(request.getUserMessageID(), request);
            }
        }

        public synchronized void messageReceived(MessageEvent e) {
            Message message = e.getMessage();
            if (message instanceof Return) {
                Return retMessage = (Return) message;
                long userMessageId = retMessage.getUserMessageID();
                if (pendingUserMessageIds.containsKey(userMessageId)) {
                    log.debug("Got return message " + retMessage + " for my id on " + this);
                    // this is one of ours, check the status message
                    int status = retMessage.getStatus();
                    if (status != 0) {
                        DeviceErrorDescription description = deviceErrorTranslatorDao.translateErrorCode(status);
                        description.setPorter(retMessage.getResultString());
                        log.debug("Calling receivedError on " + callback + " for " + retMessage);
                        callback.receivedError(description);
                    } 
                    Vector resultVector = retMessage.getVector();
                    for (Object aResult : resultVector) {
                        if (aResult instanceof PointData) {
                            PointData pData = (PointData) aResult;
                            log.debug("Calling receivedValue on " + callback + " for " + retMessage);
                            callback.receivedValue(pData);
                        } else {
                            handleUnknownReturn(userMessageId, aResult);
                        }
                    }
                    
                    log.debug("Calling receivedResultString on " + callback + " for " + retMessage);
                    callback.receivedResultString(retMessage.getResultString());
                    
                    if (retMessage.getExpectMore() == 0) {
                        String resultString = retMessage.getResultString();
                        log.debug("Calling receivedLastResultString on " + callback + " for " + retMessage);
                        callback.receivedLastResultString(resultString);
                        pendingUserMessageIds.remove(userMessageId);
                    }
                    
                }
                
                // an argument could be made that this belongs in the above if,
                // but I think this makes it a little more resiliant to strange errors
                if (pendingUserMessageIds.isEmpty()) {
                    callback.complete();
                    
                    log.debug("Removing porter message listener because pending list is empty: " + this);
                    porterConnection.removeMessageListener(this);
                }
            }
        }

        private void handleUnknownReturn(long userMessageID, Object aResult) {
            // figure out what the command was
            String origCommand = "unknown-command";
            Request request = pendingUserMessageIds.get(userMessageID);
            if (request != null) {
                origCommand = request.getCommandString();
            }
            log.warn("received a message in the result vector that wasn't a PointData: "  
                     + aResult.getClass().toString()
                     + "; original command was: " + origCommand);
        }
        
        @Override
        public String toString() {
            ToStringCreator tsc = new ToStringCreator(this);
            tsc.append("callback", callback);
            tsc.append("pendingUserMessageIds", pendingUserMessageIds.keySet());
            return tsc.toString(); 
        }
    }

    private final class WaitableCommandCompletionCallback extends CollectingCommandCompletionCallback {
        private final CountDownLatch latch = new CountDownLatch(1);

        private WaitableCommandCompletionCallback() {
        }

        public void complete() {
                latch.countDown();
        }
        
        public void waitForCompletion(long time) throws InterruptedException, TimeoutException {
            log.debug("Starting await on " + Thread.currentThread() + " for " + time);
            boolean success = latch.await(time, TimeUnit.SECONDS);
            log.debug("Finished await on " + Thread.currentThread() + " with " + success);
            if (!success) {
                throw new TimeoutException("Commander command execution did not complete with " + time + " seconds");
            }
        }
        
        @Override
        public String toString() {
            return new ToStringCreator(this).toString();
        }
    }

    public CommandResultHolder execute(YukonDevice device, String command, LiteYukonUser user) throws Exception {
        CommandRequest cmdRequest = new CommandRequest();
        cmdRequest.setDeviceId(device.getDeviceId());
        
        String commandStr = command;
        commandStr += " update";
        commandStr += " noqueue";
        cmdRequest.setCommand(commandStr);
        return execute(cmdRequest, user);
    }
    
    public CommandResultHolder execute(CommandRequest command, LiteYukonUser user)  throws CommandCompletionException, PaoAuthorizationException {
        return execute(Collections.singletonList(command), user);
    }

    public CommandResultHolder execute(List<CommandRequest> commands, LiteYukonUser user) throws CommandCompletionException, PaoAuthorizationException {
        WaitableCommandCompletionCallback callback = new WaitableCommandCompletionCallback();
        
        execute(commands, callback, user);
        
        try {
            callback.waitForCompletion(60);
            return callback;
        } catch (InterruptedException e) {
            throw new CommandCompletionException("Interrupted while block for command completion", e);
        } catch (TimeoutException e) {
            throw new CommandCompletionException("Timed out while blocking for command completion", e);
        }
        
    }

    public void execute(List<CommandRequest> commands,
            final CommandCompletionCallback callback,
            LiteYukonUser user) throws PaoAuthorizationException {
        
        log.debug("Executing " + commands.size() + " for " + callback);
        
        if (commands.isEmpty()) {
            log.debug("Skipping execution because there were no commands");
            callback.complete();
            return;
        }

        // build up a list of command requests
        final List<Request> commandRequests = new ArrayList<Request>(commands.size());
        for (CommandRequest command : commands) {
            verifyRequest(command, user);
            Request request = buildRequest(command);
            commandRequests.add(request);
        }
        
        // create listener
        CommandResultMessageListener messageListener = new CommandResultMessageListener(commandRequests, callback);
        log.debug("Addinging porter message listener: " + messageListener);
        porterConnection.addMessageListener(messageListener);
        
        boolean nothingWritten = true;
        try {
            //write requests
            for (Request request : commandRequests) {
                porterConnection.write(request);
                log.debug("Send request to porter: " + request);
                nothingWritten = false;
            }
        } finally {
            if (nothingWritten) {
                log.debug("Removing porter message listener because nothing was written: " + messageListener);
                porterConnection.removeMessageListener(messageListener);
            }
        }

    }
    
    private void verifyRequest(CommandRequest commandReq, LiteYukonUser user) throws PaoAuthorizationException {
        String command = commandReq.getCommand();
        int deviceId = commandReq.getDeviceId();
        LiteYukonPAObject liteYukonPAO = paoDao.getLiteYukonPAO(deviceId);
        commandAuthorizationService.verifyAuthorized(user, command, liteYukonPAO);
    }

    private Request buildRequest(CommandRequest command) {
        Request request = new Request();
        request.setCommandString(command.getCommand());
        request.setDeviceID(command.getDeviceId());
        long requestId = RandomUtils.nextInt();
        request.setUserMessageID(requestId);
        int priority = command.isBackgroundPriority() ? defaultBackgroundPriority : defaultForegroundPriority;
        request.setPriority(priority);
        log.debug("Built request '" + command.getCommand() + "' for device " + command.getDeviceId() + " with user id " + requestId);
        return request;
    }
    
    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    
    @Required
    public void setCommandAuthorizationService(PaoCommandAuthorizationService commandAuthorizationService) {
        this.commandAuthorizationService = commandAuthorizationService;
    }
    
    @Required
    public void setDeviceErrorTranslatorDao(DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setDefaultBackgroundPriority(int defaultBackgroundPriority) {
        this.defaultBackgroundPriority = defaultBackgroundPriority;
    }

    public void setDefaultForegroundPriority(int defaultForegroundPriority) {
        this.defaultForegroundPriority = defaultForegroundPriority;
    }

}
