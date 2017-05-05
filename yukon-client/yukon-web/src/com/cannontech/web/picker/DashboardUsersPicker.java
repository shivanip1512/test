package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.result.UltraLightYukonUser;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.google.common.collect.Lists;

public class DashboardUsersPicker extends DatabasePicker<UltraLightYukonUser> {
    
    @Autowired private DashboardDao dashboardDao;

    private final static String[] searchColumnNames = new String[] {
        "UserId", "UserName", "Name"};
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("userName", "yukon.web.picker.user.name"));        
        columns.add(new OutputColumn("userGroupName", "yukon.web.picker.user.userGroupName"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    protected DashboardUsersPicker() {
        super(new DashboardUserRowMapper(), searchColumnNames);
    }
   
    @Override
    public String getIdFieldName() {
        return "userId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "userId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightYukonUser>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        Integer dashboardId = extraArgs == null ? null : Integer.parseInt(extraArgs);
        sqlFilters.add(new DashboardUserFilter(dashboardId));
    }
    
    private static class DashboardUserRowMapper extends
            AbstractRowMapperWithBaseQuery<UltraLightYukonUser> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT UserId, UserName, Name");
            sql.append("FROM YukonUser yu");
            sql.append("LEFT JOIN UserGroup yg on yg.UserGroupId = yu.UserGroupId");
            return sql;
        }

        @Override
        public UltraLightYukonUser mapRow(YukonResultSet rs) throws SQLException {
            final String username = rs.getString("UserName");
            final int userId = rs.getInt("UserId");
            final String userGroupName = rs.getString("Name");
            final UltraLightYukonUser user = new UltraLightYukonUser() {
                @Override
                public String getUserName() {
                    return username;
                }

                @Override
                public String getUserGroupName() {
                    return userGroupName;
                }

                @Override
                public int getUserId() {
                    return userId;
                }
            };
            return user;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }
    }
    
    private class DashboardUserFilter implements SqlFilter {
        private int dashboardId;

        public DashboardUserFilter(int dashboardId) {
            this.dashboardId = dashboardId;
        }

        @Override
        public SqlFragmentSource getWhereClauseFragment() {
            List<Integer> userIds = dashboardDao.getAllUsersForDashboard(dashboardId);
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("YU.UserId").in(userIds);
            return sql;
        }
    }
}
