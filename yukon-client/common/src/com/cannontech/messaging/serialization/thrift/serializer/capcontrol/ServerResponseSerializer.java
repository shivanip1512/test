package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlResponseType;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCServerResponse;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class ServerResponseSerializer
    extends
    ThriftInheritanceSerializer<CapControlServerResponse, Message, CCServerResponse, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ServerResponseSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CapControlServerResponse> getTargetMessageClass() {
        return CapControlServerResponse.class;
    }

    @Override
    protected CCServerResponse
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        CCServerResponse entity = new CCServerResponse();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(CCServerResponse entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CapControlServerResponse createMessageInstance() {
        return new CapControlServerResponse();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCServerResponse entity,
                                                   CapControlServerResponse msg) {
        msg.setMessageId(entity.get_messageId());
        msg.setResponseType(CapControlResponseType.getResponseTypeById(entity.get_responseType()));
        msg.setResponse(entity.get_response());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CapControlServerResponse msg,
                                                   CCServerResponse entity) {
        entity.set_messageId(msg.getMessageId());
        entity.set_responseType(msg.getResponseType().getTypeId());
        entity.set_response(msg.getResponse());
    }

}
