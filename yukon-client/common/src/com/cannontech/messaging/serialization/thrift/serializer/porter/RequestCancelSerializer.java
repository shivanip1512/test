package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.RequestCancelMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.RequestCancel;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class RequestCancelSerializer extends
    ThriftInheritanceSerializer<RequestCancelMessage, BaseMessage, RequestCancel, Message> {

    public RequestCancelSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RequestCancelMessage> getTargetMessageClass() {
        return RequestCancelMessage.class;
    }

    @Override
    protected RequestCancel createThrifEntityInstance(Message entityParent) {
        RequestCancel entity = new RequestCancel();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(RequestCancel entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RequestCancelMessage createMessageInstance() {
        return new RequestCancelMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, RequestCancel entity, RequestCancelMessage msg) {
        msg.setRequestId(entity.get_RequestId());
        msg.setRequestIdCount(entity.get_RequestIdCount());
        msg.setTime(ConverterHelper.millisecToDate(entity.get_Time()));
        msg.setUserMessageId(entity.get_UserMessageId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RequestCancelMessage msg, RequestCancel entity) {
        // TODO Conversion from long to int on many field
        entity.set_RequestId((int) msg.getRequestId());
        entity.set_RequestIdCount((int) msg.getRequestIdCount());
        entity.set_Time(ConverterHelper.dateToMillisec(msg.getTime()));
        entity.set_UserMessageId((int) msg.getUserMessageId());
    }
}
