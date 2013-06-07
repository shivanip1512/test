package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupSADigital;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupSADigital;

public class GroupSADigitalSerializer extends
    ThriftInheritanceSerializer<GroupSADigital, DirectGroupBase, LMGroupSADigital, LMGroupBase> {

    public GroupSADigitalSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupSADigital> getTargetMessageClass() {
        return GroupSADigital.class;
    }

    @Override
    protected LMGroupSADigital createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupSADigital entity = new LMGroupSADigital();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupSADigital entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupSADigital createMessageInstance() {
        return new GroupSADigital();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupSADigital entity, GroupSADigital msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupSADigital msg, LMGroupSADigital entity) {}
}
