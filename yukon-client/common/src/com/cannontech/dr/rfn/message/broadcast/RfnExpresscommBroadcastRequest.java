package com.cannontech.dr.rfn.message.broadcast;

import java.io.Serializable;
import java.util.Arrays;

import com.cannontech.common.rfn.message.RfnMessageClass;

/**
 * JMS Queue name: yukon.qr.obj.dr.rfn.ExpresscommBroadcastRequest
 */
public class RfnExpresscommBroadcastRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int messagePriority;
    private RfnMessageClass rfnMessageClass;
    private long expirationTime;
    private byte[] payload;

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

    @Override
    public String toString() {
        return String
            .format("RfnExpresscommBroadcastRequest [messagePriority=%s, rfnMessageClass=%s, expirationTime=%s, payload=%s]",
                    messagePriority,
                    rfnMessageClass,
                    expirationTime,
                    Arrays.toString(payload));
    }

    
}