package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.porter.message.RequestCancel;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class RequestCancelSerializer
    extends
    ThriftInheritanceSerializer<RequestCancel, Message, com.cannontech.messaging.serialization.thrift.generated.RequestCancel, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public RequestCancelSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RequestCancel> getTargetMessageClass() {
        return RequestCancel.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.RequestCancel
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.RequestCancel entity =
            new com.cannontech.messaging.serialization.thrift.generated.RequestCancel();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.RequestCancel entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RequestCancel createMessageInstance() {
        return new RequestCancel();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.RequestCancel entity,
                                        RequestCancel msg) {
        msg.setRequestId(entity.get_RequestId());
        msg.setRequestIdCount(ConverterHelper.IntToUnsigned(entity.get_RequestIdCount()));
        msg.setTime(ConverterHelper.millisecToDate(entity.get_Time()));
        msg.setUserMessageId(entity.get_UserMessageId());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RequestCancel msg,
                                        com.cannontech.messaging.serialization.thrift.generated.RequestCancel entity) {
        entity.set_RequestId((int) msg.getRequestId());
        entity.set_RequestIdCount(ConverterHelper.UnsignedToInt(msg.getRequestIdCount()));
        entity.set_Time(ConverterHelper.dateToMillisec(msg.getTime()));
        entity.set_UserMessageId((int) msg.getUserMessageId());
    }
}
