package com.cannontech.common.rfn.message;

import java.io.Serializable;

import com.cannontech.common.util.jms.RequestMultiReplyTemplate;

/**
 * Interface for responses to a JMS request, where each request may have multiple responses. The number of expected
 * responses does not need to be known at the time of request.
 * @see RequestMultiReplyTemplate
 */
public interface JmsMultiResponse extends Serializable {

    /**
     * @return the requestId, which can be used to correlate this response to the original request.
     */
    public String getRequestId();

    /**
     * @return the total number of responses to expect for this requestId.
     */
    public int getTotalSegments();

    /**
     * @return the "index" of this response in relation to all responses for this requestId, which can be used to
     * order the responses. The first segment number is 1. The last segment number is equal to <code>getTotalSegments()
     * </code>
     */
    public int getSegmentNumber();
    
}
