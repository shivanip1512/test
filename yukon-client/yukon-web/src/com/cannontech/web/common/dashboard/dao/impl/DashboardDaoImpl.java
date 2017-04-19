package com.cannontech.web.common.dashboard.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.common.dashboard.dao.DashboardDao;
import com.cannontech.web.common.dashboard.model.DashboardBase;
import com.cannontech.web.common.dashboard.model.Widget;

public class DashboardDaoImpl implements DashboardDao {
    private static final int col1WidgetBase = 100; // This implies no more than 100 widgets per column.
    private static final int col2WidgetBase = 200;
    
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void deleteDashboard(int dashboardId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Dashboard");
        sql.append("WHERE DashboardId").eq(dashboardId);
        jdbcTemplate.update(sql);
    }
    
    @Override
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
    
    @Override
    public int create(DashboardBase dashboard) {
        int dashboardId = dashboard.getDashboardId();
        if (dashboardId == 0) {
            dashboardId = nextValueHelper.getNextValue("Dashboard");
        }
        SqlStatementBuilder dashboardSql = new SqlStatementBuilder();
        SqlParameterSink dashboardSink = dashboardSql.insertInto("Dashboard");
        dashboardSink.addValue("DashboardId", dashboardId);
        dashboardSink.addValue("Name", dashboard.getName());
        dashboardSink.addValue("Description", dashboard.getDescription());
        dashboardSink.addValue("OwnerId", dashboard.getOwner().getUserID());
        dashboardSink.addValue("Visibility", dashboard.getVisibility());
        jdbcTemplate.update(dashboardSql);
        
        return dashboardId;
    }
    
    @Override
    @Transactional
    public void insertWidgets(int dashboardId, List<Widget> widgets, int column) {
        if (widgets == null) {
            return;
        }
        
        int columnBase = column == 1 ? col1WidgetBase : col2WidgetBase;
        for (int i = 0; i < widgets.size(); i++) {
            // First, insert the widget...
            Widget widget = widgets.get(i);
            int widgetId = nextValueHelper.getNextValue("Widget");
            SqlStatementBuilder widgetSql = new SqlStatementBuilder();
            SqlParameterSink widgetSink = widgetSql.insertInto("Widget");
            widgetSink.addValue("WidgetId", widgetId);
            widgetSink.addValue("WidgetType", widget.getType());
            widgetSink.addValue("DashboardId", dashboardId);
            widgetSink.addValue("Ordering", columnBase + i);
            jdbcTemplate.update(widgetSql);
            
            // ...then insert the widget settings.
            widget.getParameters().entrySet().stream().forEach(parameter -> {
                SqlStatementBuilder widgetParamSql = new SqlStatementBuilder();
                SqlParameterSink widgetParamSink = widgetParamSql.insertInto("WidgetSettings");
                int settingId = nextValueHelper.getNextValue("WidgetSettings");
                widgetParamSink.addValue("SettingId", settingId);
                widgetParamSink.addValue("WidgetId", widgetId);
                widgetParamSink.addValue("Name", parameter.getKey());
                widgetParamSink.addValue("Value", parameter.getValue());
                jdbcTemplate.update(widgetParamSql);
            });
        }
    }
    
}
