package com.cannontech.stars.dr.appliance.dao;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.util.SqlFragmentCollection;
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
                SqlFragmentCollection retVal = SqlFragmentCollection.newOrCollection();

                SqlStatementBuilder fragment = new SqlStatementBuilder();
                fragment.append("LOWER(pao.paoName) LIKE LOWER(");
                fragment.appendArgument('%' + name + '%');
                fragment.append(")");
                retVal.add(fragment);

                fragment = new SqlStatementBuilder();
                fragment.append("LOWER(wc.alternateDisplayName) LIKE LOWER(");
                fragment.appendArgument('%' + name + '%');
                fragment.append(")");
                retVal.add(fragment);

                return retVal;
            }});

        return retVal;
    }
}
