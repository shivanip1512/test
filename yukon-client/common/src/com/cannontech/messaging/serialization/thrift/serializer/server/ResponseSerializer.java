package com.cannontech.messaging.serialization.thrift.serializer.server;

import com.cannontech.message.server.ServerResponseMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;
import com.cannontech.messaging.serialization.thrift.generated.ServerResponse;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ResponseSerializer
    extends
    ThriftInheritanceSerializer<ServerResponseMsg, Message, ServerResponse, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ResponseSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ServerResponseMsg> getTargetMessageClass() {
        return ServerResponseMsg.class;
    }

    @Override
    protected ServerResponse
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        ServerResponse entity = new ServerResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(ServerResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ServerResponseMsg createMessageInstance() {
        return new ServerResponseMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, ServerResponse entity,
                                                   ServerResponseMsg msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setId(entity.get_id());
        msg.setMessage(entity.get_message());
        msg.setPayload(helper.convertFromGeneric(entity.get_payload()));
        msg.setStatus(entity.get_status());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ServerResponseMsg msg,
                                                   ServerResponse entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        GenericMessage genMsg = helper.convertToGeneric(msg.getPayload());
        entity.set_hasPayload(genMsg != null);
        entity.set_id(msg.getId());
        entity.set_message(msg.getMessage());
        entity.set_payload(genMsg);
        entity.set_status(msg.getStatus());
    }
}
