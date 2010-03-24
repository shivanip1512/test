package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class PaoTypeFilter implements SqlFilter {
    private PaoType paoType;

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder retVal = new SqlStatementBuilder();
        retVal.append("type").eq(paoType.getDbString());
        return retVal;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
}
