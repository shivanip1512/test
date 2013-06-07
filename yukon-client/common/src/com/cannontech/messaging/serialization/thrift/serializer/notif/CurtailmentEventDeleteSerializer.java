package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.CurtailmentEventDeleteMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifCurtailmentEventDelete;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class CurtailmentEventDeleteSerializer extends
    ThriftInheritanceSerializer<CurtailmentEventDeleteMessage, BaseMessage, NotifCurtailmentEventDelete, Message> {

    public CurtailmentEventDeleteSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CurtailmentEventDeleteMessage> getTargetMessageClass() {
        return CurtailmentEventDeleteMessage.class;
    }

    @Override
    protected NotifCurtailmentEventDelete createThrifEntityInstance(Message entityParent) {
        NotifCurtailmentEventDelete entity = new NotifCurtailmentEventDelete();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifCurtailmentEventDelete entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CurtailmentEventDeleteMessage createMessageInstance() {
        return new CurtailmentEventDeleteMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifCurtailmentEventDelete entity,
                                                   CurtailmentEventDeleteMessage msg) {
        msg.setCurtailmentEventId(entity.get_curtailmentEventId());
        msg.setDeleteStart(entity.is_deleteStart());
        msg.setDeleteStop(entity.is_deleteStop());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CurtailmentEventDeleteMessage msg,
                                                   NotifCurtailmentEventDelete entity) {
        entity.set_curtailmentEventId(msg.getCurtailmentEventId());
        entity.set_deleteStart(msg.isDeleteStart());
        entity.set_deleteStop(msg.isDeleteStop());
    }

}
