package com.cannontech.common.device.groups.editor.dao.impl;

import java.util.Set;

/**
 * This is used to abstract out the dependency that PartialGroupResolver has on the 
 * DeviceGroupEditorDaoImpl.
 */
public interface PartialDeviceGroupDao {

    Set<PartialDeviceGroup> getPartialGroupsById(Set<Integer> neededIds);

}
