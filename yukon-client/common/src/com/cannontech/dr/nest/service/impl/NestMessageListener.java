package com.cannontech.dr.nest.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;

/**
 * Listens for ActiveMQ messages from Load Management, parses them, and sends message to dispatch to put an entry in control history table.
 */
public class NestMessageListener {
    private static final Logger log = YukonLogManager.getLogger(NestMessageListener.class);

    @Autowired private ControlHistoryService controlHistoryService;

    public void handleCyclingControlMessage(Message message) {
        if(message instanceof StreamMessage) {
            try {
                log.debug("Received message on yukon.notif.stream.dr.NestCyclingControlMessage queue.");
                StreamMessage msg = (StreamMessage) message;
                int groupId = msg.readInt();
                long utcStartTimeSeconds = msg.readInt();
                long utcEndTimeSeconds = msg.readInt();
                Instant startTime = new Instant(utcStartTimeSeconds * 1000);
                Instant endTime = new Instant(utcEndTimeSeconds * 1000);       
                Duration controlDuration = new Duration(startTime, endTime);
                int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
                Instant startTimeUtc = new Instant(DateTimeZone.getDefault().convertLocalToUTC(startTime.getMillis(), false));
                
                controlHistoryService.sendControlHistoryShedMessage(groupId, startTimeUtc, ControlType.NEST, null,
                    controlDurationSeconds, 0);
            } catch (JMSException e) {
                log.error("Error parsing Nest control message from LM", e);
                return;
            }
        }
    }

    public void handleRestoreMessage(Message message) {
        if(message instanceof StreamMessage) {
            try {
                log.debug("Received message on yukon.notif.stream.dr.NestRestoreMessage queue.");
                StreamMessage msg = (StreamMessage) message;
                int groupId = msg.readInt();
                //Send control history message to dispatch
                controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
            } catch (JMSException e) {
                log.error("Error parsing Nest restore message from LM", e);
                return;
            }
        }
    }
}
