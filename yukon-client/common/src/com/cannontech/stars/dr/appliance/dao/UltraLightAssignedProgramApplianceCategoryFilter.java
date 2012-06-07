package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class UltraLightAssignedProgramApplianceCategoryFilter implements
        SqlFilter {
    private List<Integer> applianceCategoryIds;

    public UltraLightAssignedProgramApplianceCategoryFilter(
            List<Integer> applianceCategoryIds) {
        this.applianceCategoryIds = applianceCategoryIds;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("p.applianceCategoryId").in(applianceCategoryIds);
        return retVal;
    }
}
