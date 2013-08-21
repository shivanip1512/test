package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupSADigital;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupSADigitalSerializer
    extends
    ThriftInheritanceSerializer<LMGroupSADigital, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital, LMGroupBase> {

    public GroupSADigitalSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupSADigital> getTargetMessageClass() {
        return LMGroupSADigital.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupSADigital createMessageInstance() {
        return new LMGroupSADigital();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital entity,
                                        LMGroupSADigital msg) {}

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupSADigital msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital entity) {}
}
