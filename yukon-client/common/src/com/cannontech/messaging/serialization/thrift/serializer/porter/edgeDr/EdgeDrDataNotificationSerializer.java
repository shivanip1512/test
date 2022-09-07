package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrDataNotification;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;

import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;

public class EdgeDrDataNotificationSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<EdgeDrDataNotification> {

    @Override
    public byte[] toBytes(EdgeDrDataNotification request) {
        com.cannontech.messaging.serialization.thrift.generated.EdgeDrDataNotification thriftMessage = 
                new com.cannontech.messaging.serialization.thrift.generated.EdgeDrDataNotification();
        
        com.cannontech.messaging.serialization.thrift.generated.EdgeDrError edgeDrError =
                new com.cannontech.messaging.serialization.thrift.generated.EdgeDrError(request.getError().getErrorCode(),request.getError().getErrorMessage());
        
        thriftMessage.setPaoId(request.getPaoId());
        thriftMessage.setPayload(request.getPayload());
        thriftMessage.setE2eId(request.getE2eId());
        thriftMessage.setError(edgeDrError);
        
        return serialize(thriftMessage);
    }
    
}
