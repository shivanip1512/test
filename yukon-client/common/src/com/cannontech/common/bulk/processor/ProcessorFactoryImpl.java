package com.cannontech.common.bulk.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    @Autowired private DeviceConfigurationService deviceConfigurationService = null;

    @Override
    public Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(final DeviceConfiguration configuration) {
        return new SingleProcessor<SimpleDevice>() {
            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    deviceConfigurationService.assignConfigToDevice(configuration, device);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), "invalidDeviceType", e);
                }
            }
        };
    }
    
    @Override
    public Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor() {
        return new SingleProcessor<SimpleDevice>() {
            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    deviceConfigurationService.unassignConfig(device);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), "invalidDeviceType", e);
                }
            }
        };
    }
}
