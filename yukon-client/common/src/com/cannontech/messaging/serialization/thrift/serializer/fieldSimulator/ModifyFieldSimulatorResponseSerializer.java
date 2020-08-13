package com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator;

import com.cannontech.dr.rfn.model.FieldSimulatorSettings;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.simulators.message.response.ModifyFieldSimulatorResponse;

public class ModifyFieldSimulatorResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<ModifyFieldSimulatorResponse> {

    @Override
    public ModifyFieldSimulatorResponse fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.fieldSimulator.FieldSimulatorConfigurationResponse();
        
        deserialize(msgBytes, entity);
        
        var settings = new FieldSimulatorSettings();

        settings.setDeviceGroup(
                entity.get_settings().get_deviceGroup());
        settings.setDeviceConfigFailureRate(
                entity.get_settings().get_deviceConfigFailureRate());
        
        var response = new ModifyFieldSimulatorResponse();

        response.setSettings(settings);
        response.setSuccessful(entity.is_success());
        
        return response;
    }

}
