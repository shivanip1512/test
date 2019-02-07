package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LmGroupItron;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupItron;

public class GroupItronSerializer extends
    ThriftInheritanceSerializer<LmGroupItron, LMDirectGroupBase, LMGroupItron, LMGroupBase> {
    
    public GroupItronSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }
    
    @Override
    public Class<LmGroupItron> getTargetMessageClass() {
        return LmGroupItron.class;
    }
    
    @Override
    protected LMGroupItron createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupItron entity = new LMGroupItron();
        entity.set_baseMessage(entityParent);
        return entity;
    }
    
    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupItron entity) {
        return entity.get_baseMessage();
    }
    
    @Override
    protected LmGroupItron createMessageInstance() {
        return new LmGroupItron();
    }
    
    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupItron entity,
                                                   LmGroupItron msg) {}
    
    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LmGroupItron msg,
                                                   LMGroupItron entity) {}
}
