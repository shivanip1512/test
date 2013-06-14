package com.cannontech.messaging.serialization.thrift.serializer;

import javax.management.RuntimeErrorException;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class BaseMessageSerializer extends ThriftSerializer<BaseMessage, Message> {

    public BaseMessageSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<BaseMessage> getTargetMessageClass() {
        return BaseMessage.class;
    }

    @Override
    public BaseMessage createMessageInstance() {
        return new BaseMessage();
    }

    @Override
    public Message createThrifEntityInstance() {
        return new Message();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Message thriftMessage,
                                                   BaseMessage msg) {
        msg.setPriority(thriftMessage.get_messagePriority());
        msg.setSOE_Tag(thriftMessage.get_soe());
        msg.setSource(thriftMessage.get_src());
        msg.setTimeStamp(ConverterHelper.millisecToDate(thriftMessage.get_messageTime()));
        msg.setToken(thriftMessage.get_token());
        msg.setUserName(thriftMessage.get_usr());     
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, BaseMessage msg,
                                                   Message thriftMessage) {
        thriftMessage.set_messagePriority(msg.getPriority());
        thriftMessage.set_messageTime(ConverterHelper.dateToMillisec(msg.getTimeStamp()));
        thriftMessage.set_soe(msg.getSOE_Tag());
        thriftMessage.set_src(msg.getSource());
        thriftMessage.set_token(msg.getToken());
        thriftMessage.set_usr(msg.getUserName());        
    }
}
