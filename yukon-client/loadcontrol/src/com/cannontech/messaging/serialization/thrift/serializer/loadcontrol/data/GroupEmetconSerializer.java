package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupEmetcon;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupEmetcon;

public class GroupEmetconSerializer extends
    ThriftInheritanceSerializer<GroupEmetcon, DirectGroupBase, LMGroupEmetcon, LMGroupBase> {

    public GroupEmetconSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupEmetcon> getTargetMessageClass() {
        return GroupEmetcon.class;
    }

    @Override
    protected LMGroupEmetcon createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupEmetcon entity = new LMGroupEmetcon();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupEmetcon entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupEmetcon createMessageInstance() {
        return new GroupEmetcon();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupEmetcon entity, GroupEmetcon msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupEmetcon msg, LMGroupEmetcon entity) {}
}
