package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.porter.message.QueueData;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class QueueDataSerializer
    extends
    ThriftInheritanceSerializer<QueueData, Message, com.cannontech.messaging.serialization.thrift.generated.QueueData, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public QueueDataSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<QueueData> getTargetMessageClass() {
        return QueueData.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.QueueData
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.QueueData entity =
            new com.cannontech.messaging.serialization.thrift.generated.QueueData();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.QueueData entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected QueueData createMessageInstance() {
        return new QueueData();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.QueueData entity,
                                        QueueData msg) {
        msg.setQueueCount(ConverterHelper.IntToUnsigned(entity.get_queueCount()));
        msg.setQueueId(entity.get_id());
        msg.setRate(ConverterHelper.IntToUnsigned(entity.get_rate()));
        msg.setRequestId(entity.get_requestId());
        msg.setRequestIdCount(ConverterHelper.IntToUnsigned(entity.get_requestIdCount()));
        msg.setTime(ConverterHelper.millisecToDate(entity.get_aTime()));
        msg.setUserMessageId(entity.get_userMessageId());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, QueueData msg,
                                        com.cannontech.messaging.serialization.thrift.generated.QueueData entity) {
        entity.set_aTime(ConverterHelper.dateToMillisec(msg.getTime()));
        entity.set_id((int)msg.getQueueId());
        entity.set_queueCount(ConverterHelper.UnsignedToInt(msg.getQueueCount()));
        entity.set_rate(ConverterHelper.UnsignedToInt(msg.getRate()));
        entity.set_requestId((int)msg.getRequestId());
        entity.set_requestIdCount(ConverterHelper.UnsignedToInt(msg.getRequestIdCount()));
        entity.set_userMessageId((int)msg.getUserMessageId());
    }
}
