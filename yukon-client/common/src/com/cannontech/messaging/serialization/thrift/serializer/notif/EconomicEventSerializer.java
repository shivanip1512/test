package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.enums.EconomicEventAction;
import com.cannontech.message.notif.EconomicEventMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifEconomicEvent;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class EconomicEventSerializer
    extends
    ThriftInheritanceSerializer<EconomicEventMsg, Message, NotifEconomicEvent, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public EconomicEventSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EconomicEventMsg> getTargetMessageClass() {
        return EconomicEventMsg.class;
    }

    @Override
    protected NotifEconomicEvent
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifEconomicEvent entity = new NotifEconomicEvent();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifEconomicEvent entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EconomicEventMsg createMessageInstance() {
        return new EconomicEventMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifEconomicEvent entity,
                                                   EconomicEventMsg msg) {
        msg.action = EconomicEventAction.values()[entity.get_action()];
        msg.economicEventId = entity.get_economicEventId();
        msg.revisionNumber = entity.get_revisionNumber();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EconomicEventMsg msg,
                                                   NotifEconomicEvent entity) {
        entity.set_action(msg.action.ordinal());
        entity.set_economicEventId(msg.economicEventId);
        entity.set_revisionNumber(msg.revisionNumber);
    }
}
