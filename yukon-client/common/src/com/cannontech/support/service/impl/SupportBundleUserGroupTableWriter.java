package com.cannontech.support.service.impl;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

/**
 * List of all User Groups having at least 1 Role Group association. 
 * Includes all Role Groups for each User Group.
 */
public class SupportBundleUserGroupTableWriter extends SupportBundleSqlWriter {
    @Override
    protected SqlFragmentSource getSqlFragmentSource() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ug.UserGroupId, ug.Name, ug.Description, yg.GroupId, yg.GroupName, yg.GroupDescription");
        sql.append("FROM UserGroup ug JOIN UserGroupToYukonGroupMapping map on ug.UserGroupId = map.UserGroupId");
        sql.append("JOIN YukonGroup yg on yg.GroupID = map.GroupId");
        sql.append("ORDER BY ug.UserGroupId, yg.GroupID");
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "UserGroupToRoleGroup.csv";
    }
}