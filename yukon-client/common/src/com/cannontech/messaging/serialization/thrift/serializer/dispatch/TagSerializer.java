package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.message.dispatch.message.TagMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class TagSerializer
    extends
    ThriftInheritanceSerializer<TagMsg, Message, com.cannontech.messaging.serialization.thrift.generated.Tag, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public TagSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<TagMsg> getTargetMessageClass() {
        return TagMsg.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Tag
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Tag entity =
            new com.cannontech.messaging.serialization.thrift.generated.Tag();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Tag entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected TagMsg createMessageInstance() {
        return new TagMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                                   com.cannontech.messaging.serialization.thrift.generated.Tag entity,
                                                   TagMsg msg) {
        msg.setAction(entity.get_action());
        msg.setClientMessageID(entity.get_clientMsgId());
        msg.setDescriptionStr(entity.get_descriptionStr());
        msg.setInstanceID(entity.get_instanceId());
        msg.setPointID(entity.get_pointId());
        msg.setReferenceStr(entity.get_referenceStr());
        msg.setTaggedForStr(entity.get_taggedForStr());
        msg.setTagID(entity.get_tagId());
        msg.setTagTime(ConverterHelper.millisecToDate(entity.get_tagTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, TagMsg msg,
                                                   com.cannontech.messaging.serialization.thrift.generated.Tag entity) {
        entity.set_action(msg.getAction());
        entity.set_clientMsgId(msg.getClientMessageID());
        entity.set_descriptionStr(msg.getDescriptionStr());
        entity.set_instanceId(msg.getInstanceID());
        entity.set_pointId(msg.getPointID());
        entity.set_referenceStr(msg.getReferenceStr());
        entity.set_taggedForStr(msg.getTaggedForStr());
        entity.set_tagId(msg.getTagID());
        entity.set_tagTime(ConverterHelper.dateToMillisec((msg.getTagTime())));
    }
}
