package com.cannontech.web.common.dashboard.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.web.common.dashboard.dao.DashboardDao;

public class DashboardDaoImpl implements DashboardDao {
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public void deleteDashboard(int dashboardId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Dashboard");
        sql.append("WHERE DashboardId").eq(dashboardId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    @Transactional
    public void deleteUserDashboard(int userId, int dashboardId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserDashboard");
        sql.append("WHERE UserId").eq(userId);
        sql.append("AND DashboardId").eq(dashboardId);
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<Integer> getAllUsersForDashboard(int dashboardId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserId");
        sql.append("FROM UserDashboard");
        sql.append("WHERE DashboardId").eq(dashboardId);
        
        List<Integer> userIdList = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        return userIdList;
    }
}
