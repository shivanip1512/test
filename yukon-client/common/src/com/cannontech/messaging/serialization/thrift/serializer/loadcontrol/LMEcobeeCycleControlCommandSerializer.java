package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class LMEcobeeCycleControlCommandSerializer extends SimpleThriftSerializer
        implements ThriftByteDeserializer<EcobeeDutyCycleDrParameters> {

    private static final Logger log = YukonLogManager.getLogger(LMEcobeeCycleControlCommandSerializer.class);

    public EcobeeDutyCycleDrParameters fromBytes(byte[] msgBytes) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEcobeeCycleControlCommand();

        deserialize(msgBytes, entity);

        int programId = entity.get_programId();
        int groupId = entity.get_groupId();
        Instant startTime = new Instant(entity.get_controlStartDateTime() * 1000);
        Instant endTime = new Instant(entity.get_controlEndDateTime() * 1000);
        int dutyCyclePercent = entity.get_dutyCycle();
        boolean rampInOut = entity.is_isRampInOut();
        int randomTimeSeconds = (rampInOut ? 1800 : 0);
        boolean isMandatory = entity.is_isMandatory();

        log.trace(
                "Parsed duty cycle dr parameters. GroupId: {} ProgramId: {} Start time: {} End time: {} Random Time Seconds: {} Mandatory: {}",
                groupId,
                programId,
                startTime,
                endTime,
                randomTimeSeconds,
                isMandatory);

        return new EcobeeDutyCycleDrParameters(programId, startTime, endTime, dutyCyclePercent, randomTimeSeconds, isMandatory,
                groupId);
    }
}
