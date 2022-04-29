package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrUnicastRequest;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class EdgeDrUnicastRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<EdgeDrUnicastRequest> {

    @Override
    public byte[] toBytes(EdgeDrUnicastRequest request) {
        
        com.cannontech.messaging.serialization.thrift.generated.EdgeDrUnicastRequest thriftMessage = 
                new com.cannontech.messaging.serialization.thrift.generated.EdgeDrUnicastRequest();
        
        thriftMessage.setPaoIds(request.getPaoIds());
        thriftMessage.setMessageGuid(request.getMessageGuid());
        thriftMessage.setPayload(request.getPayload());
        
        return serialize(thriftMessage);
    }

}
