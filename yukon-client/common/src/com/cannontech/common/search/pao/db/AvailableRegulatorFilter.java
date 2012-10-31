package com.cannontech.common.search.pao.db;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.enums.RegulatorPointMapping;

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
        notAttachedToAnyZone.append("AND PAObjectID IN (");
        notAttachedToAnyZone.append("  SELECT ypo.PAObjectID");
        notAttachedToAnyZone.append("  FROM yukonPAObject ypo");
        notAttachedToAnyZone.append("  JOIN ExtraPaoPointAssignment eppa ON ypo.PAObjectID = eppa.PAObjectId");
        notAttachedToAnyZone.append("  WHERE eppa.Attribute").eq_k(RegulatorPointMapping.VOLTAGE_Y);
        notAttachedToAnyZone.append(")");
        
        return notAttachedToAnyZone;
    }
}
