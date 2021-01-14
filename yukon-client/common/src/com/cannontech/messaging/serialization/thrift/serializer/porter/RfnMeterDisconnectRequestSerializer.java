package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectRequest;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;

public class RfnMeterDisconnectRequestSerializer extends RfnSerializationHelper implements ThriftByteSerializer<RfnMeterDisconnectRequest> {

    @Override
    public byte[] toBytes(RfnMeterDisconnectRequest msg) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterDisconnectRequest();

        entity.setRfnIdentifier(convert(msg.getRfnIdentifier()));

        return serialize(entity);
    }
}
