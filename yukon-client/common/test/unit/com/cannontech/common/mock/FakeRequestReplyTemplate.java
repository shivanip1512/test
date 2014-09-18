package com.cannontech.common.mock;

import java.io.Serializable;

import com.cannontech.common.util.jms.JmsReplyHandler;
import com.cannontech.common.util.jms.RequestReplyTemplate;

/**
 * This fake RequestReplyTemplate can be used to remove dependence on JMS/ActiveMQ messaging. To use, create a class
 * that extends this, and implement a buildResponse() method that returns a response object of the appropriate type.
 * 
 * Call setMode() to set the template to reply with the response object, or throw an ExecutionException
 * or a TimeoutExecutionException.
 * 
 * SendCount can be used to confirm that the code under test has attempted to send an appropriate number of
 * requests.
 */
public abstract class FakeRequestReplyTemplate<T extends Serializable> implements RequestReplyTemplate<T> {
    public static enum Mode {
        REPLY,
        EXCEPTION,
        TIMEOUT;
    }
    
    private Mode mode = Mode.REPLY;
    private int sendCount = 0;
    
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    public int getSendCount() {
        return sendCount;
    }
    
    @Override
    public <Q extends Serializable> void send(Q requestPayload, JmsReplyHandler<T> callback) {
        sendCount++;
        
        switch (mode) {
            case REPLY:
                T response = buildResponse(requestPayload);
                callback.handleReply(response);
                callback.complete();
                break;
            case EXCEPTION:
                callback.handleException(new Exception("Exception thrown for testing purposes"));
                callback.complete();
                break;
            case TIMEOUT:
            default:
                callback.handleTimeout();
                callback.complete();
                break;
        }
    }
    
    protected abstract <Q extends Serializable> T buildResponse(Q request);
}
