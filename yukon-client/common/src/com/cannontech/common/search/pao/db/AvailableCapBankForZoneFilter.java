package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;


public class AvailableCapBankForZoneFilter implements SqlFilter {
    
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder notOnAnyZone = new SqlStatementBuilder();
        notOnAnyZone.append("PAObjectId NOT IN (");
        notOnAnyZone.append("  SELECT DeviceId");
        notOnAnyZone.append("  FROM CapBankToZoneMapping");
        notOnAnyZone.append(")");
        
        return notOnAnyZone;
    }
}
