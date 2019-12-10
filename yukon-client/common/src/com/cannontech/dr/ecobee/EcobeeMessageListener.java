package com.cannontech.dr.ecobee;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeSetpointDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;

/**
 * Listens for ActiveMQ messages from Load Management, parses them, and passes DR messages to the
 * EcobeeCommunicationService, which will send them to the Ecobee servers and the end devices.
 */
public class EcobeeMessageListener {
    private static final Logger log = YukonLogManager.getLogger(EcobeeMessageListener.class);

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private ControlHistoryService controlHistoryService;

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
            
            //Send DR message to ecobee server
            String drIdentifier = ecobeeCommunicationService.sendDutyCycleDR(parameters);
            
            //Store the most recent dr handle for each group, so we can cancel
            groupToDrIdentifierMap.put(parameters.getGroupId(), drIdentifier);
            
            //Send control history message to dispatch
            Duration controlDuration = new Duration(parameters.getStartTime(), parameters.getEndTime());
            int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
            Instant startTime = new Instant(DateTimeZone.getDefault().convertLocalToUTC(parameters.getStartTime().getMillis(), false));
            
            int controlCyclePercent = 100 - parameters.getDutyCyclePercent(); 
            controlHistoryService.sendControlHistoryShedMessage(parameters.getGroupId(), startTime, ControlType.ECOBEE, 
                                                                null, controlDurationSeconds,
                                                                controlCyclePercent);
        }
    }

    public void handleSetpointControlMessage(Message message) {
        log.debug("Received message on yukon.notif.stream.dr.EcobeeCyclingControlMessage queue.");

        EcobeeSetpointDrParameters parameters;
        if(message instanceof StreamMessage) {
            try {
                parameters = buildSetpointDrParameters((StreamMessage) message);
            } catch (JMSException e) {
                log.error("Exception parsing StreamMessage for duty cycle DR event.", e);
                return;
            }
            System.out.println("Parameters built " + parameters + " Ready to send Ecobee Message");

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
            
            //Send restore to ecobee server
            String drIdentifier = groupToDrIdentifierMap.get(groupId);
            ecobeeCommunicationService.sendRestore(drIdentifier);
            
            //Send control history message to dispatch
            controlHistoryService.sendControlHistoryRestoreMessage(groupId, Instant.now());
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
     * 4.  Mandatory       : signed char (8 bits) 0 == optional, 1 == mandatory]
     * 5.  Start time      : signed int (32 bits) [seconds from 1970.01.01:UTC]
     * 6.  End time        : signed int (32 bits) [seconds from 1970.01.01:UTC]
     */
    private EcobeeDutyCycleDrParameters buildDutyCycleDrParameters(StreamMessage message) throws JMSException {
        //Get the raw values
        int groupId = message.readInt();
        int controlCyclePercent = message.readByte();
        byte rampingOptions = message.readByte();
        byte mandatoryByte = message.readByte();
        long utcStartTimeSeconds = message.readInt();
        long utcEndTimeSeconds = message.readInt();

        //Massage the data into the form we want
        int dutyCyclePercent = 100 - controlCyclePercent;
        Instant startTime = new Instant(utcStartTimeSeconds * 1000);
        Instant endTime = new Instant(utcEndTimeSeconds * 1000);
        boolean rampIn = (rampingOptions & 2) == 2;
        boolean rampOut = (rampingOptions & 1) == 1;
        boolean optional = (mandatoryByte == 0);
        log.trace("Parsed duty cycle dr parameters. Start time: " + startTime + " (" + utcStartTimeSeconds 
                  + ") End time: " + endTime + " (" + utcEndTimeSeconds + ") Ramp in: " + rampIn + " Ramp out: " 
                  + rampOut + "Optional: " + optional + "(" + mandatoryByte + ")");
        
        return new EcobeeDutyCycleDrParameters(startTime, endTime, dutyCyclePercent, rampIn, rampOut, optional, groupId);
    }
    
    /**
     * Takes the StreamMessage from Load Management and parses out the values into an EcobeeSetpointDrParameters object.
     *
     * Load Management sends
     * 1.  Group ID        : signed int  (32 bits)
     * 2.  Temp Option     : signed char (8 bits)  [0 == cool, 1 == heat]
     * 3.  Mandatory       : signed char (8 bits)  [0 == optional, 1 == mandatory]
     * 4.  Temp Offset     : signed int  (32 bits) [-10,10]
     * 5.  Start time      : signed long (64 bits) [seconds from 1970.01.01:UTC]
     * 6.  End time        : signed long (64 bits) [seconds from 1970.01.01:UTC]
     */
    private EcobeeSetpointDrParameters buildSetpointDrParameters(StreamMessage message) throws JMSException {
        //Get the raw values
        int groupId = message.readInt();
        byte tempOptionByte = message.readByte();
        byte mandatoryByte = message.readByte();
        int tempOffset = message.readInt();
        long utcStartTimeSeconds = message.readLong();
        long utcEndTimeSeconds = message.readLong();

        //Massage the data into the form we want
        boolean tempOptionHeat = (tempOptionByte == 1);
        Instant startTime = new Instant(utcStartTimeSeconds * 1000);
        Instant endTime = new Instant(utcEndTimeSeconds * 1000);
        boolean optional = (mandatoryByte == 0);
        log.info("Parsed setpoint dr parameters. Start time: " + startTime + " (" + utcStartTimeSeconds 
                  + ") End time: " + endTime + " (" + utcEndTimeSeconds + ") Optional: " + optional + "(" + mandatoryByte + ")");
        
        return new EcobeeSetpointDrParameters(groupId, tempOptionHeat, optional, tempOffset, startTime, endTime);
    }
}
