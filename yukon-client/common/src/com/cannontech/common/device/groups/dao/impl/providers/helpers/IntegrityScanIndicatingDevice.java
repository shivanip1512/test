package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class IntegrityScanIndicatingDevice implements ScanIndicatingDevice {

    private SqlStatementBuilder sql;
    
    IntegrityScanIndicatingDevice() {
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceScanRate.DeviceId");
        sql.append("FROM DeviceScanRate");
        sql.append("WHERE DeviceScanRate.ScanType='Integrity'");
    }
    
    public SqlFragmentSource getDeviceIdSql() {
        return this.sql;
    }
}