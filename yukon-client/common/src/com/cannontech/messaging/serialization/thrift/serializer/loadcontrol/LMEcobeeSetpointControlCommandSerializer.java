package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.model.EcobeeSetpointDrParameters;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.generated.LMEcobeeTemperatureTypes;

public class LMEcobeeSetpointControlCommandSerializer extends SimpleThriftSerializer
        implements ThriftByteDeserializer<EcobeeSetpointDrParameters> {

    private static final Logger log = YukonLogManager.getLogger(LMEcobeeSetpointControlCommandSerializer.class);

    public EcobeeSetpointDrParameters fromBytes(byte[] msgBytes) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEcobeeSetpointControlCommand();

        deserialize(msgBytes, entity);

        int groupId = entity.get_groupId();
        Instant startTime = new Instant(entity.get_controlStartDateTime() * 1000);
        Instant endTime = new Instant(entity.get_controlEndDateTime() * 1000);
        boolean optional = entity.is_isMandatory();
        boolean tempOptionHeat = entity.get_temperatureOption() == LMEcobeeTemperatureTypes.HEAT;
        int tempOffset = entity.get_temperatureOffset();

        log.trace("Parsed setpoint dr parameters. GroupId: {} Start time: {} End time: {} Optional: {} Heat: {} Offset: {}",
                  groupId, 
                  startTime, 
                  endTime, 
                  optional, 
                  tempOptionHeat, 
                  tempOffset);

        return new EcobeeSetpointDrParameters(groupId, tempOptionHeat, optional, tempOffset, startTime, endTime);

    }
}
