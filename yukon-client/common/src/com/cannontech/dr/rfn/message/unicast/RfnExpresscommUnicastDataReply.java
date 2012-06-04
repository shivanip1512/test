package com.cannontech.dr.rfn.message.unicast;

import java.io.Serializable;

/**
 * JMS Queue names:
 * <ul>
 * <li>JMSReplyTo of yukon.qr.obj.dr.rfn.ExpresscommUnicastRequest</li>
 * <li>yukon.qr.obj.dr.rfn.ExpresscommBulkUnicastResponse</li>
 * </ul>
 */
public class RfnExpresscommUnicastDataReply implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String messageId;
    private RfnExpresscommUnicastDataReplyType replyType = RfnExpresscommUnicastDataReplyType.TIMEOUT;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public RfnExpresscommUnicastDataReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnExpresscommUnicastDataReplyType replyType) {
        this.replyType = replyType;
    }

    @Override
    public String toString() {
        return String.format("RfnExpresscommUnicastDataReply [messageId=%s, replyType=%s]",
                             messageId,
                             replyType);
    }

}