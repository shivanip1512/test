package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupGolay;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupGolaySerializer
    extends
    ThriftInheritanceSerializer<LMGroupGolay, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay, LMGroupBase> {

    public GroupGolaySerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupGolay> getTargetMessageClass() {
        return LMGroupGolay.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupGolay createMessageInstance() {
        return new LMGroupGolay();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay entity,
                                        LMGroupGolay msg) {}

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupGolay msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay entity) {}

}
