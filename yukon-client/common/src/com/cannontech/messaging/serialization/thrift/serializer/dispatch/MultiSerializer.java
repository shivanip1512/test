package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

@SuppressWarnings("rawtypes")
public class MultiSerializer
    extends
    ThriftInheritanceSerializer<Multi, Message, com.cannontech.messaging.serialization.thrift.generated.Multi, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public MultiSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Multi entity) {
        return entity.get_baseMessage();
    }

    @Override
    public Class<Multi> getTargetMessageClass() {
        return Multi.class;
    }

    @Override
    public Multi createMessageInstance() {
        return new Multi();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Multi
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Multi entity =
            new com.cannontech.messaging.serialization.thrift.generated.Multi();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Multi msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Multi entity) {
        List<GenericMessage> genericMsgList = new ArrayList<GenericMessage>();
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        for (Object innerMessage : msg.getVector()) {
            genericMsgList.add(helper.convertToGeneric(innerMessage));
        }
        entity.set_bag(genericMsgList);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Multi entity, Multi msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setVector(helper.convertToMessageVector(entity.get_bag(), Message.class));        
    }
}
