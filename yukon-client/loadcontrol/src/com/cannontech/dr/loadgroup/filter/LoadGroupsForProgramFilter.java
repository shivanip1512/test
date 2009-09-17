package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LoadGroupsForProgramFilter implements UiFilter<DisplayablePao> {
    private int programId;

    public LoadGroupsForProgramFilter(int programId) {
        this.programId = programId;
    }

    @Override
    public List<PostProcessingFilter<DisplayablePao>> getPostProcessingFilters() {
        return null;
    }

    @Override
    public List<SqlFilter> getSqlFilters() {
        List<SqlFilter> retVal = new ArrayList<SqlFilter>(1);
        retVal.add(new SqlFilter(){

            @Override
            public SqlFragmentSource getWhereClauseFragment() {
                SqlStatementBuilder retVal = new SqlStatementBuilder("paObjectId IN (SELECT lmGroupDeviceId");
                retVal.append("FROM lmProgramDirectGroup WHERE deviceId =");
                retVal.appendArgument(programId);
                retVal.append(")");
                return retVal;
            }});

        return retVal;
    }
}
