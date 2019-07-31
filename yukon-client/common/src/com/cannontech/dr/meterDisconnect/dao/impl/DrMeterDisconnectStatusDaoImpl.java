package com.cannontech.dr.meterDisconnect.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStatementBuilder.SqlBatchUpdater;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.meterDisconnect.DrMeterControlStatus;
import com.cannontech.dr.meterDisconnect.DrMeterEventStatus;
import com.cannontech.dr.meterDisconnect.dao.DrMeterDisconnectStatusDao;

public class DrMeterDisconnectStatusDaoImpl implements DrMeterDisconnectStatusDao {
    @Autowired NextValueHelper nextValueHelper;
    @Autowired YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int createEvent(Instant startTime, Instant expectedEndTime, int programPaoId) {
        int eventId = nextValueHelper.getNextValue("DrDisconnectEvent");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO DrDisconnectEvent").values(eventId, programPaoId, startTime, expectedEndTime);
        jdbcTemplate.update(sql);
        return eventId;
    }

    @Override
    public void addDevicesToEvent(int eventId, Collection<Integer> deviceIds) {
        batchInsertDeviceStatuses(eventId, deviceIds, DrMeterControlStatus.NOT_SENT, Optional.empty());
    }
    
    private void batchInsertDeviceStatuses(int eventId, Collection<Integer> deviceIds, DrMeterControlStatus status, 
                                           Optional<Instant> timestamp) {
        
        Instant statusTime = timestamp.orElse(Instant.now());
        List<List<Object>> insertValues = deviceIds.stream()
                                                   .map(deviceId -> getNewEventDeviceInserts(eventId, deviceId, 
                                                                                             statusTime, status))
                                                   .collect(Collectors.toList());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlBatchUpdater updater = sql.batchInsertInto("DynamicDrDisconnectDeviceStatus");
        updater.columns("EntryId", "EventId", "DeviceId", "ControlStatus", "ControlStatusTime");
        updater.values(insertValues);
        jdbcTemplate.yukonBatchUpdate(sql);
    }
    
    private List<Object> getNewEventDeviceInserts(int eventId, int deviceId, Instant now, DrMeterControlStatus status) {
        int entryId = nextValueHelper.getNextValue("DrDisconnectDeviceStatus");
        return List.of(entryId, eventId, deviceId, status, now);
    }

    @Override
    @Transactional
    public void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp, 
                                    DrMeterControlStatus currentStatus) {
        
        // Find all the devices whose most recent status in the event is the specified "current status"
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH DevicesInEvent AS (");
        sql.append("  SELECT DISTINCT DeviceId");
        sql.append("  FROM DrDisconnectDeviceStatus");
        sql.append("  WHERE EventId").eq(eventId);
        sql.append(")");
        sql.append("SELECT DeviceId");
        sql.append("FROM DrDisconnectDeviceStatus S1");
        sql.append("WHERE S1.DeviceId IN (SELECT * FROM DevicesInEvent)");
        sql.append("AND S1.ControlStatusTime = (");
        sql.append("  SELECT MAX(S2.ControlStatusTime)");
        sql.append("  FROM DrDisconnectDeviceStatus S2");
        sql.append("  WHERE S2.DeviceId = S1.DeviceId");
        sql.append(")");
        sql.append("AND ControlStatus").eq_k(currentStatus);
        sql.append("GROUP BY DeviceId");
        List<Integer> deviceIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        // Insert a new status for those devices
        batchInsertDeviceStatuses(eventId, deviceIds, status, Optional.empty());
    }

    @Override
    public void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp,
                                    Collection<Integer> deviceIds) {
        
        batchInsertDeviceStatuses(eventId, deviceIds, status, Optional.of(timestamp));
    }

    @Override
    public Optional<Integer> findActiveEventForProgram(int programId) {
        
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EventId");
        sql.append("FROM DrDisconnectEvent");
        sql.append("WHERE ProgramId").eq(programId);
        sql.append("AND StartTime").lt(now);
        sql.append("AND EndTime").gt(now);
        
        // This will error if there are multiple active events
        Integer eventId = jdbcTemplate.queryForObject(sql, TypeRowMapper.INTEGER_NULLABLE);
        
        return Optional.ofNullable(eventId);
    }

    @Override
    public Optional<Integer> findActiveEventForDevice(int deviceId) {
        
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT dde.EventId ");
        sql.append("FROM DrDisconnectEvent dde");
        sql.append("JOIN DrDisconnectDeviceStatus ddds ON ddds.EventId = dde.EventId");
        sql.append("WHERE ddds.DeviceId").eq(deviceId);
        sql.append("AND dde.StartTime").lt(now);
        sql.append("AND dde.EndTime").gt(now);
        
        // This will error if there are multiple active events for device
        Integer eventId = jdbcTemplate.queryForObject(sql, TypeRowMapper.INTEGER_NULLABLE);
        
        return Optional.ofNullable(eventId);
    }

    @Override
    public List<DrMeterEventStatus> getAllCurrentStatusForLatestProgramEvent(int programId,
                                                                             Collection<DrMeterControlStatus> controlStatuses) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH DevicesInEvent AS (");
        sql.append("  SELECT DISTINCT DeviceId");
        sql.append("  FROM DrDisconnectDeviceStatus");
        sql.append("  WHERE EventId = (");
        sql.append("    SELECT MAX(EventId)");
        sql.append("    FROM DrDisconnectEvent");
        sql.append("    WHERE ProgramId").eq(programId);
        sql.append("  )");
        sql.append(")");
        sql.append("SELECT EntryId, EventId, S1.DeviceId, ib.InventoryId, ControlStatus, ControlStatusTime, ypo.PaoName");
        sql.append("FROM DrDisconnectDeviceStatus S1");
        sql.append("JOIN YukonPaObject ypo ON ypo.PaObjectId = S1.DeviceId");
        sql.append("JOIN InventoryBase ib ON ib.DeviceId = S1.DeviceId");
        sql.append("WHERE S1.DeviceId IN (SELECT * FROM DevicesInEvent)");
        sql.append("AND S1.ControlStatusTime = (");
        sql.append("  SELECT MAX(S2.ControlStatusTime)");
        sql.append("  FROM DrDisconnectDeviceStatus S2");
        sql.append("  WHERE S2.DeviceId = S1.DeviceId");
        sql.append(")");
        sql.append("GROUP BY S1.DeviceId, ib.InventoryId, ypo.PaoName, ControlStatus, ControlStatusTime, EventId, EntryId");
        
        return jdbcTemplate.query(sql, eventStatusRowMapper);
    }
    
    private static final YukonRowMapper<DrMeterEventStatus> eventStatusRowMapper = (YukonResultSet rs) -> {
        DrMeterEventStatus status = new DrMeterEventStatus();
        status.setEventId(rs.getInt("EventId"));
        status.setDeviceId(rs.getInt("DeviceId"));
        status.setInventoryId(rs.getInt("InventoryId"));
        status.setMeterDisplayName(rs.getString("PaoName"));
        status.setControlStatus(rs.getEnum("ControlStatus", DrMeterControlStatus.class));
        status.setControlStatusTime(rs.getInstant("ControlStatusTime"));
        return status;
    };

    @Override
    public List<DrMeterEventStatus> getAllStatusForEvent(int eventId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EntryId, EventId, S1.DeviceId, ib.InventoryId, ControlStatus, ControlStatusTime, ypo.PaoName");
        sql.append("FROM DrDisconnectDeviceStatus S1");
        sql.append("JOIN YukonPaObject ypo ON ypo.PaObjectId = S1.DeviceId");
        sql.append("JOIN InventoryBase ib ON ib.DeviceId = S1.DeviceId");
        sql.append("WHERE EventId").eq(eventId);
        sql.append("ORDER BY ControlStatusTime DESC");
        
        return jdbcTemplate.query(sql, eventStatusRowMapper);
    }

}
