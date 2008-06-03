package com.cannontech.common.bulk.processor;

import java.util.Collection;
import java.util.Collections;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
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

    public Processor<YukonDevice> createAssignConfigurationToYukonDeviceProcessor(
            final ConfigurationBase configuration) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {
                deviceConfigurationDao.assignConfigToDevices(configuration,
                                                             devices);
            }
        };
    }
}
