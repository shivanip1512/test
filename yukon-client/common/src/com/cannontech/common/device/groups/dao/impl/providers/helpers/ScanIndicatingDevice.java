package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlFragmentSource;

public interface ScanIndicatingDevice {
    
    /**
     * The column name returned must be called DeviceId.  Happy SQL Times, woot, woot.
     * @return
     */
    public SqlFragmentSource getDeviceIdSql();
}
