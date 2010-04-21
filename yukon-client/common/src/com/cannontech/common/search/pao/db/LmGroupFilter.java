package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LmGroupFilter implements SqlFilter {

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
    	
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("category").eq(PaoCategory.DEVICE);
        sql.append("AND");
        sql.append("paoClass").eq(PaoClass.GROUP);
        return sql;
    }
}