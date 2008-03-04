package com.cannontech.common.bulk.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private DeviceConfigurationDao deviceConfigurationDao = null;

    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public Processor<YukonDevice> createAddYukonDeviceToGroupProcessor(final StoredDeviceGroup group) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices) throws ProcessingException {
                deviceGroupMemberEditorDao.addDevices(group, devices);
            }
        };
    }

    public Processor<YukonDevice> createAssignConfigurationToYukonDeviceProcessor(
            final ConfigurationBase configuration) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices) throws ProcessingException {
                deviceConfigurationDao.assignConfigToDevices(configuration, devices);
            }
        };
    }
}
