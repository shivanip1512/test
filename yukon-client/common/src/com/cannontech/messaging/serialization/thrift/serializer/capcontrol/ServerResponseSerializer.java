package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.capcontrol.ResponseType;
import com.cannontech.messaging.message.capcontrol.ServerResponseMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCServerResponse;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class ServerResponseSerializer extends
    ThriftInheritanceSerializer<ServerResponseMessage, BaseMessage, CCServerResponse, Message> {

    public ServerResponseSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ServerResponseMessage> getTargetMessageClass() {
        return ServerResponseMessage.class;
    }

    @Override
    protected CCServerResponse createThrifEntityInstance(Message entityParent) {
        CCServerResponse entity = new CCServerResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(CCServerResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ServerResponseMessage createMessageInstance() {
        return new ServerResponseMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCServerResponse entity, ServerResponseMessage msg) {
        msg.setMessageId(entity.get_messageId());
        msg.setResponseType(ResponseType.getResponseTypeById(entity.get_responseType()));
        msg.setResponse(entity.get_response());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ServerResponseMessage msg, CCServerResponse entity) {
        entity.set_messageId(msg.getMessageId());
        entity.set_responseType(msg.getResponseType().ordinal());
        entity.set_response(msg.getResponse());
    }

}
