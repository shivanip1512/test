package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleYukonUserTableSource extends SupportBundleSqlSource {

    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonUser");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "YukonUserTable.csv";
    }

}
