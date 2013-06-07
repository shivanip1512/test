package com.cannontech.cbc.commands;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.capcontrol.ResponseType;
import com.cannontech.messaging.message.capcontrol.CommandResultParticipant;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.messaging.message.capcontrol.ServerResponseMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;

public class CapControlCommandExecutor {
    
	private CapControlCache cache;
	private Executor executor;

    public void execute(final BaseMessage message) throws CommandExecutionException {
        try {
            cache.getConnection().write(message);
        } catch (ConnectionException e) {
            throw new CommandExecutionException("Unable to send command", e); 
        }
    }
    
    public <T extends BaseMessage & CommandResultParticipant> void execute(final T message, final CommandResultCallback callback) {
        executor.execute(new Runnable() {
            public void run() {
                
                CommandMessageListener messageListener = new CommandMessageListener(callback, message.getMessageId());
                cache.getConnection().addMessageListener(messageListener);
                try {
                    cache.getConnection().write(message);
                } catch (ConnectionException e) {
                    callback.processingExceptionOccured(e.getMessage());
                    messageListener.removeListener();
                }
                
            }
        });
    }
    
    public <Q extends BaseMessage & CommandResultParticipant, R extends ServerResponseMessage> R blockingExecute(Q messageToSend, Class<R> returnType, CommandResultCallback callback) {
        
        WaitableCommandResultCallback waitable = new WaitableCommandResultCallback(callback);
        execute(messageToSend, waitable);
        try {
            if (!waitable.await(60)) {
                ServerResponseMessage timeout = new ServerResponseMessage();
                timeout.setMessageId(messageToSend.getMessageId());
                timeout.setResponseType(ResponseType.TIMEOUT);
                return returnType.cast(timeout);
            }
        } catch (InterruptedException e) {/* Ignore */}
        
        BaseMessage response = waitable.getResponse();
        if (returnType.isInstance(response)) {
            R message = returnType.cast(response);
            return message;
        }
        throw new IllegalArgumentException();
    }
    
    private class CommandMessageListener implements MessageListener {
        private CommandResultCallback callback;
        private long messageId;
        
        public CommandMessageListener(CommandResultCallback callback, long messageId) {
            this.callback = callback;
            this.messageId = messageId;
        }
        
        @Override
        public void messageReceived(MessageEvent e) {
            if (e.getMessage() instanceof ServerResponseMessage) {
                ServerResponseMessage message = (ServerResponseMessage)e.getMessage();
                if (message.getMessageId() == messageId) {
                    callback.recievedResponse(message);
                    this.removeListener();
                }
            }
        }
        
        public void removeListener() {
            cache.getConnection().removeMessageListener(this);
        }
    }
    
    @Autowired
    public void setCapControlCache(CapControlCache cache) {
        this.cache = cache;
    }
    
    @PostConstruct
    public void init() {
        executor = Executors.newScheduledThreadPool(4);
    }
    
}