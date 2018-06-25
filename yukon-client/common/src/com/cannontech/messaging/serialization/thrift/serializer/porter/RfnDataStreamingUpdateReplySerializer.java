package com.cannontech.messaging.serialization.thrift.serializer.porter;

import org.apache.commons.lang.NotImplementedException;

import com.cannontech.message.porter.message.RfnDataStreamingUpdateReply;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class RfnDataStreamingUpdateReplySerializer extends ThriftSerializer<RfnDataStreamingUpdateReply, com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdateReply> {

    protected RfnDataStreamingUpdateReplySerializer() {
        super(RfnDataStreamingUpdateReply.class.toString());
    }
    
    protected RfnDataStreamingUpdateReplySerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<com.cannontech.message.porter.message.RfnDataStreamingUpdateReply> getTargetMessageClass() {
        return com.cannontech.message.porter.message.RfnDataStreamingUpdateReply.class;
    }

    @Override
    protected com.cannontech.message.porter.message.RfnDataStreamingUpdateReply createMessageInstance() {
        return new RfnDataStreamingUpdateReply();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdateReply createThriftEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdateReply();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdateReply entity,
                                                   com.cannontech.message.porter.message.RfnDataStreamingUpdateReply msg) {
        //  We do not expect to receive these
        throw new NotImplementedException();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory,
                                                   com.cannontech.message.porter.message.RfnDataStreamingUpdateReply msg,
                                                   com.cannontech.messaging.serialization.thrift.generated.RfnDataStreamingUpdateReply entity) {
        entity.setSuccess(msg.success);
    }
}
