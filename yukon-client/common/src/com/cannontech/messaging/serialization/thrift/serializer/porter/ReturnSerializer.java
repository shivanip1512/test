package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Multi;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ReturnSerializer
    extends
    ThriftInheritanceSerializer<Return, Message, com.cannontech.messaging.serialization.thrift.generated.Return, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ReturnSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Return> getTargetMessageClass() {
        return Return.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Return
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Return entity =
            new com.cannontech.messaging.serialization.thrift.generated.Return();
        Multi multi = new Multi();
        multi.set_baseMessage(entityParent);
        entity.set_baseMessage(multi);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Return entity) {
        return entity.get_baseMessage().get_baseMessage();
    }

    @Override
    protected Return createMessageInstance() {
        return new Return();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Return entity,
                                        Return msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setAttemptNum(entity.get_attemptNum());
        msg.setCommandString(entity.get_commandString());
        msg.setDeviceID(entity.get_deviceId());
        msg.setExpectMore(ConverterHelper.boolToInt(entity.is_expectMore()));
        msg.setGroupMessageID(entity.get_groupMessageId());
        msg.setMacroOffset(entity.get_macroOffset());
        msg.setResultString(entity.get_resultString());
        msg.setRouteOffset(entity.get_routeId());
        msg.setStatus(entity.get_status());
        msg.setUserMessageID(entity.get_userMessageId());
        msg.setMessages(helper.convertToMessageList(entity.get_baseMessage().get_bag(), Message.class));
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Return msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Return entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_attemptNum(msg.getAttemptNum());
        entity.set_commandString(msg.getCommandString());
        entity.set_deviceId(msg.getDeviceID());
        entity.set_expectMore(ConverterHelper.intToBool(msg.getExpectMore()));
        entity.set_groupMessageId((int) msg.getGroupMessageID());
        entity.set_macroOffset(msg.getMacroOffset());
        entity.set_resultString(msg.getResultString());
        entity.set_routeId(msg.getRouteOffset());
        entity.set_status(msg.getStatus());
        entity.set_userMessageId((int) msg.getUserMessageID());

        // we have to populate the multi message as well (event if this hierarchy does not exist in java, it does on the
        // cpp side)
        entity.get_baseMessage().set_bag(helper.convertToGenericList(msg.getMessages()));
    }
}
