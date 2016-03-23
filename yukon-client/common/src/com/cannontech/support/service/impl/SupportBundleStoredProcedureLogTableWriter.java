package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleStoredProcedureLogTableWriter extends SupportBundleSqlWriter {

    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM StoredProcedureLog");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "StoredProcedureLog.csv";
    }

}
