package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupGolay;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupGolay;

public class GroupGolaySerializer extends
    ThriftInheritanceSerializer<GroupGolay, DirectGroupBase, LMGroupGolay, LMGroupBase> {

    public GroupGolaySerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupGolay> getTargetMessageClass() {
        return GroupGolay.class;
    }

    @Override
    protected LMGroupGolay createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupGolay entity = new LMGroupGolay();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupGolay entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupGolay createMessageInstance() {
                return new GroupGolay();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupGolay entity, GroupGolay msg) {    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupGolay msg, LMGroupGolay entity) {    }

}
