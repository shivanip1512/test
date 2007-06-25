package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StaticDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;

public class StaticDeviceGroupProvider extends DeviceGroupDaoBase {
    private DeviceGroupEditorDao deviceGroupEditorDao;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        StaticDeviceGroup sdg = getStaticGroup(group);
        List<YukonDevice> childDevices = deviceGroupEditorDao.getChildDevices(sdg);
        return childDevices;
    }

    @Override
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        StaticDeviceGroup sdg = getStaticGroup(group);
        List<StaticDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(sdg);
        return childGroups;
    }

    private StaticDeviceGroup getStaticGroup(DeviceGroup group) {
        Validate.isTrue(group instanceof StaticDeviceGroup, "Group must be static at this point");
        StaticDeviceGroup sdg = (StaticDeviceGroup) group;
        return sdg;
    }
    
    @Override
    public DeviceGroup getRootGroup() {
        StaticDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        return rootGroup;
    }
    
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
}
