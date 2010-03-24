package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class InApplianceCategoryFilter implements SqlFilter {
    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("paobjectId IN (SELECT deviceId FROM lmProgramWebPublishing)");
        retVal.append("AND paobjectId IN (SELECT lmProgramDeviceId FROM lmControlAreaProgram)");
        return retVal;
    }
}
