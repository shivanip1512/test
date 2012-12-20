package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.database.incrementer.NextValueHelper;

public class PaoScheduleDaoImpl implements PaoScheduleDao {

    private static final YukonRowMapper<PaoScheduleAssignment> assignmentRowMapper;
    private static final YukonRowMapper<PAOSchedule> paoScheduleRowMapper;

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper = null;

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

        paoScheduleRowMapper = new YukonRowMapper<PAOSchedule>() {
            @Override
            public PAOSchedule mapRow(YukonResultSet rs) throws SQLException
            {
                PAOSchedule sched = new PAOSchedule();
                sched.setScheduleID(rs.getInt("ScheduleID"));
                sched.setNextRunTime(rs.getInstant("NextRunTime").toDate());
                sched.setLastRunTime(rs.getInstant("LastRunTime").toDate());
                sched.setIntervalRate(rs.getInt("IntervalRate"));
                sched.setScheduleName(rs.getString("ScheduleName"));
                sched.setDisabled(rs.getString("Disabled").equalsIgnoreCase("Y") ? true : false);
                return sched;
            }
        };
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

        yukonJdbcTemplate.update(sql);

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

        List<PaoScheduleAssignment> assignmentList = yukonJdbcTemplate.query(sql, assignmentRowMapper);
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
            PaoScheduleAssignment assignment = yukonJdbcTemplate.queryForObject(sql, assignmentRowMapper);
            return assignment;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("PaoSchedule with id of " + eventId + " was not found.");
        }

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
        
        int rowsAffected = yukonJdbcTemplate.update(sql);
        
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PAOSchedule> getAllPaoScheduleNames() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleId, NextRunTime, LastRunTime,");
        sql.append(       "IntervalRate, ScheduleName, Disabled");
        sql.append("FROM PaoSchedule");
        
        List<PAOSchedule> scheduleNames = yukonJdbcTemplate.query(sql, paoScheduleRowMapper);
        return scheduleNames;
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
        
        int rowsAffected = yukonJdbcTemplate.update(sql);
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
        
        int rowsAffected = yukonJdbcTemplate.update(sql);
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deletePaoScheduleAssignmentsByScheduleId(int scheduleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PAOScheduleAssignment");
        sql.append("WHERE ScheduleId").eq(scheduleId);
        
        int rowsAffected = yukonJdbcTemplate.update(sql);
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean delete(int scheduleId) {
        deletePaoScheduleAssignmentsByScheduleId(scheduleId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PAOSchedule");
        sql.append("WHERE ScheduleId").eq(scheduleId);
        
        int rowsAffected = yukonJdbcTemplate.update(sql);
        return rowsAffected == 1;
    }
}