package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.DeviceCreationDescriptor;
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
        com.cannontech.messaging.serialization.thrift.generated.DeviceCreationDescriptor descriptor = entity.getDescriptor();
        if (descriptor != null) {
            msg.setDescriptor(new DeviceCreationDescriptor(descriptor.getPaoId(), descriptor.getCategory(), descriptor.getDeviceType()));
        }
        msg.setSuccess(entity.isSuccess());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                                   com.cannontech.message.porter.message.RfnDeviceCreationReply msg,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationReply entity) {
        DeviceCreationDescriptor descriptor = msg.getDescriptor();
        if (descriptor != null) {
            entity.setDescriptor(new com.cannontech.messaging.serialization.thrift.generated.DeviceCreationDescriptor(descriptor.getPaoId(), descriptor.getCategory(), descriptor.getDeviceType()));
        }
        entity.setSuccess(msg.isSuccess());
    }
}
