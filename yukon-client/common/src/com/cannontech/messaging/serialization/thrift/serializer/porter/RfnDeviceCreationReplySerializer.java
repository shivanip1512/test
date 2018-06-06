package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.porter.message.RfnDeviceCreationReply;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class RfnDeviceCreationReplySerializer extends ThriftSerializer<RfnDeviceCreationReply, com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationReply> {

    protected RfnDeviceCreationReplySerializer() {
        super(RfnDeviceCreationReply.class.toString());
    }
    
    protected RfnDeviceCreationReplySerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<com.cannontech.message.porter.message.RfnDeviceCreationReply> getTargetMessageClass() {
        return com.cannontech.message.porter.message.RfnDeviceCreationReply.class;
    }

    @Override
    protected com.cannontech.message.porter.message.RfnDeviceCreationReply createMessageInstance() {
        return new RfnDeviceCreationReply();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationReply createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationReply();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationReply entity,
                                                   com.cannontech.message.porter.message.RfnDeviceCreationReply msg) {
        msg.setPaoId(entity.getPaoId());
        msg.setCategory(entity.getCategory());
        msg.setDeviceType(entity.getDeviceType());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                                   com.cannontech.message.porter.message.RfnDeviceCreationReply msg,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationReply entity) {
        entity.setPaoId(msg.getPaoId());
        entity.setCategory(msg.getCategory());
        entity.setDeviceType(msg.getDeviceType());
    }
}
