package com.cannontech.common.search.pao.db;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;

public class AvailableRegulatorFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {        
        SqlStatementBuilder notAttachedToAnyZone = new SqlStatementBuilder();
        notAttachedToAnyZone.append("PAObjectId NOT IN (");
        notAttachedToAnyZone.append("  SELECT RegulatorId");
        notAttachedToAnyZone.append("  FROM Zone");
        notAttachedToAnyZone.append(")");
        
        return notAttachedToAnyZone;
    }
}
