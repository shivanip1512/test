package com.cannontech.dr.meterDisconnect;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;

public class MeterDisconnectRestoreMessage {
    private static final Logger log =
        YukonLogManager.getLogger(MeterDisconnectRestoreMessage.class);

    private int groupId;
    Instant restoreTime;

    public MeterDisconnectRestoreMessage(Message message) {
        log.debug("Received message on yukon.notif.stream.dr.MeterDisconnectRestoreMessage queue.");
        StreamMessage msg = (StreamMessage) message;
        try {
            this.groupId = msg.readInt();
            this.restoreTime = new Instant(msg.readLong());
        } catch (JMSException e) {
            log.warn("Error parsing Meter Disconnect restore message from LM", e);
        }
        log.debug("Parsed message - Group Id: " + groupId + ", Restore Time: " + restoreTime);

    }
    
    public int getGroupId() {
        return this.groupId;
    }
    
    public Instant getRestoreTime() {
        return this.restoreTime;
    }
}
