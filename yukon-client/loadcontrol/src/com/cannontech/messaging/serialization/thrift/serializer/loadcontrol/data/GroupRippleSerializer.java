package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupRipple;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupRipple;

public class GroupRippleSerializer extends
    ThriftInheritanceSerializer<GroupRipple, DirectGroupBase, LMGroupRipple, LMGroupBase> {

    public GroupRippleSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupRipple> getTargetMessageClass() {
        return GroupRipple.class;
    }

    @Override
    protected LMGroupRipple createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupRipple entity = new LMGroupRipple();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupRipple entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupRipple createMessageInstance() {
        return new GroupRipple();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupRipple entity, GroupRipple msg) {
        msg.setShedTime(entity.get_shedTime());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupRipple msg, LMGroupRipple entity) {
        entity.set_shedTime(msg.getShedTime());
    }
}
