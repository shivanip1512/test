package com.cannontech.common.device.groups.dao.impl.providers.helpers;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LoadProfileScanIndicatingDevice implements ScanIndicatingDevice {

    private SqlStatementBuilder sql;
    
    LoadProfileScanIndicatingDevice() {
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT DeviceLoadProfile.DeviceId");
        sql.append("FROM DeviceLoadProfile");
        sql.append("WHERE DeviceLoadProfile.LoadProfileCollection LIKE 'Y___'");
    }
    
    public SqlFragmentSource getDeviceIdSql() {
        return this.sql;
    }
}