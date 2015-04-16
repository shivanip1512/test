package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.dao.impl.DeviceDataMonitorDaoImpl;
import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Phase;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class RegulatorEventsDaoImpl implements RegulatorEventsDao {

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorDaoImpl.class);

    private static final YukonRowMapper<RegulatorEvent> rowMapper = new YukonRowMapper<RegulatorEvent>() {

        @Override
        public RegulatorEvent mapRow(YukonResultSet rs) throws SQLException {

            int id = rs.getInt("RegulatorEventId");
            int regulatorId = rs.getInt("RegulatorId");
            Instant timestamp = rs.getInstant("TimeStamp");
            EventType type = rs.getEnum("EventType", EventType.class);

            Phase phase;
            try {
                phase = rs.getEnum("Phase", Phase.class);
            } catch (IllegalArgumentException e) {
                log.warn("Illegal Phase in the RegulatorEvents Table. Using Phase ALL");
                phase = Phase.ALL;
            }
            if (phase == null) phase = Phase.ALL;

            String userName = rs.getString("UserName");

            return RegulatorEvent.of(id, regulatorId, timestamp, type, phase, userName);
        }
    };

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public List<RegulatorEvent> getForIdSinceTimestamp(int regulatorId, Instant start) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RegulatorEventId, RegulatorId, Timestamp, EventType, Phase, UserName");
        sql.append("FROM RegulatorEvents");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        sql.append("AND TimeStamp").gte(start);
        sql.append("ORDER BY TimeStamp DESC");

        List<RegulatorEvent> events = yukonJdbcTemplate.query(sql, rowMapper);

        return events;
    }

}
