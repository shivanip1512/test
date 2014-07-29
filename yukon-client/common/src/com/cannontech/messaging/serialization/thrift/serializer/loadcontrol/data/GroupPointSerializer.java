package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupPoint;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupPointSerializer
    extends
    ThriftInheritanceSerializer<LMGroupPoint, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint, LMGroupBase> {

    public GroupPointSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupPoint> getTargetMessageClass() {
        return LMGroupPoint.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupPoint createMessageInstance() {
        return new LMGroupPoint();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint entity,
                                        LMGroupPoint msg) {}

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory factory, LMGroupPoint msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupPoint entity) {}
}
