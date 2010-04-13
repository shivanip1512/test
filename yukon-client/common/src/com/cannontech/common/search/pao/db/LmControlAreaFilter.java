package com.cannontech.common.search.pao.db;

import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class LmControlAreaFilter implements SqlFilter {

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
    	
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("type = '" + PaoType.LM_CONTROL_AREA.getDbString() + "'");
        return sql;
    }
}