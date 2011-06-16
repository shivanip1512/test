package com.cannontech.common.search.pao.db;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;

public class AvailableRegulatorFilter implements SqlFilter {
    private Integer zoneId;

    public AvailableRegulatorFilter(Integer zoneId) {
        super();
        this.zoneId = zoneId;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {        
        SqlStatementBuilder notAttachedToAnyZone = new SqlStatementBuilder();
        notAttachedToAnyZone.append("PAObjectId NOT IN (");
        notAttachedToAnyZone.append("  SELECT RegulatorId");
        notAttachedToAnyZone.append("  FROM RegulatorToZoneMapping");
        if (zoneId != null) {
            notAttachedToAnyZone.append("  WHERE ZoneId").neq(zoneId);
        }
        notAttachedToAnyZone.append(")");
        
        return notAttachedToAnyZone;
    }
}
