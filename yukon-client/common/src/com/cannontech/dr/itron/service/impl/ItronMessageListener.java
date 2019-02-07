package com.cannontech.dr.itron.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.itron.service.ItronCommunicationService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.yukon.IDatabaseCache;

public class ItronMessageListener {
    private static final Logger log = YukonLogManager.getLogger(ItronMessageListener.class);
    
    @Autowired private ControlHistoryService controlHistoryService;//Is control history service needed for itron?
    @Autowired private ItronCommunicationService itronCommunicationService;
    @Autowired private IDatabaseCache dbCache;
    
    public void handleCyclingControlMessage(Message message) {
        if (message instanceof StreamMessage) {
            try {
                log.debug("Received message on yukon.notif.stream.dr.ItronCyclingControlMessage queue.");
                StreamMessage msg = (StreamMessage) message;
                int groupId = msg.readInt();
                long utcStartTimeSeconds = msg.readLong();
                long utcEndTimeSeconds = msg.readLong();
                Instant startTime = new Instant(utcStartTimeSeconds * 1000);
                Instant endTime = new Instant(utcEndTimeSeconds * 1000);
                Duration controlDuration = new Duration(startTime, endTime);
                int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
                Instant startTimeUtc = new Instant(DateTimeZone.getDefault().convertLocalToUTC(startTime.getMillis(), false));
                
                int rampIn = msg.readByte();
                int rampOut = msg.readByte();
                int dutyCyclePercent = msg.readByte();
                int dutyCyclePeriod = msg.readInt();
                int criticality = msg.readByte();
                controlHistoryService.sendControlHistoryShedMessage(groupId, startTimeUtc, ControlType.ITRON, null,
                    controlDurationSeconds, 0);
            } catch (JMSException e) {
                log.error("Error parsing Itron control message from LM", e);
                return;
            }
        }
    }
    
    public void handleRestoreMessage(Message message) {
        if (message instanceof StreamMessage) {
            try {
                log.debug("Received message on yukon.notif.stream.dr.ItronRestoreMessage queue.");
                StreamMessage msg = (StreamMessage) message;
                int groupId = msg.readInt();

                LiteYukonPAObject group = dbCache.getAllLMGroups().stream()
                        .filter(g -> g.getLiteID() == groupId).findAny().orElse(null);
                if (group == null) {
                    log.error("Group with id " + groupId + " is not found");
                    return;
                }

//                if (result.isSuccess()) {
//                    controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
//                }
            } catch (JMSException e) {
                log.error("Error parsing Itron restore message from LM", e);
                return;
            }
        }
    }

}
