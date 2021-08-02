package com.cannontech.dr.ecobee;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeePlusDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeSetpointDrParameters;
import com.cannontech.dr.ecobee.model.LMEcobeeRestoreCommand;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.service.ControlHistoryService;
import com.cannontech.dr.service.ControlType;
import com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.LMEcobeeCycleControlCommandSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.LMEcobeePlusControlCommandSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.LMEcobeeRestoreCommandSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.LMEcobeeSetpointControlCommandSerializer;

/**
 * Listens for ActiveMQ messages from Load Management, parses them, and passes DR messages to the
 * EcobeeCommunicationService, which will send them to the Ecobee servers and the end devices.
 */
public class EcobeeMessageListener {
    private static final Logger log = YukonLogManager.getLogger(EcobeeMessageListener.class);

    @Autowired private EcobeeZeusCommunicationService ecobeeZeusCommunicationService;
    @Autowired private ControlHistoryService controlHistoryService;
    @Autowired private LMEcobeeCycleControlCommandSerializer ecobeeCycleControlCommandSerializer;
    @Autowired private LMEcobeeSetpointControlCommandSerializer ecobeeSetpointControlCommandSerializer;
    @Autowired private LMEcobeeRestoreCommandSerializer ecobeeRestoreCommandSerializer;
    @Autowired private LMEcobeePlusControlCommandSerializer ecobeePlusControlCommandSerializer;

    /**
     * Processes ecobee duty cycle DR messages.
     */
    public void handleCyclingControlMessage(byte[] message) {
        log.debug("Received message on yukon.notif.stream.dr.EcobeeCyclingControlMessage queue.");

        EcobeeDutyCycleDrParameters parameters = ecobeeCycleControlCommandSerializer.fromBytes(message);

        ecobeeZeusCommunicationService.sendDutyCycleDR(parameters);

        // Send control history message to dispatch
        Duration controlDuration = new Duration(parameters.getStartTime(), parameters.getEndTime());
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
        Instant startTime = new Instant(DateTimeZone.getDefault().convertLocalToUTC(parameters.getStartTime().getMillis(), false));

        int controlCyclePercent = 100 - parameters.getDutyCyclePercent();
        controlHistoryService.sendControlHistoryShedMessage(parameters.getGroupId(),
                                                            startTime,
                                                            ControlType.ECOBEE,
                                                            null,
                                                            controlDurationSeconds,
                                                            controlCyclePercent);
    }

    public void handleSetpointControlMessage(byte[] message) {
        log.debug("Received message on yukon.notif.stream.dr.EcobeeSetpointControlMessage queue.");

        EcobeeSetpointDrParameters parameters = ecobeeSetpointControlCommandSerializer.fromBytes(message);

        ecobeeZeusCommunicationService.sendSetpointDR(parameters);

        // Send control history message to dispatch
        Duration controlDuration = new Duration(parameters.getStartTime(), parameters.getStopTime());
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
        Instant startTime = new Instant(DateTimeZone.getDefault().convertLocalToUTC(parameters.getStartTime().getMillis(),
                                                                                    false));

        int controlCyclePercent = 100;
        controlHistoryService.sendControlHistoryShedMessage(parameters.getGroupId(),
                                                            startTime,
                                                            ControlType.ECOBEE,
                                                            null,
                                                            controlDurationSeconds,
                                                            controlCyclePercent);
    }

    public void handleRestoreMessage(byte[] message) throws CommandCompletionException {
        log.debug("Received message on yukon.notif.stream.dr.EcobeeRestoreMessage queue.");

        LMEcobeeRestoreCommand ecobeeRestoreCommand = ecobeeRestoreCommandSerializer.fromBytes(message);

        ecobeeZeusCommunicationService.cancelDemandResponse(List.of(ecobeeRestoreCommand.getGroupId()));

        // Send control history message to dispatch
        controlHistoryService.sendControlHistoryRestoreMessage(ecobeeRestoreCommand.getGroupId(), Instant.now());
    }

    /**
     * Processes eco+ DR messages.
     */
    public void handleEcoPlusControlMessage(byte[] message) {
        log.debug("Received message on yukon.notif.stream.dr.EcobeePlusControlMessage queue.");

        EcobeePlusDrParameters parameters = ecobeePlusControlCommandSerializer.fromBytes(message);

        ecobeeZeusCommunicationService.sendEcoPlusDR(parameters);

        // Send control history message to dispatch
        Duration controlDuration = new Duration(parameters.getStartTime(), parameters.getEndTime());
        int controlDurationSeconds = controlDuration.toStandardSeconds().getSeconds();
        Instant startTime = new Instant(DateTimeZone.getDefault().convertLocalToUTC(parameters.getStartTime().getMillis(), false));

        controlHistoryService.sendControlHistoryShedMessage(parameters.getGroupId(),
                                                            startTime,
                                                            ControlType.ECOBEE,
                                                            null,
                                                            controlDurationSeconds,
                                                            100);
    }
}


