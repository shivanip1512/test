package com.cannontech.messaging.serialization.thrift.serializer.server;

import com.cannontech.message.server.ServerRequestMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.ServerRequest;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class RequestSerializer
    extends
    ThriftInheritanceSerializer<ServerRequestMsg, Message, ServerRequest, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public RequestSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ServerRequestMsg> getTargetMessageClass() {
        return ServerRequestMsg.class;
    }

    @Override
    protected ServerRequest
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        ServerRequest entity = new ServerRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(ServerRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ServerRequestMsg createMessageInstance() {
        return new ServerRequestMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, ServerRequest entity,
                                                   ServerRequestMsg msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setId(entity.get_id());
   
        msg.setPayload(helper.convertFromGeneric(entity.get_payload()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ServerRequestMsg msg,
                                                   ServerRequest entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_id(msg.getId());
        entity.set_payload(helper.convertToGeneric(msg.getPayload()));
    }
}
