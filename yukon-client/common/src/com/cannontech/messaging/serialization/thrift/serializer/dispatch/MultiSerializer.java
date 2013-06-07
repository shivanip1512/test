package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Multi;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

@SuppressWarnings("rawtypes")
public class MultiSerializer extends ThriftInheritanceSerializer<MultiMessage, BaseMessage, Multi, Message> {

    public MultiSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    protected Message getThriftEntityParent(Multi entity) {
        return entity.get_baseMessage();
    }

    @Override
    public Class<MultiMessage> getTargetMessageClass() {
        return MultiMessage.class;
    }

    @Override
    public MultiMessage createMessageInstance() {
        return new MultiMessage();
    }

    @Override
    protected Multi createThrifEntityInstance(Message entityParent) {
        Multi entity = new Multi();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, MultiMessage msg, Multi entity) {
        List<GenericMessage> genericMsgList = new ArrayList<GenericMessage>();
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        for (Object innerMessage : msg.getVector()) {
            genericMsgList.add(helper.convertToGeneric(innerMessage));
        }
        entity.set_bag(genericMsgList);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Multi entity, MultiMessage msg) {
        Vector<Object> msgList = new Vector<Object>();

        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        for (GenericMessage innerMsg : entity.get_bag()) {
            msgList.add(helper.convertFromGeneric(innerMsg));
        }
        msg.setVector(msgList);
    }
}
