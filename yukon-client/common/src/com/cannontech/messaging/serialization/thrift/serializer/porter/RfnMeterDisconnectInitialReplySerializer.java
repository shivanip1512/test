package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReply;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;
import com.cannontech.messaging.serialization.thrift.util.ThriftEnumHelper;

public class RfnMeterDisconnectInitialReplySerializer extends RfnSerializationHelper implements ThriftByteDeserializer<RfnMeterDisconnectInitialReply> {

    private static final ThriftEnumHelper<
                com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectInitialReplyType, 
                com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType> replyTypeHelper = 
        ThriftEnumHelper.of(
                com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectInitialReplyType.class, 
                com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType.class);
    
    @Override
    public RfnMeterDisconnectInitialReply fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectInitialReply();
        
        deserialize(msgBytes, entity);
        
        var msg = new RfnMeterDisconnectInitialReply();
        
        msg.setReplyType(replyTypeHelper.toJava(entity.getReplyType()));

        return msg;
    }
}
