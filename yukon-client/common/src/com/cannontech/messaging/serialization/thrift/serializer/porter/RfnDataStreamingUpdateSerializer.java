package com.cannontech.messaging.serialization.thrift.serializer.porter;

import org.apache.commons.lang3.NotImplementedException;

import com.cannontech.message.porter.message.RfnDataStreamingUpdate;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class RfnDataStreamingUpdateSerializer extends ThriftSerializer<RfnDataStreamingUpdate, com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdate> {

    protected RfnDataStreamingUpdateSerializer() {
        super(RfnDataStreamingUpdate.class.toString());
    }
    
    protected RfnDataStreamingUpdateSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<com.cannontech.message.porter.message.RfnDataStreamingUpdate> getTargetMessageClass() {
        return com.cannontech.message.porter.message.RfnDataStreamingUpdate.class;
    }

    @Override
    protected com.cannontech.message.porter.message.RfnDataStreamingUpdate createMessageInstance() {
        return new RfnDataStreamingUpdate();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdate createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdate();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdate entity,
                                                   com.cannontech.message.porter.message.RfnDataStreamingUpdate msg) {
        msg.paoId = entity.getPaoId();
        msg.json  = entity.getJson();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, RfnDataStreamingUpdate msg,
            com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdate entity) {
        //  We do not expect to send these
        throw new NotImplementedException("Cannot send " + getMessageType());
    }
}
