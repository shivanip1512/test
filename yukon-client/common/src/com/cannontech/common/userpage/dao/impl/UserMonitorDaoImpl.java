package com.cannontech.common.userpage.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.userpage.dao.UserMonitorDao;
import com.cannontech.common.userpage.model.UserMonitor;
import com.cannontech.common.userpage.model.UserMonitor.MonitorType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

public class UserMonitorDaoImpl implements UserMonitorDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private static SimpleTableAccessTemplate<UserMonitor> userMonitorTemplate;

    @Override
    public boolean contains(UserMonitor monitor) {
        Integer id = getId(monitor);
        return id != null;
    }

    @Override
    public UserMonitor save(UserMonitor monitor) {

        Integer userMonitorId = getId(monitor);

        if (userMonitorId == null) {
            userMonitorId = userMonitorTemplate.insert(monitor);

            monitor = monitor.updateId(userMonitorId);
        } else {
            monitor = monitor.updateId(userMonitorId);
            userMonitorTemplate.update(monitor);
        }
        return monitor;
    }

    @Override
    public void delete(UserMonitor page) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM UserMonitor");
        sql.appendFragment(getUniquenessCriterion(page));
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<UserMonitor> getMonitorsForUser(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserMonitorId, UserId, MonitorName, MonitorType, MonitorId");
        sql.append("FROM UserMonitor");
        sql.append("WHERE UserId").eq(user.getUserID());

        List<UserMonitor> results = yukonJdbcTemplate.query(sql, userMonitorRowMapper);

        return results;
    }

    private Integer getId(UserMonitor monitor) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserMonitorId, UserId, MonitorName, MonitorType, MonitorId");
        sql.append("FROM UserMonitor");
        sql.appendFragment(getUniquenessCriterion(monitor));
        List<UserMonitor> pages = yukonJdbcTemplate.query(sql, userMonitorRowMapper);

        switch(pages.size()){
        case 0: return null;
        default: return pages.get(0).getId();
        }
    }

   private static SqlFragmentSource getUniquenessCriterion(UserMonitor monitor) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WHERE UserId").eq(monitor.getUserId());
        sql.append("AND MonitorId").eq(monitor.getMonitorId());
        sql.append("AND MonitorType").eq(monitor.getType());
        return sql;
    }

    private static YukonRowMapper<UserMonitor> userMonitorRowMapper = new YukonRowMapper<UserMonitor>() {

        public UserMonitor mapRow(YukonResultSet rs) throws SQLException {
            Integer id = rs.getInt("UserMonitorId");
            Integer userId = rs.getInt("UserId");
            String name = rs.getString("MonitorName");
            MonitorType type = rs.getEnum("MonitorType", MonitorType.class);
            Integer monitorId = rs.getInt("MonitorId");

            UserMonitor monitor = new UserMonitor(userId, name, type, monitorId, id);

            return monitor;
        }
    };

    private static AdvancedFieldMapper<UserMonitor> userMonitorMapper = new AdvancedFieldMapper<UserMonitor>() {

        public void extractValues(SqlParameterChildSink p, UserMonitor monitor) {
            p.addValue("UserId", monitor.getUserId());
            p.addValue("MonitorName", monitor.getName());
            p.addValue("MonitorType", monitor.getType());
            p.addValue("MonitorId", monitor.getMonitorId());
        }
        public Number getPrimaryKey(UserMonitor monitor) {
            return monitor.getId();
        }
        public void setPrimaryKey(UserMonitor monitor, int value) {
            //Immutable object
        }
    };

    @PostConstruct
    public void init() throws Exception {
        userMonitorTemplate = new SimpleTableAccessTemplate<UserMonitor>(yukonJdbcTemplate, nextValueHelper);
        userMonitorTemplate.setTableName("UserMonitor");
        userMonitorTemplate.setPrimaryKeyField("UserMonitorId");
        userMonitorTemplate.setAdvancedFieldMapper(userMonitorMapper);
    }
}