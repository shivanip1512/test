package com.cannontech.watchdog.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.watchdog.dao.WatchdogDao;

public class WatchdogDaoImpl implements WatchdogDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<String> getSubscribedUsersEmailId() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT recipient ");
        sql.append("FROM SmartNotificationSub");
        sql.append("WHERE type ").eq_k(SmartNotificationEventType.YUKON_WATCHDOG);

        return jdbcTemplate.query(sql, new YukonRowMapper<String>() {
            @Override
            public String mapRow(YukonResultSet rs) throws SQLException {
                return rs.getStringSafe("recipient");
            }
        });
    }
}
