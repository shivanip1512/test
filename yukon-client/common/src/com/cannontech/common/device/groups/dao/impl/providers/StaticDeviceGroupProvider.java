package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class StaticDeviceGroupProvider extends DeviceGroupDaoBase {
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<YukonDevice> childDevices = deviceGroupMemberEditorDao.getChildDevices(sdg);
        return childDevices;
    }

    @Override
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(sdg);
        return childGroups;
    }

    private StoredDeviceGroup getStoredGroup(DeviceGroup group) {
        Validate.isTrue(group instanceof StoredDeviceGroup, "Group must be static at this point");
        StoredDeviceGroup sdg = (StoredDeviceGroup) group;
        return sdg;
    }
    
    @Override
    public DeviceGroup getRootGroup() {
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        return rootGroup;
    }
    
    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
}
