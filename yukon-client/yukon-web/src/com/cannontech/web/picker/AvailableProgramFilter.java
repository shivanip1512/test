package com.cannontech.web.picker;

import java.util.List;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class AvailableProgramFilter implements SqlFilter {
    private List<Integer> programIds;

    public AvailableProgramFilter(List<Integer> programIds) {
        this.programIds = programIds;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("pao.PAObjectID").in(programIds);
        return retVal;
    }
}