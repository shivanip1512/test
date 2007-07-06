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

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.CollectingCommandCompletionCallback;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequest;
import com.cannontech.common.device.commands.CommandRequestExecutor;
import com.cannontech.common.device.commands.CommandResultHolder;
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
                    // this is one of ours, check the status message
                    int status = retMessage.getStatus();
                    if (status != 0) {
                        DeviceErrorDescription description = deviceErrorTranslatorDao.translateErrorCode(status);
                        description.setPorter(retMessage.getResultString());
                        callback.receivedError(description);
                    } 
                    Vector resultVector = retMessage.getVector();
                    for (Object aResult : resultVector) {
                        if (aResult instanceof PointData) {
                            PointData pData = (PointData) aResult;
                            callback.receivedValue(pData);
                        } else {
                            handleUnknownReturn(userMessageId, aResult);
                        }
                    }
                    
                    callback.receivedResultString(retMessage.getResultString());
                    
                    if (retMessage.getExpectMore() == 0) {
                        String resultString = retMessage.getResultString();
                        callback.receivedLastResultString(resultString);
                        pendingUserMessageIds.remove(userMessageId);
                    }
                    
                }
                
                // an argument could be made that this belongs in the above if,
                // but I think this makes it a little more resiliant to strange errors
                if (pendingUserMessageIds.isEmpty()) {
                    callback.complete();
                    
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
    }

    private final class WaitableCommandCompletionCallback extends CollectingCommandCompletionCallback {
        private final CountDownLatch latch = new CountDownLatch(1);

        private WaitableCommandCompletionCallback() {
        }

        public void complete() {
                latch.countDown();
        }
        
        public void waitForCompletion(long time) throws InterruptedException, TimeoutException {
            boolean success = latch.await(time, TimeUnit.SECONDS);
            if (!success) {
                throw new TimeoutException("Commander command execution did not complete with " + time + " seconds");
            }
        }
    }

    public CommandResultHolder execute(CommandRequest command)  throws CommandCompletionException {
        return execute(Collections.singletonList(command));
    }

    public CommandResultHolder execute(List<CommandRequest> commands) throws CommandCompletionException {
        WaitableCommandCompletionCallback callback = new WaitableCommandCompletionCallback();
        
        execute(commands, callback);
        
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
            final CommandCompletionCallback callback) {

        // build up a list of command requests
        final List<Request> commandRequests = new ArrayList<Request>(commands.size());
        for (CommandRequest command : commands) {
            Request request = buildRequest(command);
            commandRequests.add(request);
        }
        
        // create listener
        CommandResultMessageListener messageListener = new CommandResultMessageListener(commandRequests, callback);
        porterConnection.addMessageListener(messageListener);
        
        boolean nothingWritten = true;
        try {
            //write requests
            for (Request request : commandRequests) {
                porterConnection.write(request);
                nothingWritten = false;
            }
        } finally {
            if (nothingWritten) {
                porterConnection.removeMessageListener(messageListener);
            }
        }

    }

    private Request buildRequest(CommandRequest command) {
        Request request = new Request();
        request.setCommandString(command.getCommand());
        request.setDeviceID(command.getDeviceId());
        long requestId = RandomUtils.nextInt();
        request.setUserMessageID(requestId);
        return request;
    }
    
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
    
    public void setDeviceErrorTranslatorDao(
            DeviceErrorTranslatorDao deviceErrorTranslatorDao) {
        this.deviceErrorTranslatorDao = deviceErrorTranslatorDao;
    }

}
