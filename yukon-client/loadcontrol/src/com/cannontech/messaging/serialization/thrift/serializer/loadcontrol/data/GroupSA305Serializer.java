package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupSA305;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupSA305Serializer
    extends
    ThriftInheritanceSerializer<LMGroupSA305, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305, LMGroupBase> {

    public GroupSA305Serializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupSA305> getTargetMessageClass() {

        return LMGroupSA305.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305 entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305 entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupSA305 createMessageInstance() {
        return new LMGroupSA305();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305 entity,
                                        LMGroupSA305 msg) {}

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupSA305 msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305 entity) {}
}
