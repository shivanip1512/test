package com.cannontech.amr.meter.dao;

import java.util.List;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.model.DeviceGroup;


public interface GroupMetersDao {

    /**
     * Get all meters in group. Returns meters ordered by DEVICE_DISPLAY_TEMPLATE role property.
     * Includes meters of all sub-groups.
     * @param group
     * @return
     */
    public List<Meter> getMetersByGroup(DeviceGroup group);
    
    /**
     * Get all meters in group. Returns meters ordered by DEVICE_DISPLAY_TEMPLATE role property.
     * Only includes direct children of group.
     * @param group
     * @return
     */
    public List<Meter> getChildMetersByGroup(DeviceGroup group);
    
    /**
     * Get meters in group. Returns meters ordered by DEVICE_DISPLAY_TEMPLATE role property.
     * Only returns first maxRecordCount meters.
     * Only includes direct children of group.
     * @param group
     * @param maxSize
     * @return
     */
    public List<Meter> getChildMetersByGroup(DeviceGroup group, int maxRecordCount);
}
