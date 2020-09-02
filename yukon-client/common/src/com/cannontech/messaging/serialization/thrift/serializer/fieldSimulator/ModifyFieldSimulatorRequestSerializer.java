package com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator;

import com.cannontech.messaging.serialization.thrift.ThriftByteSerializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.simulators.message.request.ModifyFieldSimulatorRequest;

public class ModifyFieldSimulatorRequestSerializer extends SimpleThriftSerializer implements ThriftByteSerializer<ModifyFieldSimulatorRequest> {

    @Override
    public byte[] toBytes(ModifyFieldSimulatorRequest msg) {
        var settings = new com.cannontech.messaging.serialization.thrift.generated.fieldSimulator.FieldSimulatorSettings();

        settings.set_deviceGroup(
                msg.getFieldSimulatorSettings().getDeviceGroup());
        settings.set_deviceConfigFailureRate(
                msg.getFieldSimulatorSettings().getDeviceConfigFailureRate());

        var entity = new com.cannontech.messaging.serialization.thrift.generated.fieldSimulator.FieldSimulatorConfigurationRequest();

        entity.set_settings(settings);
        
        return serialize(entity);
    }
}
