package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.InfoMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCInfo;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class InfoSerializer extends ThriftInheritanceSerializer<InfoMessage, BaseMessage, MCInfo, Message> {

    public InfoSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<InfoMessage> getTargetMessageClass() {
        return InfoMessage.class;
    }

    @Override
    protected MCInfo createThrifEntityInstance(Message entityParent) {
        MCInfo entity = new MCInfo();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCInfo entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected InfoMessage createMessageInstance() {
        return new InfoMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCInfo entity, InfoMessage msg) {
        msg.setId(entity.get_id());
        msg.setInfo(entity.get_info());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, InfoMessage msg, MCInfo entity) {
        // TODO long to int conversion. is it expected?
        entity.set_id((int) msg.getId());
        entity.set_info(msg.getInfo());

    }

}
