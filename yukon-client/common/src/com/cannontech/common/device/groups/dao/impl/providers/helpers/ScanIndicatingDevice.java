package com.cannontech.common.device.groups.dao.impl.providers.helpers;

public interface ScanIndicatingDevice {
    
    /**
     * The column name returned must be called DeviceId.  Happy SQL Times, woot, woot.
     * @return
     */
    public String getDeviceIdSql();
}
