package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.message.util.Message;
import com.cannontech.message.util.Ping;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class PingSerializer
    extends
    ThriftInheritanceSerializer<Ping, Message, com.cannontech.messaging.serialization.thrift.generated.Message, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public PingSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Ping> getTargetMessageClass() {
        return Ping.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        return entityParent;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Message entity) {
        return entity;
    }

    @Override
    protected Ping createMessageInstance() {
        return new Ping();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Message entity, Ping msg) {
        // Nothing to do here as Ping message is empty
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Ping msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Message entity) {
        // Nothing to do here as Ping message is empty
    }
}
