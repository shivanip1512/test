package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupSA205;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;


public class GroupSA205Serializer extends
    ThriftInheritanceSerializer<LMGroupSA205, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205, LMGroupBase> {

    public GroupSA205Serializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupSA205> getTargetMessageClass() {

        return LMGroupSA205.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205 createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205 entity = new com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205 entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupSA205 createMessageInstance() {
        return new LMGroupSA205();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205 entity, LMGroupSA205 msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupSA205 msg, com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205 entity) {}
}
