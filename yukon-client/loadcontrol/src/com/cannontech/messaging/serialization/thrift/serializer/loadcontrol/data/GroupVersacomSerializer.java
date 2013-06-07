package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupVersacom;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom;

public class GroupVersacomSerializer extends
    ThriftInheritanceSerializer<GroupVersacom, DirectGroupBase, LMGroupVersacom, LMGroupBase> {

    public GroupVersacomSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupVersacom> getTargetMessageClass() {
        return GroupVersacom.class;
    }

    @Override
    protected LMGroupVersacom createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupVersacom entity = new LMGroupVersacom();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupVersacom entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupVersacom createMessageInstance() {
        return new GroupVersacom();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupVersacom entity, GroupVersacom msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupVersacom msg, LMGroupVersacom entity) {}
}
