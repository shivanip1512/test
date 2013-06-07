package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupPoint;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint;

public class GroupPointSerializer extends
    ThriftInheritanceSerializer<GroupPoint, DirectGroupBase, LMGroupPoint, LMGroupBase> {

    public GroupPointSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupPoint> getTargetMessageClass() {
        return GroupPoint.class;
    }

    @Override
    protected LMGroupPoint createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupPoint entity = new LMGroupPoint();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupPoint entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupPoint createMessageInstance() {
        return new GroupPoint();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMGroupPoint entity, GroupPoint msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, GroupPoint msg, LMGroupPoint entity) {}
}
