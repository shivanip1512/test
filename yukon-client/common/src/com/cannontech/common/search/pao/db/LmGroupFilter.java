package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LmGroupFilter implements SqlFilter {

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
    	
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("category = 'DEVICE' AND paoClass = 'GROUP'");
        return sql;
    }
}