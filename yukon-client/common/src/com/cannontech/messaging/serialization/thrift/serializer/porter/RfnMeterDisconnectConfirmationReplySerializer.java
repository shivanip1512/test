package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReply;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;
import com.cannontech.messaging.serialization.thrift.util.ThriftEnumHelper;

public class RfnMeterDisconnectConfirmationReplySerializer extends RfnSerializationHelper implements ThriftByteDeserializer<RfnMeterDisconnectConfirmationReply> {

    private static final ThriftEnumHelper<
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectConfirmationReplyType, 
                    com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType> replyTypeHelper = 
            ThriftEnumHelper.of(
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectConfirmationReplyType.class, 
                    com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType.class);
    
    private static final ThriftEnumHelper<
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectState, 
                    com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState> stateHelper = 
            ThriftEnumHelper.of(
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectState.class, 
                    com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState.class);

    @Override
    public RfnMeterDisconnectConfirmationReply fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectConfirmationReply();
        
        deserialize(msgBytes, entity);
        
        var msg = new RfnMeterDisconnectConfirmationReply();
        
        msg.setReplyType(replyTypeHelper.toJava(entity.getReplyType()));
        msg.setState(stateHelper.toJava(entity.getState()));

        return msg;
    }
}
