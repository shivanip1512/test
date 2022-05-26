package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrUnicastRequest;
import com.cannontech.dr.edgeDr.EdgeUnicastPriority;
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
        
        thriftMessage.setQueuePriority(convertPriority(request.getQueuePriority()));
        thriftMessage.setQueuePriority(convertPriority(request.getNetworkPriority()));
        
        return serialize(thriftMessage);
    }
    
    /**
     * Convert from the priority in the POJO to the identical priority in the Thrift object.
     * If new priority values are ever added, this method will default any unknown values to HIGH.
     */
    private com.cannontech.messaging.serialization.thrift.generated.EdgeUnicastPriority convertPriority(EdgeUnicastPriority objectPriority) {
        if (objectPriority == EdgeUnicastPriority.LOW) {
            return com.cannontech.messaging.serialization.thrift.generated.EdgeUnicastPriority.LOW;
        }
        return com.cannontech.messaging.serialization.thrift.generated.EdgeUnicastPriority.HIGH;
    }

}
