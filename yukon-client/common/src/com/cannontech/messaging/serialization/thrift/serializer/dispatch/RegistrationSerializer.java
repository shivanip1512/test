package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class RegistrationSerializer
    extends
    ThriftInheritanceSerializer<Registration, Message, com.cannontech.messaging.serialization.thrift.generated.Registration, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public RegistrationSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Registration> getTargetMessageClass() {
        return Registration.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Registration
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Registration entity =
            new com.cannontech.messaging.serialization.thrift.generated.Registration();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Registration entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Registration createMessageInstance() {
        return new Registration();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Registration entity,
                                        Registration msg) {
        msg.setAppExpirationDelay(entity.get_appExpirationDelay());
        msg.setAppIsUnique(entity.get_appIsUnique());
        msg.setAppName(entity.get_appName());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Registration msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Registration entity) {
        entity.set_appExpirationDelay(msg.getAppExpirationDelay());
        // this cast will loose accuracy but it is there for legacy reasons : this is how it was handled in RogueWave
        // serialization
        entity.set_appId((int) msg.getRegID());
        entity.set_appIsUnique(msg.getAppIsUnique());
        entity.set_appName(msg.getAppName());
    }

}
