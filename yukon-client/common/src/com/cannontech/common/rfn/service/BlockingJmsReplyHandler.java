package com.cannontech.common.rfn.service;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import com.cannontech.common.util.jms.JmsReplyHandler;

public class BlockingJmsReplyHandler<T extends Serializable> implements JmsReplyHandler<T> {
    private T reply;
    private boolean timeoutExceeded;
    private Exception exception;
    private boolean isComplete;
    private final Class<T> clazz;
    
    public BlockingJmsReplyHandler(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public synchronized void complete() {
        isComplete = true;
        //notify any thread waiting for completion
        notifyAll();
    }
    
    @Override
    public void handleException(Exception exception)  {
        this.exception = exception;
    }
    
    @Override
    public void handleTimeout() {
        timeoutExceeded = true;
    }
    
    @Override
    public void handleReply(T reply) {
        this.reply = reply;
    }
    
    @Override
    public Class<T> getExpectedType() {
        return clazz;
    }
    
    /**
     * A blocking request for response data.
     * @throws ExecutionException if the request timed out, or if an error occurred.
     */
    public synchronized T waitForCompletion() throws ExecutionException {
        //wait for completion
        while (!isComplete) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                //can be safely ignored
            }
        }
        
        if (reply != null) {
            return reply;
        } else if (exception != null) {
            throw new ExecutionException(exception);
        } else if (timeoutExceeded) {
            throw new TimeoutExecutionException();
        }
        
        //shouldn't be able to get here
        return null;
    }
    
    private static class TimeoutExecutionException extends ExecutionException {
        public TimeoutExecutionException() {
            super("Timed out waiting for GatewayDataResponse.");
        }
    }
}