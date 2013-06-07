package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.CustomerEmailMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifCustomerEmail;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class CustomerEmailSerializer extends
    ThriftInheritanceSerializer<CustomerEmailMessage, BaseMessage, NotifCustomerEmail, Message> {

    public CustomerEmailSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CustomerEmailMessage> getTargetMessageClass() {
        return CustomerEmailMessage.class;
    }

    @Override
    protected NotifCustomerEmail createThrifEntityInstance(Message entityParent) {
        NotifCustomerEmail entity = new NotifCustomerEmail();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifCustomerEmail entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CustomerEmailMessage createMessageInstance() {
        return new CustomerEmailMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifCustomerEmail entity, CustomerEmailMessage msg) {
        msg.setBody(entity.get_body());
        msg.setCustomerId(entity.get_customerId());
        msg.setSubject(entity.get_subject());

        // TODO the following fields seem to be missing from the NotifCustomerEmailMsg. Why?
        // attachments , to , toBCC, toCC
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CustomerEmailMessage msg, NotifCustomerEmail entity) {
        entity.set_body(msg.getBody());
        entity.set_customerId(msg.getCustomerId());
        entity.set_subject(msg.getSubject());

        // TODO the following fields seem to be missing from the NotifCustomerEmailMsg. Why?
        entity.set_to("");
        entity.set_toBcc("");
        entity.set_toCc("");
    }
}
