package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.RegistrationMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Registration;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class RegistrationSerializer extends
    ThriftInheritanceSerializer<RegistrationMessage, BaseMessage, Registration, Message> {

    public RegistrationSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RegistrationMessage> getTargetMessageClass() {
        return RegistrationMessage.class;
    }

    @Override
    protected Registration createThrifEntityInstance(Message entityParent) {
        Registration entity = new Registration();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(Registration entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RegistrationMessage createMessageInstance() {
        return new RegistrationMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Registration entity, RegistrationMessage msg) {
        msg.setAppExpirationDelay(entity.get_appExpirationDelay());
        msg.setAppIsUnique(entity.get_appIsUnique());
        msg.setAppKnownPort(entity.get_appKnownPort());
        msg.setAppName(entity.get_appName());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RegistrationMessage msg, Registration entity) {
        entity.set_appExpirationDelay(msg.getAppExpirationDelay());
        entity.set_appId((int) msg.getRegId());
        entity.set_appIsUnique(msg.getAppIsUnique());
        entity.set_appKnownPort(msg.getAppKnownPort());
        entity.set_appName(msg.getAppName());
    }

}
