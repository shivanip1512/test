package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundlePaoTypeCountsWriter extends SupportBundleSqlWriter {
    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Type, COUNT(*)");
        sql.append("FROM YukonPaobject");
        sql.append("GROUP BY Type");
        sql.append("ORDER BY Type");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "PaoTypeCounts.csv";
    }
}
