package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.porter.QueueDataMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.QueueData;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class QueueDataSerializer extends ThriftInheritanceSerializer<QueueDataMessage, BaseMessage, QueueData, Message> {

    public QueueDataSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<QueueDataMessage> getTargetMessageClass() {
        return QueueDataMessage.class;
    }

    @Override
    protected QueueData createThrifEntityInstance(Message entityParent) {
        QueueData entity = new QueueData();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(QueueData entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected QueueDataMessage createMessageInstance() {
        return new QueueDataMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, QueueData entity, QueueDataMessage msg) {
        // TODO int to long conversion on many fields..
        msg.setQueueCount(entity.get_queueCount());
        msg.setQueueId(entity.get_id());
        msg.setRate(entity.get_rate());
        msg.setRequestId(entity.get_requestId());
        msg.setRequestIdCount(entity.get_requestIdCount());
        msg.setTime(ConverterHelper.millisecToDate(entity.get_aTime()));
        msg.setUserMessageId(entity.get_userMessageId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, QueueDataMessage msg, QueueData entity) {
        entity.set_aTime(ConverterHelper.dateToMillisec(msg.getTime()));
        // TODO long to int conversion on many fields..
        entity.set_id((int) msg.getQueueId());
        entity.set_queueCount((int) msg.getQueueCount());
        entity.set_rate((int) msg.getRate());
        entity.set_requestId((int) msg.getRequestId());
        entity.set_requestIdCount((int) msg.getRequestIdCount());
        entity.set_userMessageId((int) msg.getUserMessageId());
    }

}
