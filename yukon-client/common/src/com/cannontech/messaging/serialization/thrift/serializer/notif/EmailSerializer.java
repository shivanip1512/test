package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.EmailMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.NotifEmail;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class EmailSerializer extends ThriftInheritanceSerializer<EmailMessage, BaseMessage, NotifEmail, Message> {

    public EmailSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EmailMessage> getTargetMessageClass() {
        return EmailMessage.class;
    }

    @Override
    protected Message getThriftEntityParent(NotifEmail entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifEmail createThrifEntityInstance(Message entityParent) {
        NotifEmail entity = new NotifEmail();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected EmailMessage createMessageInstance() {
        return new EmailMessage();
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, EmailMessage msg, NotifEmail entity) {
        entity.set_body(msg.getBody());
        entity.set_notifGroupId(msg.getNotifGroupId());
        entity.set_subject(msg.getSubject());
        entity.set_to(msg.getTo());
        entity.set_toBcc(msg.getTo_BCC());
        entity.set_toCc(msg.getTo_CC());
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifEmail entity, EmailMessage msg) {
        msg.setBody(entity.get_body());
        msg.setNotifGroupId(entity.get_notifGroupId());
        msg.setSubject(entity.get_subject());
        msg.setTo(entity.get_to());
        msg.setTo_BCC(entity.get_toBcc());
        msg.setTo_CC(entity.get_toCc());
    }
}
