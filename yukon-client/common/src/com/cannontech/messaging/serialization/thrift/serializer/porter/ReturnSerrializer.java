package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.ReturnMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Multi;
import com.cannontech.messaging.serialization.thrift.generated.Return;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ReturnSerrializer extends ThriftInheritanceSerializer<ReturnMessage, BaseMessage, Return, Message> {

    public ReturnSerrializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ReturnMessage> getTargetMessageClass() {
        return ReturnMessage.class;
    }

    @Override
    protected Return createThrifEntityInstance(Message entityParent) {
        Return entity = new Return();
        Multi multi = new Multi();
        multi.set_baseMessage(entityParent);
        entity.set_baseMessage(multi);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(Return entity) {
        return entity.get_baseMessage().get_baseMessage();
    }

    @Override
    protected ReturnMessage createMessageInstance() {
        return new ReturnMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Return entity, ReturnMessage msg) {
        msg.setAttemptNum(entity.get_attemptNum());
        msg.setCommandString(entity.get_commandString());
        msg.setDeviceId(entity.get_deviceId());
        msg.setExpectMore(entity.is_expectMore());
        msg.setGroupMessageId(entity.get_groupMessageId());
        msg.setMacroOffset(entity.get_macroOffset());
        msg.setResultString(entity.get_resultString());
        msg.setRouteOffset(entity.get_routeId());
        msg.setStatus(entity.get_status());
        msg.setUserMessageId(entity.get_userMessageId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ReturnMessage msg, Return entity) {
        entity.set_attemptNum(msg.getAttemptNum());
        entity.set_commandString(msg.getCommandString());
        entity.set_deviceId(msg.getDeviceId());
        entity.set_expectMore(msg.getExpectMore());
        entity.set_groupMessageId(ConverterHelper.UnsignedToInt(msg.getGroupMessageId()));
        entity.set_macroOffset(msg.getMacroOffset());
        entity.set_resultString(msg.getResultString());
        entity.set_routeId(msg.getRouteOffset());
        entity.set_status(msg.getStatus());
        entity.set_userMessageId(ConverterHelper.UnsignedToInt(msg.getUserMessageId()));
    }
}
