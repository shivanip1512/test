package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupSA305;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupSA305;

public class GroupSA305Serializer extends
    ThriftInheritanceSerializer<GroupSA305, DirectGroupBase, LMGroupSA305, LMGroupBase> {

    public GroupSA305Serializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupSA305> getTargetMessageClass() {

        return GroupSA305.class;
    }

    @Override
    protected LMGroupSA305 createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupSA305 entity = new LMGroupSA305();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupSA305 entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupSA305 createMessageInstance() {
        return new GroupSA305();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupSA305 entity, GroupSA305 msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupSA305 msg, LMGroupSA305 entity) {}
}
