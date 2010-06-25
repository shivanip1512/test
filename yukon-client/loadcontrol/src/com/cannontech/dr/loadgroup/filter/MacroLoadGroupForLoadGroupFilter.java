package com.cannontech.dr.loadgroup.filter;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class MacroLoadGroupForLoadGroupFilter implements UiFilter<DisplayablePao> {
    private int loadGroupId;

    public MacroLoadGroupForLoadGroupFilter(int loadGroupId) {
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
                SqlStatementBuilder retVal = new SqlStatementBuilder();
                retVal.append("paObjectId IN (SELECT ownerId");
                retVal.append("FROM genericMacro WHERE childId =");
                retVal.appendArgument(loadGroupId);
                retVal.append(")");
                return retVal;
            }});

        return retVal;
    }
}
