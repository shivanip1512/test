package com.cannontech.amr.meter.dao;

import java.util.List;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.device.groups.model.DeviceGroup;


public interface GroupMetersDao {

    /**
     * Get all meters in group. Returns meters ordered by DEVICE_DISPLAY_TEMPLATE role property.
     * Only includes direct children of group.
     * @param group
     */
    List<YukonMeter> getChildMetersByGroup(DeviceGroup group);
}
