package com.cannontech.capcontrol.dao;

import java.util.List;

import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;

public interface CcMonitorBankListDao {
    
    /**
     * Given a zoneId, returns a list of VoltageLimitedDeviceInfo for every
     * voltage limited device attached to that zone.
     */
    public List<VoltageLimitedDeviceInfo> getDeviceInfoByZoneId(int zoneId);
    
    /**
     * Updates the Phase, Lower Limit, Upper Limit, and Override Strategy
     * values for a single device entry.
     */
    public void updateDeviceInfo(VoltageLimitedDeviceInfo deviceInfo);
    
    /**
     * Updates the Phase, Lower Limit, Upper Limit, and Override Strategy
     * values for multiple device entries.
     */
    public void updateDeviceInfo(List<VoltageLimitedDeviceInfo> deviceInfoList);
}
