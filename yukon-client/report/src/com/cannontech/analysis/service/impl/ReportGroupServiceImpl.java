package com.cannontech.analysis.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.analysis.data.group.SimpleReportGroup;
import com.cannontech.analysis.service.ReportGroupService;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;

public class ReportGroupServiceImpl implements ReportGroupService {

    private DeviceGroupService deviceGroupService;
    private DeviceGroupProviderDao deviceGroupDao;
    
    @Override
    public SimpleReportGroup getSimpleGroupMembership(DeviceGroup base, YukonDevice device) {

        SimpleReportGroup simpleReportGroup = new SimpleReportGroup();
        Set<DeviceGroup> membership = deviceGroupDao.getGroupMembership(base, device);
        if (!membership.isEmpty()) {
            simpleReportGroup = new SimpleReportGroup();
            simpleReportGroup.setDeviceGroup(membership.iterator().next());
            simpleReportGroup.setUnique(membership.size() == 1); //unique if only one element in set
        }
        return simpleReportGroup;
    }

    @Override
    public SimpleReportGroup getSimpleGroupMembership(SystemGroupEnum systemGroupEnum, YukonDevice device) {
        DeviceGroup storedDeviceGroup = deviceGroupService.resolveGroupName(systemGroupEnum.getFullPath());
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
    
    @Autowired
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
}
