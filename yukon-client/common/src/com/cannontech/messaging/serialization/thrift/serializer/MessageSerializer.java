package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class MessageSerializer extends
    ThriftSerializer<Message, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public MessageSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<Message> getTargetMessageClass() {
        return Message.class;
    }

    @Override
    public Message createMessageInstance() {
        return new Message();
    }

    @Override
    public com.cannontech.messaging.serialization.thrift.generated.Message createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.Message();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Message thriftMessage,
                                        Message msg) {
        msg.setPriority(thriftMessage.get_messagePriority());
        msg.setSoeTag(thriftMessage.get_soe());
        msg.setSource(thriftMessage.get_src());
        msg.setTimeStamp(ConverterHelper.millisecToDate(thriftMessage.get_messageTime()));
        msg.setUserName(thriftMessage.get_usr());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Message msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Message entity) {
        entity.set_messagePriority((byte)msg.getPriority());
        entity.set_messageTime(ConverterHelper.dateToMillisec(msg.getTimeStamp()));
        entity.set_soe(msg.getSoeTag());
        entity.set_src(msg.getSource());
        entity.set_usr(msg.getUserName());
    }
}
