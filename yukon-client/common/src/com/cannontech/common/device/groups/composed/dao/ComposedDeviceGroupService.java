package com.cannontech.common.device.groups.composed.dao;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;

public interface ComposedDeviceGroupService {

    void save(DeviceGroupComposed composedGroup, DeviceGroupComposedCompositionType compositionType, List<ComposedGroup> displayableComposedGroups);

    List<ComposedGroup> getGroupsForComposedGroup(DeviceGroupComposed composedGroup);
}