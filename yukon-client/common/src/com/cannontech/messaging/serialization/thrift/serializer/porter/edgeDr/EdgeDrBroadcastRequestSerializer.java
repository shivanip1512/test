package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrBroadcastRequest;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class EdgeDrBroadcastRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<EdgeDrBroadcastRequest> {

    @Override
    public byte[] toBytes(EdgeDrBroadcastRequest request) {
        
        com.cannontech.messaging.serialization.thrift.generated.EdgeDrBroadcastRequest thriftMessage = 
                new com.cannontech.messaging.serialization.thrift.generated.EdgeDrBroadcastRequest();
        
        thriftMessage.setMessageGuid(request.getMessageGuid());
        thriftMessage.setPayload(request.getPayload());
        
        return serialize(thriftMessage);
    }

}
