package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.message.notif.CurtailmentEventMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifCurtailmentEvent;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class CurtailmentEventSerializer
    extends
    ThriftInheritanceSerializer<CurtailmentEventMsg, Message, NotifCurtailmentEvent, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public CurtailmentEventSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CurtailmentEventMsg> getTargetMessageClass() {
        return CurtailmentEventMsg.class;
    }

    @Override
    protected NotifCurtailmentEvent
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifCurtailmentEvent entity = new NotifCurtailmentEvent();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifCurtailmentEvent entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CurtailmentEventMsg createMessageInstance() {
        return new CurtailmentEventMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifCurtailmentEvent entity,
                                                   CurtailmentEventMsg msg) {
        msg.action = CurtailmentEventAction.values()[entity.get_action()];
        msg.curtailmentEventId = entity.get_curtailmentEventId();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CurtailmentEventMsg msg,
                                                   NotifCurtailmentEvent entity) {
        entity.set_action(msg.action.ordinal());
        entity.set_curtailmentEventId(msg.curtailmentEventId);
    }
}
