package com.cannontech.common.device.groups.service;

import java.util.List;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.predicate.Predicate;

public interface DeviceGroupUiService {

	/**
     * Method to get a hierarchy of the current device groups starting with the
     * given group. Predicate is evaluated to determine if child groups should be included in heirarchy.
     * @param root
     * @param predicate
     * @return
     */
    public DeviceGroupHierarchy getDeviceGroupHierarchy(DeviceGroup root, Predicate<DeviceGroup> predicate);

    /**
     * Given an existing hierarchy, filters out groups that do not pass predicate and returns new hierarchy.
     * @param hierarchy
     * @param deviceGroupPredicate
     * @return
     */
    public DeviceGroupHierarchy getFilteredDeviceGroupHierarchy(DeviceGroupHierarchy hierarchy, Predicate<DeviceGroup> deviceGroupPredicate);

    /**
     * Return a list of groups that meet the predicate constraint.
     * @param deviceGroupPredicate
     * @return
     */
    public List<DeviceGroup> getGroups(Predicate<DeviceGroup> deviceGroupPredicate);

    /**
     * Get a list of up to maxRecordCount DisplayablePao objects for the specified group.
     * @param group
     * @return
     */
    public List<DisplayablePao> getDevicesByGroup(DeviceGroup group, int maxRecordCount);

    /**
     * Get a list of up to maxRecordCount DisplayablePao objects for the direct children of the specified group.
     * @param group
     * @return
     */
    public List<DisplayablePao> getChildDevicesByGroup(DeviceGroup group, int maxRecordCount);
}
