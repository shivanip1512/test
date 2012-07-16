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

    public void setDeviceConfigurationDao(
            DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(final ConfigurationBase configuration) {

        return new SingleProcessor<SimpleDevice>() {

            public void process(SimpleDevice device) throws ProcessingException {
                try {
                    if(configuration == null) {
                        deviceConfigurationDao.unassignConfig(device);
                    } else {
                        deviceConfigurationDao.assignConfigToDevice(configuration, device);
                    }
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage());
                }
            }

        };
    }
}
