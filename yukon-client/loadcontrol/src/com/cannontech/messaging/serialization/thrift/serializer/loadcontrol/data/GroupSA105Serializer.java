package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupSA105;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupSA105Serializer
    extends
    ThriftInheritanceSerializer<LMGroupSA105, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105, LMGroupBase> {

    public GroupSA105Serializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupSA105> getTargetMessageClass() {

        return LMGroupSA105.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105 entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105 entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupSA105 createMessageInstance() {
        return new LMGroupSA105();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105 entity,
                                        LMGroupSA105 msg) {}

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupSA105 msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105 entity) {}
}
