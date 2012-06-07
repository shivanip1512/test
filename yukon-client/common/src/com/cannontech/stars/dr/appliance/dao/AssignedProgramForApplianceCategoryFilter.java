package com.cannontech.stars.dr.appliance.dao;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public class AssignedProgramForApplianceCategoryFilter implements UiFilter<AssignedProgram> {
    private Iterable<Integer> applianceCategoryIds;

    public AssignedProgramForApplianceCategoryFilter(Iterable<Integer> applianceCategoryIds) {
        this.applianceCategoryIds = applianceCategoryIds;
    }

    @Override
    public Iterable<PostProcessingFilter<AssignedProgram>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public Iterable<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("p.applianceCategoryId").in(applianceCategoryIds);
                return retVal;
            }});

        return retVal;
    }
}
