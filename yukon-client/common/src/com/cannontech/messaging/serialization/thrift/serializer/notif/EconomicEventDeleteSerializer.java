package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.EconomicEventDeleteMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifEconomicEventDelete;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class EconomicEventDeleteSerializer
    extends
    ThriftInheritanceSerializer<EconomicEventDeleteMsg, Message, NotifEconomicEventDelete, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public EconomicEventDeleteSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EconomicEventDeleteMsg> getTargetMessageClass() {
        return EconomicEventDeleteMsg.class;
    }

    @Override
    protected NotifEconomicEventDelete
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifEconomicEventDelete entity = new NotifEconomicEventDelete();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifEconomicEventDelete entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EconomicEventDeleteMsg createMessageInstance() {
        return new EconomicEventDeleteMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifEconomicEventDelete entity,
                                                   EconomicEventDeleteMsg msg) {
        msg.deleteStart = entity.is_deleteStart();
        msg.deleteStop = entity.is_deleteStop();
        msg.economicEventId = entity.get_economicEventId();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EconomicEventDeleteMsg msg,
                                                   NotifEconomicEventDelete entity) {
        entity.set_deleteStart(msg.deleteStart);
        entity.set_deleteStop(msg.deleteStop);
        entity.set_economicEventId(msg.economicEventId);
    }
}
