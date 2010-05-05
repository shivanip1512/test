package com.cannontech.stars.dr.appliance.dao;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.dr.appliance.model.AssignedProgram;

public class AssignedProgramNameFilter implements UiFilter<AssignedProgram> {
    private String name;

    public AssignedProgramNameFilter(String name) {
        this.name = name;
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
                retVal.append("LOWER(pao.paoName) like LOWER(");
                retVal.appendArgument('%' + name + '%');
                retVal.append(")");
                retVal.append("OR LOWER(wc.alternateDisplayName) like LOWER(");
                retVal.appendArgument('%' + name + '%');
                retVal.append(")");
                return retVal;
            }});

        return retVal;
    }
}
