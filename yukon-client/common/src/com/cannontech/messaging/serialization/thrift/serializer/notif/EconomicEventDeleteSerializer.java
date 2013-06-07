package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.EconomicEventDeleteMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifEconomicEventDelete;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class EconomicEventDeleteSerializer extends
    ThriftInheritanceSerializer<EconomicEventDeleteMessage, BaseMessage, NotifEconomicEventDelete, Message> {

    public EconomicEventDeleteSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EconomicEventDeleteMessage> getTargetMessageClass() {
        return EconomicEventDeleteMessage.class;
    }

    @Override
    protected NotifEconomicEventDelete createThrifEntityInstance(Message entityParent) {
        NotifEconomicEventDelete entity = new NotifEconomicEventDelete();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifEconomicEventDelete entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EconomicEventDeleteMessage createMessageInstance() {
        return new EconomicEventDeleteMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifEconomicEventDelete entity, EconomicEventDeleteMessage msg) {
        msg.setDeleteStart(entity.is_deleteStart());
        msg.setDeleteStop(entity.is_deleteStop());
        msg.setEconomicEventId(entity.get_economicEventId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EconomicEventDeleteMessage msg, NotifEconomicEventDelete entity) {
        entity.set_deleteStart(msg.isDeleteStart());
        entity.set_deleteStop(msg.isDeleteStop());
        entity.set_economicEventId(msg.getEconomicEventId());
    }

}
