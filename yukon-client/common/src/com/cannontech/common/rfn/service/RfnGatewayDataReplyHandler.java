package com.cannontech.common.rfn.service;

import java.util.concurrent.ExecutionException;

import com.cannontech.common.rfn.message.gateway.GatewayDataResponse;
import com.cannontech.common.util.jms.JmsReplyHandler;

/**
 * Gateway data reply handler that blocks for completion.
 */
public class RfnGatewayDataReplyHandler implements JmsReplyHandler<GatewayDataResponse> {
    private GatewayDataResponse reply;
    private boolean timeoutExceeded;
    private Exception exception;
    private boolean isComplete;
    
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
    public void handleReply(GatewayDataResponse reply) {
        this.reply = reply;
    }
    
    @Override
    public Class<GatewayDataResponse> getExpectedType() {
        return GatewayDataResponse.class;
    }
    
    /**
     * A blocking request for response data.
     * @throws ExecutionException if the request timed out, or if an error occurred.
     */
    public synchronized GatewayDataResponse waitForCompletion() throws ExecutionException {
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
            throw new GatewayDataResponseException("Timed out waiting for GatewayDataResponse.");
        }
        
        //shouldn't be able to get here
        return null;
    }
    
    /**
     * Subclass of ExecutionException that lets us set the message.
     */
    private class GatewayDataResponseException extends ExecutionException {
        public GatewayDataResponseException(String message) {
            super(message);
        }
    }
}