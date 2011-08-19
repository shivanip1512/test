package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleEventLogAggregatedResultsSource extends SupportBundleSqlSource {

    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*), EventType");
        sql.append("FROM EventLog");
        sql.append("GROUP BY EventType");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "EventLogAggregatedResults.csv";
    }

}
