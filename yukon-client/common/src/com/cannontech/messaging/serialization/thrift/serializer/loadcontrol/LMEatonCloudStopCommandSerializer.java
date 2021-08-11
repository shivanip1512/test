package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.model.EatonCloudStopType;
import com.cannontech.loadcontrol.messages.LMEatonCloudStopCommand;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class LMEatonCloudStopCommandSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<LMEatonCloudStopCommand> {

    @Override
    public LMEatonCloudStopCommand fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.LMEatonCloudStopCommand();
        
        deserialize(msgBytes, entity);
        
        var eatonCloudStopCommand = new LMEatonCloudStopCommand();
        eatonCloudStopCommand.setGroupId(entity.get_groupId());
        eatonCloudStopCommand.setRestoreTime(new Instant(entity.get_restoreTime() * 1000));
        eatonCloudStopCommand.setStopType(EatonCloudStopType.of(entity.get_stopType().getValue()));
        eatonCloudStopCommand.setVirtualRelayId(entity.get_vRelayId());
        
        return eatonCloudStopCommand;
    }

}
