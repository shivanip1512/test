package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleYukonUserGroupTableWriter extends SupportBundleSqlWriter {
    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonUserGroup");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "YukonUserGroupTable.csv";
    }
}
