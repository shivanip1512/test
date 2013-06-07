package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.EconomicEventMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifEconomicEvent;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class EconomicEventSerializer extends
    ThriftInheritanceSerializer<EconomicEventMessage, BaseMessage, NotifEconomicEvent, Message> {

    public EconomicEventSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EconomicEventMessage> getTargetMessageClass() {
        return EconomicEventMessage.class;
    }

    @Override
    protected NotifEconomicEvent createThrifEntityInstance(Message entityParent) {
        NotifEconomicEvent entity = new NotifEconomicEvent();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifEconomicEvent entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EconomicEventMessage createMessageInstance() {
        return new EconomicEventMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifEconomicEvent entity, EconomicEventMessage msg) {
        msg.setAction(EconomicEventAction.values()[entity.get_action()]);
        msg.setEconomicEventId(entity.get_economicEventId());
        msg.setRevisionNumber(entity.get_revisionNumber());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EconomicEventMessage msg, NotifEconomicEvent entity) {
        entity.set_action(msg.getAction().ordinal());
        entity.set_economicEventId(msg.getEconomicEventId());
        entity.set_revisionNumber(msg.getRevisionNumber());
    }

}
