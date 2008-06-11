package com.cannontech.analysis.service.impl;

import java.util.Set;

import com.cannontech.analysis.data.group.SimpleReportGroup;
import com.cannontech.analysis.service.ReportGroupService;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class ReportGroupServiceImpl implements ReportGroupService {

    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    
    @Override
    public SimpleReportGroup getSimpleGroupMembership(StoredDeviceGroup base, YukonDevice device) {

        SimpleReportGroup simpleReportGroup = new SimpleReportGroup();
        Set<StoredDeviceGroup> membership = deviceGroupMemberEditorDao.getGroupMembership(base, device);
        if (!membership.isEmpty()) {
            simpleReportGroup = new SimpleReportGroup();
            simpleReportGroup.setDeviceGroup(membership.iterator().next());
            simpleReportGroup.setUnique(membership.size() == 1); //unique if only one element in set
        }
        return simpleReportGroup;
    }

    @Override
    public SimpleReportGroup getSimpleGroupMembership(SystemGroupEnum systemGroupEnum, YukonDevice device) {
        StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroupEnum);
        return getSimpleGroupMembership(storedDeviceGroup, device);
    }
    
    @Override
    public String getPartialGroupName(SystemGroupEnum systemGroupEnum, SimpleReportGroup simpleReportGroup) {
        String parentName = systemGroupEnum.getFullPath();
        if (simpleReportGroup.getDeviceGroup() != null) {
            String partialName = simpleReportGroup.getDeviceGroup().getFullName().replaceFirst(parentName, "");
            return partialName + (simpleReportGroup.getUnique() ? "": SimpleReportGroup.NON_UNIQUE_IDENTIFIER);
        }
        return null;
    }
    
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
}
