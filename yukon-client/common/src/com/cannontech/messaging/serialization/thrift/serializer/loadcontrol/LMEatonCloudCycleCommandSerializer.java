package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.model.EatonCloudCycleType;
import com.cannontech.loadcontrol.messages.LMEatonCloudCycleCommand;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class LMEatonCloudCycleCommandSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<LMEatonCloudCycleCommand> {

    @Override
    public LMEatonCloudCycleCommand fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEatonCloudCycleCommand();

        deserialize(msgBytes, entity);

        var eatonCloudCycleCommand = new LMEatonCloudCycleCommand();
        eatonCloudCycleCommand.setGroupId(entity.get_groupId());
        eatonCloudCycleCommand.setControlSeconds(entity.get_controlSeconds());
        eatonCloudCycleCommand.setIsRampIn(entity.is_isRampIn());
        eatonCloudCycleCommand.setIsRampOut(entity.is_isRampOut());
        eatonCloudCycleCommand.setCyclingOption(EatonCloudCycleType.of(entity.get_cyclingOption().getValue()));
        eatonCloudCycleCommand.setDutyCyclePercentage(entity.get_dutyCyclePercentage());
        eatonCloudCycleCommand.setDutyCyclePeriod(entity.get_dutyCyclePercentage());
        eatonCloudCycleCommand.setCriticality(entity.get_criticality());
        eatonCloudCycleCommand.setCurrentDateTime(new Instant(entity.get_currentDateTime()));

        return eatonCloudCycleCommand;
    }

}
