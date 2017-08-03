package com.cannontech.common.util.jms.api;

/**
 * The pattern of JMS messages that comprise a complete transaction for a particular feature.
 */
public enum JmsCommunicationPattern {
    /** 
     * The sender sends a notification message, with no response expected from the receiver.
     */
    NOTIFICATION,
    
    /** 
     * The sender sends a request message, and the receiver replies with a response message to the sender.
     */
    REQUEST_RESPONSE,
    
    /** 
     * 1. The sender sends a request message.<br> 
     * 2. The receiver sends an initial response (ack), usually to indicate that the request was received.<br>
     * 3. Possibly some time later, the receiver sends a full response indicating the result of the operation.
     */
    REQUEST_ACK_RESPONSE,
    ;
}
