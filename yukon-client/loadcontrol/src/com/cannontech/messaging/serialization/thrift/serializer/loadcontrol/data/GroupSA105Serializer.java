package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupSA105;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupSA105;

public class GroupSA105Serializer extends
    ThriftInheritanceSerializer<GroupSA105, DirectGroupBase, LMGroupSA105, LMGroupBase> {

    public GroupSA105Serializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupSA105> getTargetMessageClass() {

        return GroupSA105.class;
    }

    @Override
    protected LMGroupSA105 createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupSA105 entity = new LMGroupSA105();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupSA105 entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupSA105 createMessageInstance() {
        return new GroupSA105();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupSA105 entity, GroupSA105 msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupSA105 msg, LMGroupSA105 entity) {}
}
