package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class SupportBundleUserGroupToYukonGroupMappingTableWriter extends SupportBundleSqlWriter {
    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroupToYukonGroupMapping");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "UserGroupToYukonGroupMappingTable.csv";
    }
}
