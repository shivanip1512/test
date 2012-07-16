package com.cannontech.dr.rfn.message.broadcast;

import java.io.Serializable;
import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

import com.cannontech.common.rfn.message.RfnMessageClass;

/**
 * JMS Queue name: yukon.qr.obj.dr.rfn.ExpressComBroadcastRequest
 */
public class RfnExpressComBroadcastRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private short messageId;
    private int messagePriority;
    private RfnMessageClass rfnMessageClass;
    private long expirationDuration;
    private byte[] payload;

    public short getMessageId() {
        return messageId;
    }

    public void setMessageId(short messageId) {
        this.messageId = messageId;
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
    
    // Created a RfnExpressComBroadcastRequest object based on a stream. This is required
    // for the c++ code which currently cannot create ObjectMessages.
    public static RfnExpressComBroadcastRequest create(StreamMessage message) {
        try {
            short messageId = message.readShort();
            int messagePriority = message.readInt();
            RfnMessageClass messageClass = RfnMessageClass.valueOf(message.readString());
            long time = message.readLong();
            int payloadSize = message.readInt();

            byte[] bytes = new byte[payloadSize];
            message.readBytes(bytes);

            RfnExpressComBroadcastRequest result = new RfnExpressComBroadcastRequest();
            result.setMessageId(messageId);
            result.setMessagePriority(messagePriority);
            result.setRfnMessageClass(messageClass);
            result.setExpirationDuration(time);
            result.setPayload(bytes);

            return result;
        } catch (JMSException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String
            .format("RfnExpressComBroadcastRequest [messageId=%s, messagePriority=%s, rfnMessageClass=%s, expirationDuration=%s, payload=%s]",
                    messageId,
                    messagePriority,
                    rfnMessageClass,
                    expirationDuration,
                    Arrays.toString(payload));
    }

    
}
