package com.cannontech.dr.honeywellWifi;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.honeywell.service.HoneywellCommunicationService;
import com.cannontech.dr.honeywellWifi.model.HoneywellWifiDutyCycleDrParameters;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;

/**
 * Listens for ActiveMQ messages from Load Management, parses them, and passes DR messages to the
 * HoneywellCommunicationService, which will send them to the Honeywell servers and the end devices.
 */
public class HoneywellWifiMessageListener {
    private static final Logger log = YukonLogManager.getLogger(HoneywellWifiMessageListener.class);

    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private HoneywellCommunicationService honeywellCommunicationService;
    @Autowired private NextValueHelper nextValueHelper;
    
    private static final Map<Integer, Integer> groupToEventIdMap = new HashMap<>();

    /**
     * Processes LMHonewellWifi duty cycle DR messages.
     */
    public void handleLMHoneywellCyclingControlMessage(Message message) {
        log.debug("Received message on yukon.notif.stream.dr.HoneywellCyclingControlMessage queue.");

        HoneywellWifiDutyCycleDrParameters parameters;
        if (message instanceof StreamMessage) {
            try {
                parameters = buildDutyCycleDrParameters((StreamMessage) message);
            } catch (JMSException e) {
                log.error("Exception parsing StreamMessage for duty cycle DR event.", e);
                return;
            }

            // Send DR message to HoneywellWifi server
            honeywellCommunicationService.sendDREventForGroup(parameters);

            // Store the most recent dr handle for each group, so we can cancel
            groupToEventIdMap.put(parameters.getGroupId(), parameters.getEventId());

            Instant startTime =
                new Instant(DateTimeZone.getDefault().convertUTCToLocal(parameters.getStartTime().getMillis()));

            controlHistoryService.sendControlHistoryShedMessage(parameters.getGroupId(),
                                                                startTime,
                                                                ControlType.HONEYWELLWIFI,
                                                                null,
                                                                parameters.getDurationSeconds(),
                                                                parameters.getDutyCyclePercent());

        }

    }

    /**
     * Processes LMHoneywellWifi Restore messages.
     */
    public void handleLMHoneywellRestoreMessage(Message message) {
        log.debug("Received message on yukon.notif.stream.dr.HoneywellRestoreMessage queue.");

        int groupId;
        if (message instanceof StreamMessage) {
            try {
                groupId = getRestoreGroupId((StreamMessage) message);
            } catch (JMSException e) {
                log.error("Exception parsing StreamMessage for DR restore.", e);
                return;
            }

            // Send restore to HoneywellWifi server
            Integer eventId = groupToEventIdMap.get(groupId);
            if (eventId != null) {
                honeywellCommunicationService.cancelDREventForGroup(groupId, eventId, Boolean.TRUE);
                // Send control history message to dispatch
                controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
            }
        }
    }

    /**
     * Takes the StreamMessage from Load Management and parses out the groupId to restore.
     * Currently, the restore time is always the time the message was sent, so it can be ignored.
     *
     * Load Management sends
     * 1. Group ID : signed int (32 bits)
     * 2. Restore time : signed int (32 bits) [seconds from 1970.01.01:UTC]
     */
    private int getRestoreGroupId(StreamMessage message) throws JMSException {
        int groupId = message.readInt();
        log.trace("Parsed group id " + groupId);
        return groupId;
    }

    /**
     * Takes the StreamMessage from Load Management and parses out the values into an
     * HoneywellWifiDutyCycleDrParameters object.
     *
     * Load Management sends
     * 1. Group ID : signed int (32 bits)
     * 2. Duty Cycle : signed char (8 bits)
     * 3. Ramping option : signed char (8 bits) bitmask:[ 0x01 == ramp out, 0x02 == ramp in ]
     * 4. Start time : signed int (32 bits) [seconds from 1970.01.01:UTC]
     * 5. End time : signed int (32 bits) [seconds from 1970.01.01:UTC]
     */
    private HoneywellWifiDutyCycleDrParameters buildDutyCycleDrParameters(StreamMessage message)
            throws JMSException {
        // Get the raw values
        int groupId = message.readInt();
        int dutyCycle = message.readByte();
        byte rampingOptions = message.readByte();
        long utcStartTimeSeconds = message.readInt();
        long utcEndTimeSeconds = message.readInt();

        // Massage the data into the form we want
        int dutyCyclePercent = 100 - dutyCycle;
        Instant startTime = new Instant(utcStartTimeSeconds * 1000);
        Instant endTime = new Instant(utcEndTimeSeconds * 1000);
        boolean rampInOut = (rampingOptions & 2) == 2;
        // If rampInOut is set then randomizationInterval is 30 else 0
        int randomizationInterval = (rampInOut ? 30 : 0);
        int eventId = nextValueHelper.getNextValue("HoneywellDREvent");
        
        Duration controlDuration = new Duration(startTime, endTime);
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
        
        log.trace("Parsed duty cycle dr parameters. Start time: " + startTime + " (" + utcStartTimeSeconds + ")"
                + " End time: " + endTime + " (" + utcEndTimeSeconds + ") Ramp in-out: " + rampInOut);

        return new HoneywellWifiDutyCycleDrParameters(eventId,
                                                      startTime,
                                                      endTime,
                                                      dutyCyclePercent,
                                                      randomizationInterval,
                                                      groupId,
                                                      controlDurationSeconds);
    }

}
