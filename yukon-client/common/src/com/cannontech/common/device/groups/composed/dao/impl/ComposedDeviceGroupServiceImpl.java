package com.cannontech.common.device.groups.composed.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.groups.composed.dao.ComposedDeviceGroupService;
import com.cannontech.common.device.groups.composed.dao.ComposedGroup;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;

public class ComposedDeviceGroupServiceImpl implements ComposedDeviceGroupService {

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupComposedDao deviceGroupComposedDao;
    @Autowired private DeviceGroupComposedGroupDao deviceGroupComposedGroupDao;

    @Override
    @Transactional
    public void save(DeviceGroupComposed composedGroup, DeviceGroupComposedCompositionType compositionType,
            List<ComposedGroup> displayableComposedGroups) {

        // remove current groups
        deviceGroupComposedGroupDao.removeAllGroups(composedGroup.getDeviceGroupComposedId());

        // update composition type
        composedGroup.setDeviceGroupComposedCompositionType(compositionType);
        deviceGroupComposedDao.saveOrUpdate(composedGroup);

        // add new groups
        for (ComposedGroup displayableComposedGroup : displayableComposedGroups) {

            String groupFullName = displayableComposedGroup.getGroupFullName();
            DeviceGroup group = deviceGroupService.findGroupName(groupFullName);
            if (group != null) {

                DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(groupFullName);
                DeviceGroupComposedGroup compositionGroup = new DeviceGroupComposedGroup();
                compositionGroup.setDeviceGroupComposedId(composedGroup.getDeviceGroupComposedId());
                compositionGroup.setDeviceGroup(deviceGroup);
                compositionGroup.setNot(displayableComposedGroup.isNegate());

                deviceGroupComposedGroupDao.saveOrUpdate(compositionGroup);
            }
        }
    }

    @Override
    public List<ComposedGroup> getGroupsForComposedGroup(DeviceGroupComposed group) {
        List<DeviceGroupComposedGroup> compositionGroups = deviceGroupComposedGroupDao.getComposedGroupsForId(group.getDeviceGroupComposedId());

        List<ComposedGroup> memberGroups = new ArrayList<ComposedGroup>();
        for (DeviceGroupComposedGroup compositionGroup : compositionGroups) {

            ComposedGroup displayableComposedGroup = new ComposedGroup(compositionGroup.getDeviceGroup(), compositionGroup.isNot());
            memberGroups.add(displayableComposedGroup);
        }

        return memberGroups;
    }
}