package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.read.RfnMeterReadReply;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;
import com.cannontech.messaging.serialization.thrift.util.ThriftEnumHelper;

public class RfnMeterReadReplySerializer extends RfnSerializationHelper implements ThriftByteDeserializer<RfnMeterReadReply> {

    private static final ThriftEnumHelper<
                com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingReplyType, 
                com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType> replyTypeHelper = 
        ThriftEnumHelper.of(
                com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadingReplyType.class, 
                com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType.class);
    
    @Override
    public RfnMeterReadReply fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadReply();
        
        deserialize(msgBytes, entity);
        
        var msg = new RfnMeterReadReply();
        
        msg.setReplyType(replyTypeHelper.toJava(entity.getReplyType()));

        return msg;
    }
}
