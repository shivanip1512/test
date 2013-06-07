package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.TraceMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Trace;

public class TraceSerializer extends ThriftInheritanceSerializer<TraceMessage, BaseMessage, Trace, Message> {

    public TraceSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<TraceMessage> getTargetMessageClass() {
        return TraceMessage.class;
    }

    @Override
    protected Trace createThrifEntityInstance(Message entityParent) {
        Trace entity = new Trace();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(Trace entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected TraceMessage createMessageInstance() {
        return new TraceMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Trace entity, TraceMessage msg) {
        msg.setAttributes(entity.get_attributes());
        msg.setEnd(entity.is_end());
        msg.setTrace(entity.get_trace());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, TraceMessage msg, Trace entity) {
        entity.set_attributes(msg.getAttributes());
        entity.set_end(msg.isEnd());
        entity.set_trace(msg.getTrace());
    }
}
