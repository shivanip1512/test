package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleJobTableSource extends SupportBundleSqlSource {

    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM JOB");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "JobTable.csv";
    }

}
