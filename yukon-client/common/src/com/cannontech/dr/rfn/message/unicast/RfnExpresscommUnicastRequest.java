package com.cannontech.dr.rfn.message.unicast;

import java.io.Serializable;
import java.util.Arrays;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnMessageClass;
import com.cannontech.common.rfn.model.YukonRfn;

/**
 * JMS Queue names:
 * <ul>
 * <li>yukon.qr.obj.dr.rfn.ExpresscommUnicastRequest</li>
 * <li>yukon.qr.obj.dr.rfn.ExpresscommBulkUnicastRequest</li>
 * </ul>
 */
public class RfnExpresscommUnicastRequest implements YukonRfn, Serializable {
    
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String groupId;
    private RfnIdentifier rfnIdentifier;
    private int messagePriority;
    private RfnMessageClass rfnMessageClass;
    private long expirationTime;
    private byte[] payload;
    private boolean responseExpected;


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

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
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
            .format("RfnExpresscommUnicastRequest [messageId=%s, groupId=%s, rfnIdentifier=%s, messagePriority=%s, rfnMessageClass=%s, expirationTime=%s, payload=%s, responseExpected=%s]",
                    messageId,
                    groupId,
                    rfnIdentifier,
                    messagePriority,
                    rfnMessageClass,
                    expirationTime,
                    Arrays.toString(payload),
                    responseExpected);
    }

    
}