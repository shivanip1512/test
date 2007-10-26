package com.cannontech.common.bulk.processor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;

    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

    public Processor<YukonDevice> createAddYukonDeviceToGroupProcessor(final StoredDeviceGroup group) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices) throws ProcessingException {
                deviceGroupMemberEditorDao.addDevices(group, (List<? extends YukonDevice>) devices);
            }
        };
    }
}
