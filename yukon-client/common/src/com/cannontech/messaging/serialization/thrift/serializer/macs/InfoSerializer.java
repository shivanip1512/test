package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.Info;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCInfo;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class InfoSerializer extends
    ThriftInheritanceSerializer<Info, Message, MCInfo, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public InfoSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Info> getTargetMessageClass() {
        return Info.class;
    }

    @Override
    protected MCInfo
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCInfo entity = new MCInfo();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(MCInfo entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Info createMessageInstance() {
        return new Info();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCInfo entity, Info msg) {
        msg.setId(entity.get_id());
        msg.setInfo(entity.get_info());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Info msg, MCInfo entity) {
        entity.set_id((int)msg.getId());
        entity.set_info(msg.getInfo());
    }
}
