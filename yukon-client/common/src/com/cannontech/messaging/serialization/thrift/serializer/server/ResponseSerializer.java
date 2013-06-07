package com.cannontech.messaging.serialization.thrift.serializer.server;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.server.ServerResponseMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.ServerResponse;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ResponseSerializer extends
    ThriftInheritanceSerializer<ServerResponseMessage, BaseMessage, ServerResponse, Message> {

    public ResponseSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ServerResponseMessage> getTargetMessageClass() {
        return ServerResponseMessage.class;
    }

    @Override
    protected ServerResponse createThrifEntityInstance(Message entityParent) {
        ServerResponse entity = new ServerResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(ServerResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ServerResponseMessage createMessageInstance() {
        return new ServerResponseMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, ServerResponse entity, ServerResponseMessage msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setId(entity.get_id());
        msg.setMessage(entity.get_message());
        msg.setPayload(helper.convertFromGeneric(entity.get_payload()));
        msg.setStatus(entity.get_status());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ServerResponseMessage msg, ServerResponse entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        GenericMessage genMsg = helper.convertToGeneric(msg.getPayload());
        entity.set_hasPayload(genMsg != null);
        entity.set_id(msg.getId());
        entity.set_message(msg.getMessage());
        entity.set_payload(genMsg);
        entity.set_status(msg.getStatus());
    }
}
