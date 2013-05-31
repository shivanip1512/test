package com.cannontech.analysis.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.analysis.data.group.SimpleReportGroup;
import com.cannontech.analysis.service.ReportGroupService;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;

public class ReportGroupServiceImpl implements ReportGroupService {

    private DeviceGroupService deviceGroupService;
    private DeviceGroupProviderDao deviceGroupDao;
    
    @Override
    public SimpleReportGroup getSimpleGroupMembership(DeviceGroup base, SimpleDevice device) {

        SimpleReportGroup simpleReportGroup;
        Set<DeviceGroup> membership = deviceGroupDao.getGroupMembership(base, device);
        if (!membership.isEmpty()) {
            simpleReportGroup = new SimpleReportGroup(base, membership.iterator().next(), membership.size() == 1);
        } else {
            simpleReportGroup = new SimpleReportGroup(base);
        }
        return simpleReportGroup;
    }

    @Override
    public SimpleReportGroup getSimpleGroupMembership(SystemGroupEnum systemGroupEnum, SimpleDevice device) {
        DeviceGroup storedDeviceGroup = deviceGroupService.resolveGroupName(systemGroupEnum);
        return getSimpleGroupMembership(storedDeviceGroup, device);
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
