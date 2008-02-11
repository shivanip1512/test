package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlStatementBuilder;

public class VoltageProfileScanIndicatingDevice implements ScanIndicatingDevice {

    private String sql;
    
    VoltageProfileScanIndicatingDevice() {
        
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("SELECT DeviceLoadProfile.DeviceId");
        builder.append("FROM DeviceLoadProfile");
        builder.append("WHERE DeviceLoadProfile.LoadProfileCollection LIKE '___Y'");
        this.sql = builder.toString();
    }
    
    public String getDeviceIdSql() {
        return this.sql;
    }
}
