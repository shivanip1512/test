package com.cannontech.common.bulk.processor;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.model.SimpleDevice;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    private DeviceConfigurationDao deviceConfigurationDao = null;

    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    @Override
    public Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(final ConfigurationBase configuration) {
        return new SingleProcessor<SimpleDevice>() {
            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    deviceConfigurationDao.assignConfigToDevice(configuration, device);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage());
                }
            }
        };
    }
    
    @Override
    public Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor() {
        return new SingleProcessor<SimpleDevice>() {
            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    deviceConfigurationDao.unassignConfig(device);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage());
                }
            }
        };
    }
}
