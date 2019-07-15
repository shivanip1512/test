package com.cannontech.common.util.jms;

import com.cannontech.common.rfn.message.JmsMultiResponse;

/**
 * Callback used in RequestMultiReplyTemplate to handle responses.
 * @see RequestMultiReplyTemplate
 * @see JmsMultiResponseHandlerTemplate
 */
public interface JmsMultiResponseHandler<T extends JmsMultiResponse> {
    
    /**
     * @return The class of response objects that this can handle.
     */
    public Class<T> getExpectedType();
    
    /**
     * Receive and process a response object.
     */
    public void handleReply(T reply);
    
    /**
     * Invoked when the timeout has been exceeded between the request and the first response, or between responses.
     */
    public void handleTimeout();
    
    /**
     * Invoked when an exception occurs during the request-response process.
     */
    public void handleException(Exception e);
    
    /**
     * Invoked when the request-response process has completed, either successfully, or due to timeout or exception.
     */
    public void complete();
}
