package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.capcontrol.model.SystemStatus;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCSystemStatus;

public class SystemStatusSerializer extends
    ThriftInheritanceSerializer<SystemStatus, CapControlMessage, CCSystemStatus, CCMessage> {

    public SystemStatusSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SystemStatus> getTargetMessageClass() {
        return SystemStatus.class;
    }

    @Override
    protected CCSystemStatus createThriftEntityInstance(CCMessage entityParent) {
        CCSystemStatus entity = new CCSystemStatus();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCSystemStatus entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SystemStatus createMessageInstance() {
        return new SystemStatus();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSystemStatus entity,
                                                   SystemStatus msg) {
        msg.setEnabled(entity.is_systemState());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SystemStatus msg,
                                                   CCSystemStatus entity) {
        entity.set_systemState(msg.isEnabled());
    }
}
