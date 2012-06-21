package com.cannontech.dr.rfn.message.unicast;

import java.io.Serializable;

/**
 * JMS Queue names:
 * <ul>
 * <li>JMSReplyTo of yukon.qr.obj.dr.rfn.ExpressComUnicastRequest</li>
 * <li>yukon.qr.obj.dr.rfn.ExpressComBulkUnicastResponse</li>
 * </ul>
 */
public class RfnExpressComUnicastReply implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String messageId;
    private RfnExpressComUnicastReplyType replyType;

    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setReplyType(RfnExpressComUnicastReplyType replyType) {
        this.replyType = replyType;
    }

    public RfnExpressComUnicastReplyType getReplyType() {
        return replyType;
    }

    @Override
    public String toString() {
        return String.format("RfnExpressComUnicastReply [messageId=%s, replyType=%s]",
                             messageId,
                             replyType);
    }
    
    public boolean isSuccess() {
        return replyType == RfnExpressComUnicastReplyType.OK;
    }
    
}