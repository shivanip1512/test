package com.cannontech.dr.rfn.message.unicast;

import java.io.Serializable;

/**
 * JMS Queue names:
 * <ul>
 * <li>JMSReplyTo of yukon.qr.obj.dr.rfn.ExpresscommUnicastRequest</li>
 * <li>yukon.qr.obj.dr.rfn.ExpresscommBulkUnicastResponse</li>
 * </ul>
 */
public class RfnExpresscommUnicastReply implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String messageId;
    private RfnExpresscommUnicastReplyType replyType;

    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setReplyType(RfnExpresscommUnicastReplyType replyType) {
        this.replyType = replyType;
    }

    public RfnExpresscommUnicastReplyType getReplyType() {
        return replyType;
    }

    @Override
    public String toString() {
        return String.format("RfnExpresscommUnicastReply [messageId=%s, replyType=%s]",
                             messageId,
                             replyType);
    }
    
}