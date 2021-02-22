package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;
import com.cannontech.messaging.serialization.thrift.util.ThriftEnumHelper;

public class RfnMeterDisconnectRequestSerializer extends RfnSerializationHelper implements ThriftByteSerializer<RfnMeterDisconnectRequest> {

    private static final ThriftEnumHelper<
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectCmdType, 
                    com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType> actionHelper = 
            ThriftEnumHelper.of(
                    com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectCmdType.class, 
                    com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectCmdType.class);

    @Override
    public byte[] toBytes(RfnMeterDisconnectRequest msg) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectRequest();

        entity.setRfnIdentifier(convert(msg.getRfnIdentifier()));
        entity.setAction(actionHelper.toThrift(msg.getAction()));

        return serialize(entity);
    }
}
