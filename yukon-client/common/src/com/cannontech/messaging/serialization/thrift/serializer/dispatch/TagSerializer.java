package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.TagMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Tag;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class TagSerializer extends ThriftInheritanceSerializer<TagMessage, BaseMessage, Tag, Message> {

    public TagSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<TagMessage> getTargetMessageClass() {
        return TagMessage.class;
    }

    @Override
    protected Tag createThrifEntityInstance(Message entityParent) {
        Tag entity = new Tag();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(Tag entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected TagMessage createMessageInstance() {
        return new TagMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Tag entity, TagMessage msg) {
        msg.setAction(entity.get_action());
        msg.setClientMessageId(entity.get_clientMsgId());
        msg.setDescriptionStr(entity.get_descriptionStr());
        msg.setInstanceId(entity.get_instanceId());
        msg.setPointId(entity.get_pointId());
        msg.setReferenceStr(entity.get_referenceStr());
        msg.setTaggedForStr(entity.get_taggedForStr());
        msg.setTagId(entity.get_tagId());
        msg.setTagTime(ConverterHelper.millisecToDate(entity.get_tagTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, TagMessage msg, Tag entity) {
        entity.set_action(msg.getAction());
        entity.set_clientMsgId(msg.getClientMessageId());
        entity.set_descriptionStr(msg.getDescriptionStr());
        entity.set_instanceId(msg.getInstanceId());
        entity.set_pointId(msg.getPointId());
        entity.set_referenceStr(msg.getReferenceStr());
        entity.set_taggedForStr(msg.getTaggedForStr());
        entity.set_tagId(msg.getTagId());
        entity.set_tagTime(ConverterHelper.dateToMillisec((msg.getTagTime())));
    }
}
