package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrError;
import com.cannontech.dr.edgeDr.EdgeDrUnicastResponse;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EdgeDrUnicastResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<EdgeDrUnicastResponse> {

    @Override
    public EdgeDrUnicastResponse fromBytes(byte[] msgBytes) {
        
        var thriftMessage = new com.cannontech.messaging.serialization.thrift.generated.EdgeDrUnicastResponse();
        deserialize(msgBytes, thriftMessage);

        EdgeDrError error = null;
        if(thriftMessage.isSetError()) {
                    error = new EdgeDrError(thriftMessage.getError().getErrorType(), 
                                            thriftMessage.getError().getErrorMessage());
        }
        EdgeDrUnicastResponse response = new EdgeDrUnicastResponse(thriftMessage.getMessageGuid(), thriftMessage.getPaoToE2eId(), error);
        
        return response;
    }

}
