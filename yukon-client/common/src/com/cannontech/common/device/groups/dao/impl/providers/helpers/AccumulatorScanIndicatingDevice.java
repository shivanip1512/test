package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlStatementBuilder;

public class AccumulatorScanIndicatingDevice implements ScanIndicatingDevice{

    private String sql;
    
    AccumulatorScanIndicatingDevice() {
        
        SqlStatementBuilder builder = new SqlStatementBuilder();
        builder.append("SELECT DeviceScanRate.DeviceId");
        builder.append("FROM DeviceScanRate");
        builder.append("WHERE DeviceScanRate.ScanType='Accumulator'");
        this.sql = builder.toString();
    }
    
    public String getDeviceIdSql() {
        return this.sql;
    }
}
