package com.cannontech.dr.rfn.message.unicast;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifyingMessage;
import com.cannontech.common.rfn.message.RfnMessageClass;

/**
 * JMS Queue names:
 * <ul>
 * <li>yukon.qr.obj.dr.rfn.ExpressComUnicastRequest</li>
 * <li>yukon.qr.obj.dr.rfn.ExpressComBulkUnicastRequest</li>
 * </ul>
 */
public class RfnExpressComUnicastRequest implements RfnIdentifyingMessage, Serializable {
    
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String groupId;
    private RfnIdentifier rfnIdentifier;
    private int messagePriority;
    private RfnMessageClass rfnMessageClass;
    private long expirationDuration = -1; // milliseconds to live
    private byte[] payload;
    private boolean responseExpected;

    public RfnExpressComUnicastRequest(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    @Override
    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public void setIdentifier(RfnIdentifier rfnIdentifier) {
        this.rfnIdentifier = rfnIdentifier;
    }

    public int getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(int messagePriority) {
        this.messagePriority = messagePriority;
    }

    public RfnMessageClass getRfnMessageClass() {
        return rfnMessageClass;
    }

    public void setRfnMessageClass(RfnMessageClass rfnMessageClass) {
        this.rfnMessageClass = rfnMessageClass;
    }

    public long getExpirationDuration() {
        return expirationDuration;
    }
    
    public void setExpirationDuration(long expirationDuration) {
        this.expirationDuration = expirationDuration;
    }
    
    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public boolean isResponseExpected() {
        return responseExpected;
    }

    public void setResponseExpected(boolean responseExpected) {
        this.responseExpected = responseExpected;
    }

    @Override
    public String toString() {
        return String
            .format("RfnExpressComUnicastRequest [messageId=%s, groupId=%s, rfnIdentifier=%s, messagePriority=%s, rfnMessageClass=%s, expirationDuration=%s, payload=[0x%s <%s>], responseExpected=%s]",
                    messageId,
                    groupId,
                    rfnIdentifier,
                    messagePriority,
                    rfnMessageClass,
                    expirationDuration,
                    DatatypeConverter.printHexBinary(payload),
                    new String(payload, StandardCharsets.US_ASCII),
                    responseExpected);
    }
}