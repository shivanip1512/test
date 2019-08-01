package com.cannontech.web.picker;

import java.util.List;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AvaiableProgramFilter implements SqlFilter {
    private List<Integer> prorgamIds;

    public AvaiableProgramFilter(List<Integer> prorgamIds) {
        this.prorgamIds = prorgamIds;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("pao.PAObjectID").in(prorgamIds);
        return retVal;
    }
}