
package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.model.EcobeePlusDrParameters;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEcobeeTemperatureTypes;

public class LMEcobeePlusControlCommandSerializer extends SimpleThriftSerializer
        implements ThriftByteDeserializer<EcobeePlusDrParameters> {
    private static final Logger log = YukonLogManager.getLogger(LMEcobeePlusControlCommandSerializer.class);

    public EcobeePlusDrParameters fromBytes(byte[] msgBytes) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEcobeePlusControlCommand();

        deserialize(msgBytes, entity);

        int programId = entity.get_programId();
        int groupId = entity.get_groupId();
        Instant startTime = new Instant(entity.get_controlStartDateTime() * 1000);
        Instant endTime = new Instant(entity.get_controlEndDateTime() * 1000);
        boolean heatingEvent = entity.get_temperatureOption() == LMEcobeeTemperatureTypes.HEAT;
        int randomTimeSeconds = entity.get_randomTimeSeconds();

        log.trace(
                "Parsed eco+ dr parameters. GroupId: {} ProgramId: {} Start time: {} End time: {} Heat: {} Random Time Seconds: {}",
                groupId,
                programId,
                startTime,
                endTime,
                heatingEvent,
                randomTimeSeconds);

        return new EcobeePlusDrParameters(programId, groupId, startTime, endTime, randomTimeSeconds, heatingEvent);
    }

}
