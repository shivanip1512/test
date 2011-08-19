package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleRawPointHistoryCountSource extends SupportBundleSqlSource {

    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM RawPointHistory");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "RawPointHistoryCount.csv";
    }

}
