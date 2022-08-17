package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleEventLogAggregatedResultsWriter extends SupportBundleSqlWriter {
    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventType, COUNT(*)");
        sql.append("FROM EventLog");
        sql.append("GROUP BY EventType");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "EventLogAggregatedResults.csv";
    }
}
