package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

/**
 * List of all User Groups having at least 1 Role Group association. 
 * Includes all Role Groups for each User Group.
 */
public class SupportBundleGlobalSettingTableWriter extends SupportBundleSqlWriter {
    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM GlobalSetting");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "GlobalSettingTable.csv";
    }
}