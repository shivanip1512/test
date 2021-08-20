package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.model.EatonCloudCycleType;
import com.cannontech.loadcontrol.messages.LMEatonCloudScheduledCycleCommand;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class LMEatonCloudScheduledCycleCommandSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<LMEatonCloudScheduledCycleCommand> {

    @Override
    public LMEatonCloudScheduledCycleCommand fromBytes(byte[] msgBytes) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEatonCloudScheduledCycleCommand();

        deserialize(msgBytes, entity);

        var eatonCloudScheduledCycleCommand = new LMEatonCloudScheduledCycleCommand();
        eatonCloudScheduledCycleCommand.setGroupId(entity.get_groupId());
        eatonCloudScheduledCycleCommand.setControlStartDateTime(new Instant(entity.get_controlStartDateTime() * 1000));
        eatonCloudScheduledCycleCommand.setControlEndDateTime(new Instant(entity.get_controlEndDateTime() * 1000));
        eatonCloudScheduledCycleCommand.setIsRampIn(entity.is_isRampIn());
        eatonCloudScheduledCycleCommand.setIsRampOut(entity.is_isRampOut());
        eatonCloudScheduledCycleCommand.setCyclingOption(EatonCloudCycleType.of(entity.get_cyclingOption().getValue()));
        eatonCloudScheduledCycleCommand.setDutyCyclePercentage(entity.get_dutyCyclePercentage());
        eatonCloudScheduledCycleCommand.setDutyCyclePeriod(entity.get_dutyCyclePeriod());
        eatonCloudScheduledCycleCommand.setCriticality(entity.get_criticality());
        eatonCloudScheduledCycleCommand.setVirtualRelayId(entity.get_vRelayId());

        return eatonCloudScheduledCycleCommand;
    }

}
