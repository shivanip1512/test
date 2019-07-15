package com.cannontech.common.util.jms.api;

/**
 * The pattern of JMS messages that comprise a complete transaction for a particular feature.
 */
public enum JmsCommunicationPattern {
    /** 
     * The sender sends a notification message, with no response expected from the receiver.
     */
    NOTIFICATION("Notification",
                 "The sender sends a notification message, with no response expected from the receiver."),
    
    /** 
     * The sender sends a request message, and the receiver replies with a response message to the sender.
     */
    REQUEST_RESPONSE("Request-Response",
                     "The sender sends a request message, and the receiver replies with a response message to the sender."),
    
    /**
     * 1. The sender sends a request message.<br> 
     * 2. The receiver sends an initial response (ack), usually to indicate that the request was received.<br>
     * 3. Possibly some time later, the receiver sends a full response indicating the result of the operation.
     */
    REQUEST_ACK_RESPONSE("Request-Acknowledgement-Response",
                         "1. The sender sends a request message.\r\n" + 
                         "2. The receiver sends an initial response (ack), usually to indicate that the request was received.\r\n" + 
                         "3. Possibly some time later, the receiver sends a full response indicating the result of the operation."),
    
    /**
     * The sender sends a request message, and the receiver replies with one or more responses.
     */
    REQUEST_MULTI_RESPONSE("Request-Multi-Response",
                           "The sender sends a request message, and the receiver replies with one or more responses.")
    ;
    
    private final String niceString;
    private final String helpString;
    
    private JmsCommunicationPattern(String niceString, String helpString) {
        this.niceString = niceString;
        this.helpString = helpString;
    }
    
    @Override
    public String toString() {
        return niceString;
    }
    
    public String helpString() {
        return helpString;
    }
}
