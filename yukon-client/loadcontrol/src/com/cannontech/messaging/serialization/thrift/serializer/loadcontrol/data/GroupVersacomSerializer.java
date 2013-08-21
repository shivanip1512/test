package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupVersacom;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupVersacomSerializer extends
    ThriftInheritanceSerializer<LMGroupVersacom, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom, LMGroupBase> {

    public GroupVersacomSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupVersacom> getTargetMessageClass() {
        return LMGroupVersacom.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom entity = new com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupVersacom createMessageInstance() {
        return new LMGroupVersacom();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom entity, LMGroupVersacom msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupVersacom msg, com.cannontech.messaging.serialization.thrift.generated.LMGroupVersacom entity) {}
}
