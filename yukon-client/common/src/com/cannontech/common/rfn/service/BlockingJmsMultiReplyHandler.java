package com.cannontech.common.rfn.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.cannontech.common.rfn.message.JmsMultiResponse;
import com.cannontech.common.rfn.model.TimeoutExecutionException;
import com.cannontech.common.util.jms.JmsMultiResponseHandler;
import com.cannontech.message.util.TimeoutException;

/**
 * A multi-response reply handler that blocks waiting for all responses before returning the complete result.
 */
public class BlockingJmsMultiReplyHandler<T extends JmsMultiResponse> implements JmsMultiResponseHandler<T> {
    private List<T> replies = new ArrayList<>();
    private boolean timeoutExceeded;
    private Exception exception;
    private boolean isComplete;
    private final Class<T> clazz;
    
    public BlockingJmsMultiReplyHandler(Class<T> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public Class<T> getExpectedType() {
        return clazz;
    }
    
    @Override
    public synchronized void complete() {
        isComplete = true;
        // Notify any thread waiting for completion
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
        replies.add(reply);
    }
    
    /**
     * A blocking request for response data.
     * @throws ExecutionException if an error occurred.
     * @throws TimeoutException if the request timed out.
     */
    public synchronized List<T> waitForCompletion() throws ExecutionException {
        //wait for completion
        while (!isComplete) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                // Can be safely ignored
            }
        }
        
        if (exception != null) {
            throw new ExecutionException(exception);
        } else if (timeoutExceeded) {
            throw new TimeoutExecutionException();
        }
        
        return replies;
    }
}
