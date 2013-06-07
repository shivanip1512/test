package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.enums.CurtailmentEventAction;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.CurtailmentEventMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifCurtailmentEvent;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class CurtailmentEventSerializer extends
    ThriftInheritanceSerializer<CurtailmentEventMessage, BaseMessage, NotifCurtailmentEvent, Message> {

    public CurtailmentEventSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CurtailmentEventMessage> getTargetMessageClass() {
        return CurtailmentEventMessage.class;
    }

    @Override
    protected NotifCurtailmentEvent createThrifEntityInstance(Message entityParent) {
        NotifCurtailmentEvent entity = new NotifCurtailmentEvent();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifCurtailmentEvent entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CurtailmentEventMessage createMessageInstance() {
        return new CurtailmentEventMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifCurtailmentEvent entity, CurtailmentEventMessage msg) {
        msg.setAction(CurtailmentEventAction.values()[entity.get_action()]);
        msg.setCurtailmentEventId(entity.get_curtailmentEventId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CurtailmentEventMessage msg, NotifCurtailmentEvent entity) {
        entity.set_action(msg.getAction().ordinal());
        entity.set_curtailmentEventId(msg.getCurtailmentEventId());
    }
}
