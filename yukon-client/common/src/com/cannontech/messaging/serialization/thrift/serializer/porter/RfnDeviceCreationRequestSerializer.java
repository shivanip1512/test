package com.cannontech.messaging.serialization.thrift.serializer.porter;

import org.apache.commons.lang.NotImplementedException;

import com.cannontech.message.porter.message.RfnDeviceCreationRequest;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class RfnDeviceCreationRequestSerializer extends ThriftSerializer<RfnDeviceCreationRequest, com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationRequest> {

    protected RfnDeviceCreationRequestSerializer() {
        super(RfnDeviceCreationRequest.class.toString());
    }
    
    protected RfnDeviceCreationRequestSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<com.cannontech.message.porter.message.RfnDeviceCreationRequest> getTargetMessageClass() {
        return com.cannontech.message.porter.message.RfnDeviceCreationRequest.class;
    }

    @Override
    protected com.cannontech.message.porter.message.RfnDeviceCreationRequest createMessageInstance() {
        return new RfnDeviceCreationRequest();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationRequest createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationRequest();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationRequest entity,
                                                   com.cannontech.message.porter.message.RfnDeviceCreationRequest msg) {
        msg.setRfnIdentifier(entity.getRfnIdentifier());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                                   com.cannontech.message.porter.message.RfnDeviceCreationRequest msg,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDeviceCreationRequest entity) {
        //  We do not expect to send these
        throw new NotImplementedException();
    }
}
