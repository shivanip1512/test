package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupExpresscom;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupExpresscomSerializer
    extends
    ThriftInheritanceSerializer<LMGroupExpresscom, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom, LMGroupBase> {

    public GroupExpresscomSerializer(String messageType,  DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupExpresscom> getTargetMessageClass() {
        return LMGroupExpresscom.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupExpresscom createMessageInstance() {
        return new LMGroupExpresscom();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom entity,
                                        LMGroupExpresscom msg) {}

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupExpresscom msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupExpresscom entity) {}
}
