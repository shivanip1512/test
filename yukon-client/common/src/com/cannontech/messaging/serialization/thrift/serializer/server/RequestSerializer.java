package com.cannontech.messaging.serialization.thrift.serializer.server;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.server.ServerRequestMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.ServerRequest;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class RequestSerializer extends
    ThriftInheritanceSerializer<ServerRequestMessage, BaseMessage, ServerRequest, Message> {

    public RequestSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ServerRequestMessage> getTargetMessageClass() {
        return ServerRequestMessage.class;
    }

    @Override
    protected ServerRequest createThrifEntityInstance(Message entityParent) {
        ServerRequest entity = new ServerRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(ServerRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ServerRequestMessage createMessageInstance() {
        return new ServerRequestMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, ServerRequest entity, ServerRequestMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setId(entity.get_id());
        msg.setPayload(helper.convertFromGeneric(entity.get_payload()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ServerRequestMessage msg, ServerRequest entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        entity.set_id(msg.getId());
        entity.set_payload(helper.convertToGeneric(msg.getPayload()));
    }
}
