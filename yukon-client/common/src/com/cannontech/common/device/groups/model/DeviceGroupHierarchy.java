package com.cannontech.common.device.groups.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a device group hierarchy. Allows for easily traversing
 * the hierarchy from the top down.
 */
public class DeviceGroupHierarchy {

    private DeviceGroup group = null;
    private List<DeviceGroupHierarchy> childGroupList = new ArrayList<DeviceGroupHierarchy>();

    public List<DeviceGroupHierarchy> getChildGroupList() {
        return childGroupList;
    }

    public void setChildGroupList(List<DeviceGroupHierarchy> childGroupList) {
        this.childGroupList = childGroupList;
    }

    public DeviceGroup getGroup() {
        return group;
    }

    public void setGroup(DeviceGroup group) {
        this.group = group;
    }

    public boolean isChildGroupsPresent() {
        return childGroupList.size() > 0;
    }

}
