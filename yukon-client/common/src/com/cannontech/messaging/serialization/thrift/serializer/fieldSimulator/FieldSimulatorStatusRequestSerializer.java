package com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator;

import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.simulators.message.request.FieldSimulatorStatusRequest;

public class FieldSimulatorStatusRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<FieldSimulatorStatusRequest> {

    @Override
    public byte[] toBytes(FieldSimulatorStatusRequest msg) {
        var entity = new com.cannontech.messaging.serialization.thrift.generated.fieldSimulator.FieldSimulatorStatusRequest();

        return serialize(entity);
    }
}
