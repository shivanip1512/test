package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.NotifCustomerEmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifCustomerEmail;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class CustomerEmailSerializer
    extends
    ThriftInheritanceSerializer<NotifCustomerEmailMsg, Message, NotifCustomerEmail, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public CustomerEmailSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<NotifCustomerEmailMsg> getTargetMessageClass() {
        return NotifCustomerEmailMsg.class;
    }

    @Override
    protected NotifCustomerEmail
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifCustomerEmail entity = new NotifCustomerEmail();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifCustomerEmail entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifCustomerEmailMsg createMessageInstance() {
        return new NotifCustomerEmailMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifCustomerEmail entity,
                                                   NotifCustomerEmailMsg msg) {
        msg.setBody(entity.get_body());
        msg.setCustomerID(entity.get_customerId());
        msg.setSubject(entity.get_subject());

        // TODO the following fields seem to be missing from the NotifCustomerEmailMsg. Why?
        // attachments , to , toBCC, toCC
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, NotifCustomerEmailMsg msg,
                                                   NotifCustomerEmail entity) {
        entity.set_body(msg.getBody());
        entity.set_customerId(msg.getCustomerID());
        entity.set_subject(msg.getSubject());

        // TODO the following fields seem to be missing from the NotifCustomerEmailMsg. Why?
        entity.set_to("");
        entity.set_toBcc("");
        entity.set_toCc("");
    }
}
