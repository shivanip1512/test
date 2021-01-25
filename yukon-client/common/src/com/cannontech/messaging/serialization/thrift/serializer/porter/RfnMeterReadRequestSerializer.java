package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.amr.rfn.message.read.RfnMeterReadRequest;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.serializer.RfnSerializationHelper;

public class RfnMeterReadRequestSerializer extends RfnSerializationHelper implements ThriftByteSerializer<RfnMeterReadRequest> {

    @Override
    public byte[] toBytes(RfnMeterReadRequest msg) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.RfnMeterReadRequest();

        entity.setRfnIdentifier(convert(msg.getRfnIdentifier()));
                             
        return serialize(entity);
    }
}
