package com.cannontech.web.api.notificationGroup.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.web.api.notificationGroup.dao.NotificationGroupDao;
import com.cannontech.web.notificationGroup.NotificationGroup;

public class NotificationGroupDaoImpl implements NotificationGroupDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private final YukonRowMapper<NotificationGroup> rowMapper = createRowMapper();
    public static final String TABLE_NAME = "NotificationGroup";

    public List<NotificationGroup> getAllNotificationGroups(String sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * FROM");
        sql.append(TABLE_NAME);

        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy);
            if (direction != null) {
                sql.append(direction);
            }
        }

        return jdbcTemplate.query(sql, rowMapper);
    }

    private YukonRowMapper<NotificationGroup> createRowMapper() {
        final YukonRowMapper<NotificationGroup> mapper = new YukonRowMapper<NotificationGroup>() {
            public NotificationGroup mapRow(YukonResultSet rs) throws SQLException {
                final NotificationGroup notifGrp = new NotificationGroup();
                notifGrp.setId(rs.getInt("NotificationGroupID"));
                notifGrp.setName(rs.getString("GroupName"));
                notifGrp.setEnabled(rs.getString("DisableFlag").equals("N") ? true : false);
                return notifGrp;
            }
        };
        return mapper;
    }
}
