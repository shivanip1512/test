package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupSA205;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupSA205;

public class GroupSA205Serializer extends
    ThriftInheritanceSerializer<GroupSA205, DirectGroupBase, LMGroupSA205, LMGroupBase> {

    public GroupSA205Serializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupSA205> getTargetMessageClass() {

        return GroupSA205.class;
    }

    @Override
    protected LMGroupSA205 createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupSA205 entity = new LMGroupSA205();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupSA205 entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupSA205 createMessageInstance() {
        return new GroupSA205();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupSA205 entity, GroupSA205 msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupSA205 msg, LMGroupSA205 entity) {}
}
