package com.cannontech.cbc.commands;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.message.capcontrol.model.CapControlResponseType;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.cannontech.message.capcontrol.model.CommandResultParticipant;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

public class CapControlCommandExecutor {
    
	private CapControlCache cache;
	private Executor executor;

    public void execute(final Message message) throws CommandExecutionException {
        try {
            cache.getConnection().write(message);
        } catch (ConnectionException e) {
            throw new CommandExecutionException("Unable to send command", e); 
        }
    }
    
    public <T extends Message & CommandResultParticipant> void execute(final T message, final CommandResultCallback callback) {
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
    
    public <Q extends Message & CommandResultParticipant, R extends CapControlServerResponse> R blockingExecute(Q messageToSend, Class<R> returnType, CommandResultCallback callback) {
        
        WaitableCommandResultCallback waitable = new WaitableCommandResultCallback(callback);
        execute(messageToSend, waitable);
        try {
            if (!waitable.await(60)) {
                CapControlServerResponse timeout = new CapControlServerResponse();
                timeout.setMessageId(messageToSend.getMessageId());
                timeout.setResponseType(CapControlResponseType.TIMEOUT);
                return returnType.cast(timeout);
            }
        } catch (InterruptedException e) {/* Ignore */}
        
        Message response = waitable.getResponse();
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
            if (e.getMessage() instanceof CapControlServerResponse) {
                CapControlServerResponse message = (CapControlServerResponse)e.getMessage();
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