package com.cannontech.dr.rfn.message.unicast;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.dr.rfn.message.RfnLcrDataMessage;
import com.cannontech.dr.rfn.message.archive.RfnLcrReading;

/**
 * JMS Queue names:
 * <ul>
 * <li>JMSReplyTo of yukon.qr.obj.dr.rfn.ExpressComUnicastRequest</li>
 * <li>yukon.qr.obj.dr.rfn.ExpressComBulkUnicastResponse</li>
 * </ul>
 */
public class RfnExpressComUnicastDataReply implements Serializable, RfnIdentifyingMessage, RfnLcrDataMessage {
    
    private static final long serialVersionUID = 1L;

    private String messageId;
    private RfnIdentifier rfnIdentifier;
    private RfnLcrReading data;
    private RfnExpressComUnicastDataReplyType replyType = RfnExpressComUnicastDataReplyType.TIMEOUT;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return null;
    }
    
    public void setRfnIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public RfnLcrReading getData() {
        return data;
    }
    
    public void setData(RfnLcrReading data) {
        this.data = data;
    }

    public RfnExpressComUnicastDataReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(RfnExpressComUnicastDataReplyType replyType) {
        this.replyType = replyType;
    }

    public boolean isSuccess() {
        return replyType == RfnExpressComUnicastDataReplyType.OK;
    }

    @Override
    public String toString() {
        return String
            .format("RfnExpressComUnicastDataReply [messageId=%s, rfnIdentifier=%s, data=%s, replyType=%s]",
                    messageId,
                    rfnIdentifier,
                    data,
                    replyType);
    }

}