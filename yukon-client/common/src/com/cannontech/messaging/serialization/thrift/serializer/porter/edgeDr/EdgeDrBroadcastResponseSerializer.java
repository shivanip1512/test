package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrBroadcastResponse;
import com.cannontech.dr.edgeDr.EdgeDrError;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EdgeDrBroadcastResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<EdgeDrBroadcastResponse> {

    @Override
    public EdgeDrBroadcastResponse fromBytes(byte[] msgBytes) {
        
        var thriftMessage = new com.cannontech.messaging.serialization.thrift.generated.EdgeDrBroadcastResponse();
        deserialize(msgBytes, thriftMessage);
        
        EdgeDrError error = new EdgeDrError(thriftMessage.getError().getErrorType(), 
                                            thriftMessage.getError().getErrorMessage());
        return new EdgeDrBroadcastResponse(thriftMessage.getMessageGuid(), error);
    }
    
}
