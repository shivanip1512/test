package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LmGroupMeterDisconnect;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupMeterDisconnect;

public class GroupMeterDisconnectSerializer extends
    ThriftInheritanceSerializer<LmGroupMeterDisconnect, LMDirectGroupBase, LMGroupMeterDisconnect, LMGroupBase> {
    
    public GroupMeterDisconnectSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }
    
    @Override
    public Class<LmGroupMeterDisconnect> getTargetMessageClass() {
        return LmGroupMeterDisconnect.class;
    }
    
    @Override
    protected LMGroupMeterDisconnect createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupMeterDisconnect entity = new LMGroupMeterDisconnect();
        entity.set_baseMessage(entityParent);
        return entity;
    }
    
    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupMeterDisconnect entity) {
        return entity.get_baseMessage();
    }
    
    @Override
    protected LmGroupMeterDisconnect createMessageInstance() {
        return new LmGroupMeterDisconnect();
    }
    
    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupMeterDisconnect entity,
                                                   LmGroupMeterDisconnect msg) {}
    
    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LmGroupMeterDisconnect msg,
                                                   LMGroupMeterDisconnect entity) {}
}
