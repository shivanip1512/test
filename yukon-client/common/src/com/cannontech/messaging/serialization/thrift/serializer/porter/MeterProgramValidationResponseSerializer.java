package com.cannontech.messaging.serialization.thrift.serializer.porter;

import java.util.UUID;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.message.porter.message.MeterProgramValidationResponse;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;

public class MeterProgramValidationResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<MeterProgramValidationResponse> {

    @Override
    public MeterProgramValidationResponse fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.porter.MeterProgramValidationResponse();
        
        deserialize(msgBytes, entity);
        
        var meterProgramGuid = UUID.fromString(entity.get_meterProgramGuid());
        var status = DeviceError.getErrorByCode(entity.get_status());
        
        return new MeterProgramValidationResponse(meterProgramGuid, status);
    }
}
