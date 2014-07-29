package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupRipple;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;

public class GroupRippleSerializer
    extends
    ThriftInheritanceSerializer<LMGroupRipple, LMDirectGroupBase, com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple, LMGroupBase> {

    public GroupRippleSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupRipple> getTargetMessageClass() {
        return LMGroupRipple.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple
        createThriftEntityInstance(LMGroupBase entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupRipple createMessageInstance() {
        return new LMGroupRipple();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple entity,
                                        LMGroupRipple msg) {
        msg.setShedTime(entity.get_shedTime());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupRipple msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple entity) {
        entity.set_shedTime(msg.getShedTime());
    }
}
