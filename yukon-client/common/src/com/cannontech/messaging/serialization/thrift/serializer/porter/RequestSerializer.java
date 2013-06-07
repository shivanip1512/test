package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.RequestMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Request;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class RequestSerializer extends ThriftInheritanceSerializer<RequestMessage, BaseMessage, Request, Message> {

    public RequestSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RequestMessage> getTargetMessageClass() {
        return RequestMessage.class;
    }

    @Override
    protected Request createThrifEntityInstance(Message entityParent) {
        Request entity = new Request();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(Request entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RequestMessage createMessageInstance() {
        return new RequestMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Request entity, RequestMessage msg) {
        msg.setAttemptNum(entity.get_attemptNum());
        msg.setCommandString(entity.get_commandString());
        msg.setDeviceId(entity.get_deviceId());
        msg.setGroupMessageId(entity.get_groupMessageId());
        msg.setMacroOffset(entity.get_macroOffset());
        msg.setOptionsField(entity.get_optionsField());
        msg.setRouteId(entity.get_routeId());
        msg.setUserMessageId(entity.get_userMessageId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RequestMessage msg, Request entity) {
        entity.set_attemptNum(msg.getAttemptNum());
        entity.set_commandString(msg.getCommandString());
        entity.set_deviceId(msg.getDeviceId());
        entity.set_groupMessageId(ConverterHelper.UnsignedToInt(msg.getGroupMessageId()));
        entity.set_macroOffset(msg.getMacroOffset());
        entity.set_optionsField(msg.getMacroOffset());
        entity.set_routeId(msg.getRouteId());
        entity.set_userMessageId(ConverterHelper.UnsignedToInt(msg.getUserMessageId()));
    }
}
