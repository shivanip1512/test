package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlStatementBuilder;

public class IntegrityScanIndicatingDevice implements ScanIndicatingDevice {

    private String sql;
    
    IntegrityScanIndicatingDevice() {
        
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("SELECT DeviceScanRate.DeviceId");
        builder.append("FROM DeviceScanRate");
        builder.append("WHERE DeviceScanRate.ScanType='Integrity'");
        this.sql = builder.toString();
    }
    
    public String getDeviceIdSql() {
        return this.sql;
    }
}