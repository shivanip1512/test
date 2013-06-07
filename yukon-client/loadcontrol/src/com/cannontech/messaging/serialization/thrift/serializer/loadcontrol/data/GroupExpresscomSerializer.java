package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupExpresscom;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom;

public class GroupExpresscomSerializer extends
    ThriftInheritanceSerializer<GroupExpresscom, DirectGroupBase, LMGroupExpresscom, LMGroupBase> {

    public GroupExpresscomSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupExpresscom> getTargetMessageClass() {
        return GroupExpresscom.class;
    }

    @Override
    protected LMGroupExpresscom createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupExpresscom entity = new LMGroupExpresscom();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupExpresscom entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupExpresscom createMessageInstance() {
        return new GroupExpresscom();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupExpresscom entity, GroupExpresscom msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupExpresscom msg, LMGroupExpresscom entity) {}
}
