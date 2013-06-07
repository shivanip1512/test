package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupDigiSep;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupDigiSEP;

public class GroupDigiSepSerializer extends
    ThriftInheritanceSerializer<GroupDigiSep, DirectGroupBase, LMGroupDigiSEP, LMGroupBase> {

    public GroupDigiSepSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<GroupDigiSep> getTargetMessageClass() {
        return GroupDigiSep.class;
    }

    @Override
    protected LMGroupDigiSEP createThrifEntityInstance(LMGroupBase entityParent) {
        LMGroupDigiSEP entity = new LMGroupDigiSEP();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupDigiSEP entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected GroupDigiSep createMessageInstance() {
        return new GroupDigiSep();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupDigiSEP entity, GroupDigiSep msg) {
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, GroupDigiSep msg, LMGroupDigiSEP entity) {
    }
}
