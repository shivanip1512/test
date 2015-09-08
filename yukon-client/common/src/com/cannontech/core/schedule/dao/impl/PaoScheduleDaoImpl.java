package com.cannontech.core.schedule.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.core.schedule.model.PaoSchedule;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class PaoScheduleDaoImpl implements PaoScheduleDao {

    private static final YukonRowMapper<PaoScheduleAssignment> assignmentRowMapper;
    
    private static SimpleTableAccessTemplate<PaoSchedule> scheduleTemplate; 

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    static {
        assignmentRowMapper = new YukonRowMapper<PaoScheduleAssignment>() {
            @Override
            public PaoScheduleAssignment mapRow(YukonResultSet rs) throws SQLException {

                PaoScheduleAssignment assignment = new PaoScheduleAssignment();

                assignment.setEventId(rs.getInt("EventID"));
                assignment.setScheduleId(rs.getInt("ScheduleID"));
                assignment.setPaoId(rs.getInt("PaoID"));
                assignment.setCommandName(rs.getString("Command"));
                assignment.setScheduleName(rs.getString("ScheduleName"));
                assignment.setDeviceName(rs.getString("PaoName"));
                assignment.setLastRunTime(rs.getInstant("LastRunTime"));
                assignment.setNextRunTime(rs.getInstant("NextRunTime"));
                assignment.setDisableOvUv(rs.getString("disableOvUv"));

                return assignment;
            }
        };

    }
    
    private static AdvancedFieldMapper<PaoSchedule> scheduleMapper = new AdvancedFieldMapper<PaoSchedule>() {
        @Override
        public void extractValues(SqlParameterChildSink sink, PaoSchedule schedule) {
            sink.addValue("NextRunTime", schedule.getNextRunTime());
            sink.addValue("LastRunTime", schedule.getLastRunTime());
            sink.addValue("IntervalRate", schedule.getRepeatSeconds());
            sink.addValue("ScheduleName", schedule.getName());
            sink.addValue("Disabled", schedule.isDisabled());
        }

        @Override
        public Number getPrimaryKey(PaoSchedule schedule) {
            return schedule.getId();
        }

        @Override
        public void setPrimaryKey(PaoSchedule schedule, int value) {
            schedule.setId(value);
        }
    };
    
    YukonRowMapper<PaoSchedule> scheduleRowMapper = new YukonRowMapper<PaoSchedule>() {
        @Override
        public PaoSchedule mapRow(YukonResultSet rs) throws SQLException {
            PaoSchedule schedule = new PaoSchedule();
            schedule.setId(rs.getInt("ScheduleID"));
            schedule.setNextRunTime(rs.getInstant("NextRunTime"));
            schedule.setLastRunTime(rs.getInstant("LastRunTime"));
            schedule.setRepeatSeconds(rs.getInt("IntervalRate"));
            schedule.setName(rs.getString("ScheduleName"));
            schedule.setDisabled(rs.getBoolean("Disabled"));
            return schedule;
        }
    };

    @Override
    public void save(PaoSchedule schedule) {
        DbChangeType type = DbChangeType.UPDATE;
        if (schedule.getId() == null) {
            type = DbChangeType.ADD;
        }
        int scheduleId = scheduleTemplate.save(schedule);

        DBChangeMsg dbChangeMsg = makeScheduleDbChangeMsg(scheduleId, type);
        dbChangeManager.processDbChange(dbChangeMsg);

    }
    
    
    
    @Override
    public PaoSchedule getForId(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleID, NextRunTime, LastRunTime, IntervalRate, ScheduleName, Disabled");
        sql.append("FROM PAOSchedule");
        sql.append("WHERE ScheduleID").eq(id);

        return jdbcTemplate.queryForObject(sql, scheduleRowMapper);
    }

    @Override
    public PaoSchedule findForName(String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleID, NextRunTime, LastRunTime, IntervalRate, ScheduleName, Disabled");
        sql.append("FROM PAOSchedule");
        sql.append("WHERE ScheduleName").eq(name);

        try {
            return jdbcTemplate.queryForObject(sql, scheduleRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<PaoSchedule> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleID, NextRunTime, LastRunTime, IntervalRate, ScheduleName, Disabled");
        sql.append("FROM PAOSchedule");
        sql.append("ORDER BY ScheduleName");

        List<PaoSchedule> scheduleNames = jdbcTemplate.query(sql, scheduleRowMapper);
        return scheduleNames;
    }

    @Override
    public int add(String name, boolean disabled) {

        int scheduleId = nextValueHelper.getNextValue("PAOSchedule");
        Date nextRunTime = new Date(System.currentTimeMillis() - 14400000);
        Date lastRunTime = CtiUtilities.get1990GregCalendar().getTime();
        Integer intervalRate = new Integer(CtiUtilities.NONE_ZERO_ID);

        SqlStatementBuilder sql = new SqlStatementBuilder();

        SqlParameterSink sink = sql.insertInto("PaoSchedule");
        sink.addValue("ScheduleId", scheduleId);
        sink.addValue("NextRunTime", nextRunTime);
        sink.addValue("LastRunTime", lastRunTime);
        sink.addValue("IntervalRate", intervalRate);
        sink.addValue("ScheduleName", name);
        sink.addValue("Disabled", disabled ? YNBoolean.YES : YNBoolean.NO);

        jdbcTemplate.update(sql);

        return scheduleId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaoScheduleAssignment> getAllScheduleAssignments() {

        final String selectAllAssignments =
            "SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime, s.LastRunTime, " +
                    "sa.PaoID, po.PAOName, sa.Command, sa.disableOvUv " +
                    "FROM PAOScheduleAssignment sa, PAOSchedule s, YukonPAObject po " +
                    "WHERE s.ScheduleID = sa.ScheduleID AND sa.PaoID = po.PAObjectID ";

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectAllAssignments);

        List<PaoScheduleAssignment> assignmentList = jdbcTemplate.query(sql, assignmentRowMapper);
        return assignmentList;
    }

    @Override
    @Transactional(readOnly = true)
    public PaoScheduleAssignment getScheduleAssignmentByEventId(Integer eventId) {
        final String selectAssignmentByEventId =
            "SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime, " +
                    "s.LastRunTime, sa.PaoID, po.PAOName, sa.Command, sa.disableOvUv " +
                    "FROM PAOScheduleAssignment sa, PAOSchedule s, YukonPAObject po " +
                    "WHERE s.ScheduleID = sa.ScheduleID AND sa.PaoID = po.PAObjectID ";
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectAssignmentByEventId);
        sql.append("AND sa.EventID").eq(eventId);

        try {
            PaoScheduleAssignment assignment = jdbcTemplate.queryForObject(sql, assignmentRowMapper);
            return assignment;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("PaoSchedule with id of " + eventId + " was not found.");
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<PaoScheduleAssignment> getScheduleAssignmentByScheduleId(Integer scheduleId) {
        final String selectAssignmentByEventId =
            "SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime, " +
                    "s.LastRunTime, sa.PaoID, po.PAOName, sa.Command, sa.disableOvUv " +
                    "FROM PAOScheduleAssignment sa, PAOSchedule s, YukonPAObject po " +
                    "WHERE s.ScheduleID = sa.ScheduleID AND sa.PaoID = po.PAObjectID ";
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectAssignmentByEventId);
        sql.append("AND sa.ScheduleId").eq(scheduleId);

        
        List<PaoScheduleAssignment> assignment = jdbcTemplate.query(sql, assignmentRowMapper);
        return assignment;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean updateAssignment(PaoScheduleAssignment assignment) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlParameterSink sink = sql.update("PAOScheduleAssignment");
        sink.addValue("ScheduleId", assignment.getScheduleId());
        sink.addValue("PaoId", assignment.getPaoId());
        sink.addValue("Command", assignment.getCommandName());
        sink.addValue("DisableOVUV", assignment.getDisableOvUv());
        
        sql.append("WHERE EventId").eq(assignment.getEventId());
        
        int rowsAffected = jdbcTemplate.update(sql);
        
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean assignCommand(PaoScheduleAssignment pao) {
        int nextId = nextValueHelper.getNextValue("PAOScheduleAssignment");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("PaoScheduleAssignment");
        sink.addValue("EventId", nextId);
        sink.addValue("ScheduleId", pao.getScheduleId());
        sink.addValue("PaoId", pao.getPaoId());
        sink.addValue("Command", pao.getCommandName());
        sink.addValue("DisableOVUV", pao.getDisableOvUv());
        
        int rowsAffected = jdbcTemplate.update(sql);
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean assignCommand(List<PaoScheduleAssignment> list) {
        boolean success = true;
        for (PaoScheduleAssignment assignment : list) {
            if (!assignCommand(assignment)) {
                success = false;
            }
        }

        return success;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean unassignCommandByEventId(int eventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PAOScheduleAssignment");
        sql.append("WHERE EventID").eq(eventId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deletePaoScheduleAssignmentsByScheduleId(int scheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PAOScheduleAssignment");
        sql.append("WHERE ScheduleId").eq(scheduleId);
        
        int rowsAffected = jdbcTemplate.update(sql);
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean delete(int scheduleId) {
        deletePaoScheduleAssignmentsByScheduleId(scheduleId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PAOSchedule");
        sql.append("WHERE ScheduleId").eq(scheduleId);
        
        int rowsAffected = jdbcTemplate.update(sql);

        DBChangeMsg changeMessage = makeScheduleDbChangeMsg(scheduleId, DbChangeType.DELETE);
        dbChangeManager.processDbChange(changeMessage);

        return rowsAffected == 1;
    }
    
    @Override
    public boolean doesNameExist(String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        sql.append("SELECT COUNT(*) FROM PaoSchedule");
        sql.append("WHERE ScheduleName").eq(name);
        
        int numWithName = jdbcTemplate.queryForInt(sql);

        return numWithName != 0;
    }
    
    private DBChangeMsg makeScheduleDbChangeMsg(int scheduleId, DbChangeType type) {
        return new DBChangeMsg(
            scheduleId,
            DBChangeMsg.CHANGE_PAO_SCHEDULE_DB,
            DBChangeMsg.CAT_PAO_SCHEDULE,
            DBChangeMsg.CAT_PAO_SCHEDULE,
            type);
    }
    
    @PostConstruct
    public void init() throws Exception {
        scheduleTemplate = new SimpleTableAccessTemplate<PaoSchedule>(jdbcTemplate, nextValueHelper);
        scheduleTemplate.setTableName("PAOSchedule");
        scheduleTemplate.setPrimaryKeyField("ScheduleId");
        scheduleTemplate.setAdvancedFieldMapper(scheduleMapper);
    }
}