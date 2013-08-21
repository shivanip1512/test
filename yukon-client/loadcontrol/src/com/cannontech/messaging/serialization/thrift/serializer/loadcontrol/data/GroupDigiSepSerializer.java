package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LMGroupDigiSep;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupDigiSEP;

public class GroupDigiSepSerializer extends
    ThriftInheritanceSerializer<LMGroupDigiSep, LMDirectGroupBase, LMGroupDigiSEP, LMGroupBase> {

    public GroupDigiSepSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMGroupDigiSep> getTargetMessageClass() {
        return LMGroupDigiSep.class;
    }

    @Override
    protected LMGroupDigiSEP createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupDigiSEP entity = new LMGroupDigiSEP();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupDigiSEP entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMGroupDigiSep createMessageInstance() {
        return new LMGroupDigiSep();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupDigiSEP entity,
                                                   LMGroupDigiSep msg) {}

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupDigiSep msg,
                                                   LMGroupDigiSEP entity) {}
}
