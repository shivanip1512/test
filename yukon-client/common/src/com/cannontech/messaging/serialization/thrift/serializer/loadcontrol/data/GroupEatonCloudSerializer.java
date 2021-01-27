package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupEatonCloud;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LmGroupEatonCloud;

public class GroupEatonCloudSerializer extends 
    ThriftInheritanceSerializer<LmGroupEatonCloud, LMDirectGroupBase, LMGroupEatonCloud, LMGroupBase> {

    public GroupEatonCloudSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LmGroupEatonCloud> getTargetMessageClass() {
        return LmGroupEatonCloud.class;
    }

    @Override
    protected LMGroupEatonCloud createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupEatonCloud entity = new LMGroupEatonCloud();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupEatonCloud entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LmGroupEatonCloud createMessageInstance() {
        return new LmGroupEatonCloud();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMGroupEatonCloud entity,
                                                   LmGroupEatonCloud msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, LmGroupEatonCloud msg,
            LMGroupEatonCloud entity) {}
}
