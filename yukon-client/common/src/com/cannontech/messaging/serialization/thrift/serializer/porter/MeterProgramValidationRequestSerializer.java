package com.cannontech.messaging.serialization.thrift.serializer.porter;

import com.cannontech.message.porter.message.MeterProgramValidationRequest;
import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;

public class MeterProgramValidationRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<MeterProgramValidationRequest> {

    @Override
    public byte[] toBytes(MeterProgramValidationRequest msg) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.porter.MeterProgramValidationRequest();

        entity.set_meterProgramGuid(msg.getMeterProgramGuid().toString());
                             
        return serialize(entity);
    }
}
