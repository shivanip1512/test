package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupEmetcon;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupEmetconSerializer extends
    ThriftInheritanceSerializer<LMGroupEmetcon, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon, LMGroupBase> {

    public GroupEmetconSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupEmetcon> getTargetMessageClass() {
        return LMGroupEmetcon.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon entity = new com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupEmetcon createMessageInstance() {
        return new LMGroupEmetcon();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon entity, LMGroupEmetcon msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupEmetcon msg, com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon entity) {}
}
