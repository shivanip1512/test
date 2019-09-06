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
        Instant now = Instant.now();
        batchInsertDeviceStatuses(eventId, deviceIds, DrMeterControlStatus.CONTROL_NOT_SENT, now);
        batchInsertDeviceStatuses(eventId, deviceIds, DrMeterControlStatus.RESTORE_NOT_SENT, now);
    }
    
    private void batchInsertDeviceStatuses(int eventId, Collection<Integer> deviceIds, DrMeterControlStatus status, 
                                           Instant timestamp) {
        
        List<List<Object>> insertValues = deviceIds.stream()
                                                   .map(deviceId -> getNewEventDeviceInserts(eventId, deviceId, 
                                                                                             timestamp, status))
                                                   .collect(Collectors.toList());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlBatchUpdater updater = sql.batchInsertInto("DrDisconnectDeviceStatus");
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
        sql.append("FROM DrDisconnectDeviceStatus ddds1");
        sql.append("WHERE ddds1.DeviceId IN (SELECT * FROM DevicesInEvent)");
        sql.append("AND ddds1.ControlStatusTime = (");
        sql.append("  SELECT MAX(ddds2.ControlStatusTime)");
        sql.append("  FROM DrDisconnectDeviceStatus ddds2");
        sql.append("  WHERE ddds2.DeviceId = ddds1.DeviceId");
        sql.append(")");
        sql.append("AND ControlStatus").eq_k(currentStatus);
        sql.append("GROUP BY DeviceId");
        List<Integer> deviceIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        // Insert a new status for those devices
        batchInsertDeviceStatuses(eventId, deviceIds, status, Instant.now());
    }

    @Override
    public void updateControlStatus(int eventId, DrMeterControlStatus status, Instant timestamp,
                                    Collection<Integer> deviceIds) {
        
        batchInsertDeviceStatuses(eventId, deviceIds, status, timestamp);
    }

    @Override
    public Optional<Integer> findActiveEventForProgram(int programId) {
        
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX (EventId)");
        sql.append("FROM DrDisconnectEvent");
        sql.append("WHERE ProgramId").eq(programId);
        sql.append("AND StartTime").lt(now);
        sql.append("AND DATEADD (day, 1, EndTime)").gte(now);
        
        // This will error if there are multiple active events
        Integer eventId = jdbcTemplate.queryForObject(sql, TypeRowMapper.INTEGER_NULLABLE);
        
        return Optional.ofNullable(eventId);
    }

    @Override
    public Optional<Integer> findActiveEventForDevice(int deviceId) {
        
        Instant now = Instant.now();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MAX (dde.EventId) ");
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
                                                                             Collection<DrMeterControlStatus> shedStatuses,
                                                                             Collection<DrMeterControlStatus> restoreStatuses) {
        
        // Default to all shed/restore statuses if no filter is selected
        shedStatuses = shedStatuses != null ? shedStatuses : DrMeterControlStatus.getShedStatuses();
        restoreStatuses = restoreStatuses != null ? restoreStatuses : DrMeterControlStatus.getRestoreStatuses();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH DevicesInEvent AS (");
        sql.append(  "SELECT DISTINCT DeviceId");
        sql.append(  "FROM DrDisconnectDeviceStatus");
        sql.append(  "WHERE EventId = (");
        sql.append(    "SELECT MAX(EventId)");
        sql.append(    "FROM DrDisconnectEvent");
        sql.append(    "WHERE ProgramId").eq(programId);
        sql.append(  ")");
        sql.append(")");
        selectEvents(sql, shedStatuses, restoreStatuses);
        
        return jdbcTemplate.query(sql, eventStatusRowMapper);
    }

    @Override
    public List<DrMeterEventStatus> getAllCurrentStatusForEvent(int eventId) {
        var shedStatuses = DrMeterControlStatus.getShedStatuses();
        var restoreStatuses = DrMeterControlStatus.getRestoreStatuses();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH DevicesInEvent AS (");
        sql.append(  "SELECT DISTINCT DeviceId");
        sql.append(  "FROM DrDisconnectDeviceStatus");
        sql.append(  "WHERE EventId").eq(eventId);
        sql.append(")");
        selectEvents(sql, shedStatuses, restoreStatuses);
        
        return jdbcTemplate.query(sql, eventStatusRowMapper);
    }
    
    private SqlStatementBuilder selectEvents(SqlStatementBuilder sql, Collection<DrMeterControlStatus> shedStatuses,
                                          Collection<DrMeterControlStatus> restoreStatuses) {
        sql.append("SELECT shed.EntryId, shed.EventId, shed.DeviceId, shed.ControlStatus, shed.ControlStatusTime,");
        sql.append(       "ib.InventoryId, ypo.PaoName, resto.ControlStatus AS RestoreStatus,");
        sql.append(       "resto.ControlStatusTime AS RestoreStatusTime");
        sql.append("FROM DrDisconnectDeviceStatus shed");
        sql.append("JOIN DrDisconnectDeviceStatus resto ON shed.DeviceId = resto.DeviceId");
        sql.append("JOIN YukonPaObject ypo ON ypo.PaObjectId = shed.DeviceId");
        sql.append("JOIN InventoryBase ib ON ib.DeviceId = shed.DeviceId");
        sql.append("WHERE shed.DeviceId IN (SELECT * FROM DevicesInEvent)");
        sql.append("AND shed.ControlStatus").in(shedStatuses);
        sql.append("AND shed.ControlStatusTime = (");
        sql.append(  "SELECT MAX(shedTimes.ControlStatusTime)");
        sql.append(  "FROM DrDisconnectDeviceStatus shedTimes");
        sql.append(  "WHERE shedTimes.DeviceId = shed.DeviceId");
        sql.append(  "AND shedTimes.ControlStatus").in(shedStatuses);
        sql.append(")");
        sql.append("AND resto.ControlStatus").in(restoreStatuses);
        sql.append("AND resto.ControlStatusTime = (");
        sql.append(  "SELECT MAX(restoTimes.ControlStatusTime)");
        sql.append(  "FROM DrDisconnectDeviceStatus restoTimes");
        sql.append(  "WHERE restoTimes.DeviceId = resto.DeviceId");
        sql.append(  "AND restoTimes.ControlStatus").in(restoreStatuses);
        sql.append(")");
        sql.append("GROUP BY shed.DeviceId, shed.ControlStatus, shed.ControlStatusTime, shed.EventId, shed.EntryId,");
        sql.append(         "ib.InventoryId, ypo.PaoName, resto.ControlStatus, resto.ControlStatusTime");
        return sql;
    }
    
// Something like this could be used to retrieve full status history for event.
//    @Override
//    public List<DrMeterEventStatus> getAllStatusForEvent(int eventId) {
//        
//        SqlStatementBuilder sql = new SqlStatementBuilder();
//        sql.append("SELECT EntryId, EventId, ddds.DeviceId, ib.InventoryId, ControlStatus, ControlStatusTime, ypo.PaoName");
//        sql.append("FROM DrDisconnectDeviceStatus ddds");
//        sql.append("JOIN YukonPaObject ypo ON ypo.PaObjectId = ddds.DeviceId");
//        sql.append("JOIN InventoryBase ib ON ib.DeviceId = ddds.DeviceId");
//        sql.append("WHERE EventId").eq(eventId);
//        sql.append("ORDER BY ControlStatusTime DESC");
//        
//        return jdbcTemplate.query(sql, eventStatusRowMapper);
//    }
    
    private static final YukonRowMapper<DrMeterEventStatus> eventStatusRowMapper = (YukonResultSet rs) -> {
        DrMeterEventStatus status = new DrMeterEventStatus();
        status.setEventId(rs.getInt("EventId"));
        status.setDeviceId(rs.getInt("DeviceId"));
        status.setInventoryId(rs.getInt("InventoryId"));
        status.setMeterDisplayName(rs.getString("PaoName"));
        status.setControlStatus(rs.getEnum("ControlStatus", DrMeterControlStatus.class));
        status.setControlStatusTime(rs.getInstant("ControlStatusTime"));
        status.setRestoreStatus(rs.getEnum("RestoreStatus", DrMeterControlStatus.class));
        status.setRestoreStatusTime(rs.getInstant("RestoreStatusTime"));
        return status;
    };
}
