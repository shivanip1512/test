package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.NotifEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifEmail;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class EmailSerializer
    extends
    ThriftInheritanceSerializer<NotifEmailMsg, Message, NotifEmail, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public EmailSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<NotifEmailMsg> getTargetMessageClass() {
        return NotifEmailMsg.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(NotifEmail entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifEmail
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifEmail entity = new NotifEmail();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected NotifEmailMsg createMessageInstance() {
        return new NotifEmailMsg();
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, NotifEmailMsg msg, NotifEmail entity) {
        entity.set_body(msg.getBody());
        entity.set_notifGroupId(msg.getNotifGroupID());
        entity.set_subject(msg.getSubject());
        entity.set_to(msg.getTo());
        entity.set_toBcc(msg.getTo_BCC());
        entity.set_toCc(msg.getTo_CC());
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifEmail entity, NotifEmailMsg msg) {
        msg.setBody(entity.get_body());
        msg.setNotifGroupID(entity.get_notifGroupId());
        msg.setSubject(entity.get_subject());
        msg.setTo(entity.get_to());
        msg.setTo_BCC(entity.get_toBcc());
        msg.setTo_CC(entity.get_toCc());
    }
}
