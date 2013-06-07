package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.message.capcontrol.SystemStatusMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.CCSystemStatus;

public class SystemStatusSerializer extends
    ThriftInheritanceSerializer<SystemStatusMessage, CapControlMessage, CCSystemStatus, CCMessage> {

    public SystemStatusSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SystemStatusMessage> getTargetMessageClass() {
        return SystemStatusMessage.class;
    }

    @Override
    protected CCSystemStatus createThrifEntityInstance(CCMessage entityParent) {
        CCSystemStatus entity = new CCSystemStatus();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCSystemStatus entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SystemStatusMessage createMessageInstance() {
        return new SystemStatusMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCSystemStatus entity, SystemStatusMessage msg) {
        msg.setEnabled(entity.is_systemState());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SystemStatusMessage msg, CCSystemStatus entity) {
        entity.set_systemState(msg.isEnabled());
    }
}
