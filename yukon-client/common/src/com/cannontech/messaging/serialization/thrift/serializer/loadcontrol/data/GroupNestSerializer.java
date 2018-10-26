package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LmGroupNest;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupNest;

public class GroupNestSerializer extends
    ThriftInheritanceSerializer<LmGroupNest, LMDirectGroupBase, LMGroupNest, LMGroupBase> {
    
    public GroupNestSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }
    
    @Override
    public Class<LmGroupNest> getTargetMessageClass() {
        return LmGroupNest.class;
    }
    
    @Override
    protected LMGroupNest createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupNest entity = new LMGroupNest();
        entity.set_baseMessage(entityParent);
        return entity;
    }
    
    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupNest entity) {
        return entity.get_baseMessage();
    }
    
    @Override
    protected LmGroupNest createMessageInstance() {
        return new LmGroupNest();
    }
    
    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupNest entity,
                                                   LmGroupNest msg) {}
    
    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LmGroupNest msg,
                                                   LMGroupNest entity) {}
}
