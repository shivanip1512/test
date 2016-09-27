package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.cannontech.loadcontrol.data.LmGroupHoneywell;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupHoneywell;

public class GroupHoneywellSerializer extends
    ThriftInheritanceSerializer<LmGroupHoneywell, LMDirectGroupBase, LMGroupHoneywell, LMGroupBase> {
    
    public GroupHoneywellSerializer(String messageType, DirectGroupBaseSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }
    
    @Override
    public Class<LmGroupHoneywell> getTargetMessageClass() {
        return LmGroupHoneywell.class;
    }
    
    @Override
    protected LMGroupHoneywell createThriftEntityInstance(LMGroupBase entityParent) {
        LMGroupHoneywell entity = new LMGroupHoneywell();
        entity.set_baseMessage(entityParent);
        return entity;
    }
    
    @Override
    protected LMGroupBase getThriftEntityParent(LMGroupHoneywell entity) {
        return entity.get_baseMessage();
    }
    
    @Override
    protected LmGroupHoneywell createMessageInstance() {
        return new LmGroupHoneywell();
    }
    
    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupHoneywell entity,
                                                   LmGroupHoneywell msg) {}
    
    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LmGroupHoneywell msg,
                                                   LMGroupHoneywell entity) {}
}
