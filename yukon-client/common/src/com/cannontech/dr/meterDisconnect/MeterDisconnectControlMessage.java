package com.cannontech.dr.meterDisconnect;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;

public class MeterDisconnectControlMessage {
    private static final Logger log =
        YukonLogManager.getLogger(MeterDisconnectControlMessage.class);

    private int groupId;
    private Instant startTime;
    private Instant endTime;

    public MeterDisconnectControlMessage(StreamMessage message) {
        log.debug("Received message on yukon.notif.stream.dr.MeterDisconnectControlMessage queue.");
        try {
            this.groupId = message.readInt();
            this.startTime = new Instant(message.readLong() * 1000);
            this.endTime = new Instant(message.readLong() * 1000);
        } catch (JMSException e) {
            log.error("Error parsing Meter Disconnect control message from LM", e);
        }
        log.debug("Parsed message - Group Id: " + groupId + ", startTime: " + startTime
                  + ", endTime: " + endTime);
    }

    public int getGroupId() {
        return this.groupId;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }
}
