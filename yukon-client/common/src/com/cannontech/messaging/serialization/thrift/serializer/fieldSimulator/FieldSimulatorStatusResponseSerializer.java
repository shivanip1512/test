package com.cannontech.messaging.serialization.thrift.serializer.fieldSimulator;

import com.cannontech.dr.rfn.model.FieldSimulatorSettings;
import com.cannontech.messaging.serialization.thrift.ThriftByteDeserializer;
import com.cannontech.messaging.serialization.thrift.SimpleThriftSerializer;
import com.cannontech.simulators.message.response.FieldSimulatorStatusResponse;

public class FieldSimulatorStatusResponseSerializer extends SimpleThriftSerializer implements ThriftByteDeserializer<FieldSimulatorStatusResponse> {

    @Override
    public FieldSimulatorStatusResponse fromBytes(byte[] msgBytes) {

        var entity = new com.cannontech.messaging.serialization.thrift.generated.fieldSimulator.FieldSimulatorStatusResponse();
        
        deserialize(msgBytes, entity);
        
        var settings = new FieldSimulatorSettings();
        
        settings.setDeviceGroup(
                entity.get_settings().get_deviceGroup());
        settings.setDeviceConfigFailureRate(
                entity.get_settings().get_deviceConfigFailureRate());
        
        var response = new FieldSimulatorStatusResponse();
        
        response.setSettings(settings);
        response.setSuccessful(true);
        
        return response;
    }

}
