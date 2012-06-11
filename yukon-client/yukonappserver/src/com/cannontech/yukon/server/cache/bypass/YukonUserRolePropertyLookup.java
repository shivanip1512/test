/*
 * Created on Sep 16, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

import java.sql.SQLException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YukonUserRolePropertyLookup {

    private static YukonRowMapper<LiteYukonRole> liteYukonRoleMapper = new YukonRowMapper<LiteYukonRole>() {
        @Override
        public LiteYukonRole mapRow(YukonResultSet rs) throws SQLException {
            LiteYukonRole liteYukonRole = new LiteYukonRole();
            liteYukonRole.setRoleID(rs.getInt("RoleId"));
            liteYukonRole.setRoleName(rs.getString("RoleName"));
            liteYukonRole.setCategory(rs.getString("Category"));
            liteYukonRole.setDescription(rs.getString("RoleDescription"));
            
            return liteYukonRole;
        }};

	public static LiteYukonRole loadSpecificRole(LiteYukonUser user, int roleId) {
	    YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Distinct YR.RoleId, YR.RoleName, YR.Category, YR.RoleDescription");
        sql.append("FROM YukonGroupRole YGR");
        sql.append("  JOIN YukonRole YR ON YR.RoleId = YGR.RoleId");
        sql.append("  JOIN YukonUserGroup YUG ON YUG.GroupId = YGR.GroupId");
        sql.append("WHERE YUG.UserId").eq(user.getUserID());
        sql.append("  AND YGR.RoleId").eq(roleId);

        LiteYukonRole yukonRole = yukonJdbcTemplate.queryForObject(sql, liteYukonRoleMapper);
		return yukonRole;
	}
}
