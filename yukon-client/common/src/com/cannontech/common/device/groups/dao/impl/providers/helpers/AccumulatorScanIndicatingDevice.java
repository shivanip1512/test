package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AccumulatorScanIndicatingDevice implements ScanIndicatingDevice{

    private SqlStatementBuilder sql;
    
    AccumulatorScanIndicatingDevice() {
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceScanRate.DeviceId");
        sql.append("FROM DeviceScanRate");
        sql.append("WHERE DeviceScanRate.ScanType='Accumulator'");
    }
    
    public SqlFragmentSource getDeviceIdSql() {
        return this.sql;
    }
}
