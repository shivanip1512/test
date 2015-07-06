package com.cannontech.capcontrol.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.deviceDataMonitor.dao.impl.DeviceDataMonitorDaoImpl;
import com.cannontech.capcontrol.dao.RegulatorEventsDao;
import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.Phase;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.TimeRange;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.google.common.collect.ImmutableList;

public class RegulatorEventsDaoImpl implements RegulatorEventsDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
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
            
            Integer tapPosition = rs.getNullableInt("TapPosition");
            Double setPointValue = rs.getNullableDouble("SetPointValue");
            String userName = rs.getString("UserName");
            
            return RegulatorEvent.of(id, regulatorId, timestamp, type, phase, tapPosition, setPointValue, userName);
        }
    };
    
    @Override
    public List<RegulatorEvent> getForIdSinceTimestamp(int regulatorId, Instant start) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RegulatorEventId, RegulatorId, Timestamp, EventType, Phase, SetPointValue, TapPosition, UserName");
        sql.append("FROM RegulatorEvents");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        sql.append("AND TimeStamp").gte(start);
        sql.append("ORDER BY TimeStamp DESC");
        
        List<RegulatorEvent> events = yukonJdbcTemplate.query(sql, rowMapper);
        
        return events;
    }
    
    @Override
    public List<RegulatorEvent> getForId(int regulatorId, TimeRange range) {
        
        Duration hours = Duration.standardHours(range.getHours());
        Instant since = Instant.now().minus(hours);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RegulatorEventId, RegulatorId, Timestamp, EventType, Phase, SetPointValue, TapPosition, UserName");
        sql.append("FROM RegulatorEvents");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        sql.append("AND TimeStamp").gte(since);
        sql.append("ORDER BY TimeStamp DESC");
        
        List<RegulatorEvent> events = yukonJdbcTemplate.query(sql, rowMapper);
        
        return events;
    }
    
    @Override
    public List<RegulatorEvent> getForIds(Iterable<Integer> regulatorIds, TimeRange range) {
        
        Duration hours = Duration.standardHours(range.getHours());
        Instant since = Instant.now().minus(hours);
        
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT RegulatorEventId, RegulatorId, Timestamp, EventType, Phase, SetPointValue, TapPosition, UserName");
                sql.append("FROM RegulatorEvents");
                sql.append("WHERE RegulatorId").in(regulatorIds);
                sql.append("AND TimeStamp").gte(since);
                sql.append("ORDER BY TimeStamp DESC");
                return sql;
            }
        };
        
        List<RegulatorEvent> events = new ArrayList<>();
        
        ChunkingSqlTemplate chunkingTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
        chunkingTemplate.query(sqlGenerator, regulatorIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                RegulatorEvent location = rowMapper.mapRow(rs);
                events.add(location);
            }
        });
        
        Collections.sort(events, new Comparator<RegulatorEvent>() {
            @Override
            public int compare(RegulatorEvent lhs, RegulatorEvent rhs) {
                return -lhs.getTimestamp().compareTo(rhs.getTimestamp());
            }});
        
        return events;
    }

    @Override
    public RegulatorEvent getLatestSetPointForId(int regulatorId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM RegulatorEvents");
        sql.append("WHERE RegulatorId").eq(regulatorId);
        sql.append("AND TimeStamp IN");
        sql.append("    (SELECT MAX(TimeStamp)");
        sql.append("     FROM RegulatorEvents ");
        sql.append("     WHERE EventType").in(ImmutableList.of(EventType.DECREASE_SETPOINT, EventType.INCREASE_SETPOINT));
        sql.append("     AND SetPointValue is not NULL)");

        RegulatorEvent event = null;
        try{
            event = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        }catch(EmptyResultDataAccessException e){
        }
        
        return event;
    }
    
}