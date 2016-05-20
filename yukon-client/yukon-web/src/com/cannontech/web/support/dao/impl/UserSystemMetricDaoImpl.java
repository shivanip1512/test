package com.cannontech.web.support.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.support.dao.UserSystemMetricDao;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

public class UserSystemMetricDaoImpl implements UserSystemMetricDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void addFavorite(LiteYukonUser user, SystemHealthMetricIdentifier metric) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("UserSystemMetric");
        sink.addValue("UserId", user.getUserID());
        sink.addValue("SystemHealthMetricId", metric);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void removeFavorite(LiteYukonUser user, SystemHealthMetricIdentifier metric) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserSystemMetric");
        sql.append("WHERE UserId").eq(user.getUserID());
        sql.append("AND SystemHealthMetricId").eq(metric);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public List<SystemHealthMetricIdentifier> getFavorites(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SystemHealthMetricId");
        sql.append("FROM UserSystemMetric");
        sql.append("WHERE UserId").eq(user.getUserID());
        
        List<SystemHealthMetricIdentifier> metrics = jdbcTemplate.query(sql, new YukonRowMapper<SystemHealthMetricIdentifier>() {
            @Override
            public SystemHealthMetricIdentifier mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("SystemHealthMetricId", SystemHealthMetricIdentifier.class);
            }
        });
        
        return metrics;
    }
}
