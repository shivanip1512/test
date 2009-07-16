package com.cannontech.common.bulk.processor;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.ConfigurationBase;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    private DeviceConfigurationDao deviceConfigurationDao = null;

    public void setDeviceConfigurationDao(
            DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public Processor<YukonDevice> createAssignConfigurationToYukonDeviceProcessor(final ConfigurationBase configuration) {

        return new SingleProcessor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                try {
                    deviceConfigurationDao.assignConfigToDevice(configuration, device);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), e);
                }
            }

        };
    }
}
