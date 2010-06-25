package com.cannontech.dr.program.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class ForLoadGroupFilter implements UiFilter<DisplayablePao> {
    private int loadGroupId;

    public ForLoadGroupFilter(int loadGroupId) {
        this.loadGroupId = loadGroupId;
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
                SqlStatementBuilder retVal = new SqlStatementBuilder(
                    "paObjectId IN (SELECT deviceId"
                    + " FROM lmProgramDirectGroup WHERE lmGroupDeviceId =");
                retVal.appendArgument(loadGroupId);
                retVal.append(")");
                return retVal;
            }});

        return retVal;
    }
}
