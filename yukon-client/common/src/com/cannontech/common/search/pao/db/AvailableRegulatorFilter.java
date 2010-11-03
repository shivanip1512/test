package com.cannontech.common.search.pao.db;

import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.bulk.filter.SqlFilter;

public class AvailableRegulatorFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();
        
        SqlStatementBuilder notAttachedToAnyZone = new SqlStatementBuilder();
        notAttachedToAnyZone.append("PAObjectId NOT IN (SELECT RegulatorId");
        notAttachedToAnyZone.append("                   FROM Zone)");
        retVal.add(notAttachedToAnyZone);
        
        return retVal;
    }
}
