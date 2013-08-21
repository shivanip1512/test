package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.util.Message;
import com.cannontech.message.porter.message.Request;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class RequestSerializer
    extends
    ThriftInheritanceSerializer<Request, Message, com.cannontech.messaging.serialization.thrift.generated.Request, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public RequestSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Request> getTargetMessageClass() {
        return Request.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Request
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Request entity =
            new com.cannontech.messaging.serialization.thrift.generated.Request();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Request entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Request createMessageInstance() {
        return new Request();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Request entity,
                                        Request msg) {
        msg.setAttemptNum(entity.get_attemptNum());
        msg.setCommandString(entity.get_commandString());
        msg.setDeviceID(entity.get_deviceId());
        msg.setGroupMessageID(entity.get_groupMessageId());
        msg.setMacroOffset(entity.get_macroOffset());
        msg.setOptionsField(entity.get_optionsField());
        msg.setRouteID(entity.get_routeId());
        msg.setUserMessageID(entity.get_userMessageId());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Request msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Request entity) {
        entity.set_attemptNum(msg.getAttemptNum());
        entity.set_commandString(msg.getCommandString());
        entity.set_deviceId(msg.getDeviceID());
        entity.set_groupMessageId(ConverterHelper.UnsignedToInt(msg.getGroupMessageID()));
        entity.set_macroOffset(msg.getMacroOffset());
        entity.set_optionsField(msg.getOptionsField());
        entity.set_routeId(msg.getRouteID());
        entity.set_userMessageId(ConverterHelper.UnsignedToInt(msg.getUserMessageID()));
    }
}
