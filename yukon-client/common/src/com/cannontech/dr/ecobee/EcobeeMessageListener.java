package com.cannontech.dr.ecobee;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;

/**
 * Listens for ActiveMQ messages from Load Management, parses them, and passes DR messages to the
 * EcobeeCommunicationService, which will send them to the Ecobee servers and the end devices.
 */
public class EcobeeMessageListener {
    private static final Logger log = YukonLogManager.getLogger(EcobeeMessageListener.class);

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;

    private static final Map<Integer, String> groupToDrIdentifierMap = new HashMap<>();

    /**
     * Processes ecobee duty cycle DR messages.
     */
    public void handleCyclingControlMessage(Message message) {
        log.debug("Received message on yukon.notif.stream.dr.EcobeeCyclingControlMessage queue.");

        EcobeeDutyCycleDrParameters parameters;
        if(message instanceof StreamMessage) {
            try {
                parameters = buildDutyCycleDrParameters((StreamMessage) message);
            } catch (JMSException e) {
                log.error("Exception parsing StreamMessage for duty cycle DR event.", e);
                return;
            }

            String drIdentifier = ecobeeCommunicationService.sendDutyCycleDR(parameters);
            //store the most recent dr handle for each group, so we can cancel
            groupToDrIdentifierMap.put(parameters.getGroupId(), drIdentifier);
        }
    }

    public void handleRestoreMessage(Message message) {
        log.debug("Received message on yukon.notif.stream.dr.EcobeeRestoreMessage queue.");

        int groupId;
        if(message instanceof StreamMessage) {
            try {
                groupId = getRestoreGroupId((StreamMessage) message);
            } catch (JMSException e) {
                log.error("Exception parsing StreamMessage for DR restore.", e);
                return;
            }

            String drIdentifier = groupToDrIdentifierMap.get(groupId);
            ecobeeCommunicationService.sendRestore(drIdentifier);
        }
    }

    /**
     * Takes the StreamMessage from Load Management and parses out the groupId to restore.
     * Currently, the restore time is always the time the message was sent, so it can be ignored.
     *
     * Load Management sends
     * 1.  Group ID    : signed int (32 bits)
     * 2.  Restore time : signed int (32 bits) [seconds from 1970.01.01:UTC]
     */
    private int getRestoreGroupId(StreamMessage message) throws JMSException {
        int groupId = message.readInt();
        log.trace("Parsed group id " + groupId);
        return groupId;
    }

    /**
     * Takes the StreamMessage from Load Management and parses out the values into an EcobeeDutyCycleDrParameters object.
     *
     * Load Management sends
     * 1.  Group ID        : signed int (32 bits)
     * 2.  Duty Cycle      : signed char (8 bits)
     * 3.  Ramping option  : signed char (8 bits) bitmask:[ 0x01 == ramp out, 0x02 == ramp in ]
     * 4.  Start time      : signed int (32 bits) [seconds from 1970.01.01:UTC]
     * 5.  End time        : signed int (32 bits) [seconds from 1970.01.01:UTC]
     */
    private EcobeeDutyCycleDrParameters buildDutyCycleDrParameters(StreamMessage message) throws JMSException {
        //Get the raw values
        int groupId = message.readInt();
        int runtimePercent = message.readByte();
        byte rampingOptions = message.readByte();
        long utcStartTimeSeconds = message.readInt();
        long utcEndTimeSeconds = message.readInt();

        //Massage the data into the form we want
        int shedTimePercent = 100 - runtimePercent;
        Instant startTime = new Instant(utcStartTimeSeconds * 1000);
        Instant endTime = new Instant(utcEndTimeSeconds * 1000);
        boolean rampIn = (rampingOptions & 2) == 2;
        boolean rampOut = (rampingOptions & 1) == 1;
        log.trace("Parsed duty cycle dr parameters. Start time: " + startTime + " (" + utcStartTimeSeconds 
                  + ") End time: " + endTime + " (" + utcEndTimeSeconds + ") Ramp in: " + rampIn + " Ramp out: " 
                  + rampOut);
        
        return new EcobeeDutyCycleDrParameters(startTime, endTime, shedTimePercent, rampIn, rampOut, groupId);
    }
}
