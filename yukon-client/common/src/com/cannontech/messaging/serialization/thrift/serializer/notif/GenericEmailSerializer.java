package com.cannontech.messaging.serialization.thrift.serializer.notif;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import com.cannontech.message.notif.EmailMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.GenericEmail;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.tools.email.EmailMessage;

public class GenericEmailSerializer extends ThriftInheritanceSerializer<EmailMsg, Message, GenericEmail, com.cannontech.messaging.serialization.thrift.generated.Message> {
    
    public GenericEmailSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<EmailMsg> getTargetMessageClass() {
        return EmailMsg.class;
    }

    @Override
    protected GenericEmail createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        GenericEmail entity = new GenericEmail();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(GenericEmail entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected EmailMsg createMessageInstance() {
        return new EmailMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, GenericEmail entity, EmailMsg msg) {
        InternetAddress[] recipients = null;
        InternetAddress[] bccRecipients = null;
        if (entity.get_to() != null) {
            recipients = Arrays.stream(entity.get_to().split(",")).map(recipient -> {
                InternetAddress address = new InternetAddress();
                address.setAddress(recipient);
                return address;
            }).toArray(InternetAddress[]::new);
        }
        if (entity.get_bcc() != null) {
            bccRecipients = Arrays.stream(entity.get_bcc().split(",")).map(recipient -> {
                InternetAddress address = new InternetAddress();
                address.setAddress(recipient);
                return address;
            }).toArray(InternetAddress[]::new);
        }
        InternetAddress fromAddress = new InternetAddress();
        fromAddress.setAddress(entity.get_from());

        String subject = entity.get_subject();
        String body = entity.get_body();

        try {
            EmailMessage message = new EmailMessage(fromAddress, recipients, null, bccRecipients, subject, body);
            msg.setMessage(message);
        } catch (MessagingException e) {
            throw new IllegalArgumentException("Error deserializing email message.", e);
        }
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, EmailMsg msg, GenericEmail entity) {
        if (msg.getMessage().getTo() != null) {
            String to = Arrays.stream(msg.getMessage().getTo()).map(InternetAddress::getAddress)
                    .collect(Collectors.joining(","));
            entity.set_to(to);
        }
        if (msg.getMessage().getBcc() != null) {
            String bcc = Arrays.stream(msg.getMessage().getBcc()).map(InternetAddress::getAddress)
                    .collect(Collectors.joining(","));
            entity.set_bcc(bcc);
        }
        entity.set_from(msg.getMessage().getFrom().getAddress());
        entity.set_subject(msg.getMessage().getSubject());
        entity.set_body(msg.getMessage().getBody());
    }
    
}
