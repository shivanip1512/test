package com.cannontech.web.common.dashboard.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.common.dashboard.dao.DashboardDao;

public class DashboardDaoImpl implements DashboardDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public void deleteDashboard(int dashboardId) {
        
        SqlStatementBuilder userDashboardSql = new SqlStatementBuilder();
        userDashboardSql.append("DELETE FROM Dashboard");
        userDashboardSql.append("WHERE DashboardId").eq(dashboardId);
        jdbcTemplate.update(userDashboardSql);
    }
    
    @Override
    @Transactional
    public void deleteUserDashboard(int userId, int dashboardId) {
        
        SqlStatementBuilder userDashboardSql = new SqlStatementBuilder();
        userDashboardSql.append("DELETE FROM UserDashboard");
        userDashboardSql.append("WHERE UserId").eq(userId);
        userDashboardSql.append("AND DashboardId").eq(dashboardId);
        jdbcTemplate.update(userDashboardSql);
    }
    
    @Override
    @Transactional
    public List<Integer> getAllUsersForDashboard(int dashboardId) {
        SqlStatementBuilder usersDashboardSql = new SqlStatementBuilder();
        usersDashboardSql.append("Select UD.UserId");
        usersDashboardSql.append("FROM UserDashboard");
        usersDashboardSql.append("WHERE DashboardId").eq(dashboardId);
        
        List<Integer> userIdList = jdbcTemplate.query(usersDashboardSql, new UserIdRowMapper());
        return userIdList;
    }
    
    private static class UserIdRowMapper implements YukonRowMapper<Integer> {
        @Override
        public Integer mapRow(YukonResultSet rs) throws SQLException {
            int userId = rs.getInt("UserId");
            return userId;
        }
    }
}
