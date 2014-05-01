package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LmGroupEcobee;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupEcobee;

public class GroupEcobeeSerializer extends
    ThriftInheritanceSerializer<LmGroupEcobee, LMDirectGroupBase, LMGroupEcobee, LMGroupBase> {
    
    public GroupEcobeeSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }
    
    @Override
    public Class<LmGroupEcobee> getTargetMessageClass() {
        return LmGroupEcobee.class;
    }
    
    @Override
    protected LMGroupEcobee createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupEcobee entity = new LMGroupEcobee();
        entity.set_baseMessage(entityParent);
        return entity;
    }
    
    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupEcobee entity) {
        return entity.get_baseMessage();
    }
    
    @Override
    protected LmGroupEcobee createMessageInstance() {
        return new LmGroupEcobee();
    }
    
    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupEcobee entity,
                                                   LmGroupEcobee msg) {}
    
    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LmGroupEcobee msg,
                                                   LMGroupEcobee entity) {}
}
