package com.cannontech.messaging.serialization.thrift.serializer.porter.edgeDr;

import com.cannontech.dr.edgeDr.EdgeDrDataNotification;
import com.cannontech.dr.edgeDr.EdgeDrError;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;

public class EdgeDrDataNotificationDeserializer extends SimpleThriftSerializer implements ThriftByteDeserializer<EdgeDrDataNotification> {

    @Override
    public EdgeDrDataNotification fromBytes(byte[] msgBytes) {
        
        var thriftMessage = new com.cannontech.messaging.serialization.thrift.generated.EdgeDrDataNotification();
        deserialize(msgBytes, thriftMessage);
        
        EdgeDrError error = new EdgeDrError(thriftMessage.getError().getErrorType(), 
                                            thriftMessage.getError().getErrorMessage());
        EdgeDrDataNotification notification = new EdgeDrDataNotification(thriftMessage.getPaoId(),
                                                                         thriftMessage.getPayload(),
                                                                         thriftMessage.getE2eId(),
                                                                         error);
        
        return notification;
    }
    
}
