package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlStatementBuilder;

public class LoadProfileScanIndicatingDevice implements ScanIndicatingDevice {

    private String sql;
    
    LoadProfileScanIndicatingDevice() {
        
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("SELECT DeviceLoadProfile.DeviceId");
        builder.append("FROM DeviceLoadProfile");
        builder.append("WHERE DeviceLoadProfile.LoadProfileCollection LIKE 'Y___'");
        this.sql = builder.toString();
    }
    
    public String getDeviceIdSql() {
        return this.sql;
    }
}