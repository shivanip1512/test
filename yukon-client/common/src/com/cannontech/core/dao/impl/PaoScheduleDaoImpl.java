package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.database.incrementer.NextValueHelper;

public class PaoScheduleDaoImpl implements PaoScheduleDao {

    private static final String assignCommandToSchedule;
    private static final String updateAssignment;
    private static final String removeCommandFromScheduleByEventId;
    private static final String selectAllAssignments;
    private static final String selectAllPaoSchedule;
    private static final String selectAssignmentByEventId;
    private static final String deletePaoSchedule;
    private static final String deletePaoScheduleAssignmentByScheduleId;
    private static final String insertSql;

    private static final ParameterizedRowMapper<PaoScheduleAssignment> assignmentRowMapper;
    private static final ParameterizedRowMapper<PAOSchedule> paoScheduleRowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private NextValueHelper nextValueHelper = null;

    static {

        insertSql = "INSERT INTO PAOSchedule values(?, ?, ?, ?, ?, ?)";

        updateAssignment =
            "UPDATE PAOScheduleAssignment SET ScheduleId = ?, PaoId = ?, Command = ?, disableOvUv = ? where EventId = ?";

        selectAllPaoSchedule =
            "SELECT ScheduleID, NextRunTime, LastRunTime, IntervalRate, ScheduleName, Disabled From PaoSchedule";

        selectAllAssignments =
            "SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime, s.LastRunTime, sa.PaoID, po.PAOName, sa.Command, sa.disableOvUv "
                    +
                    "FROM PAOScheduleAssignment sa, PAOSchedule s, YukonPAObject po " +
                    "WHERE s.ScheduleID = sa.ScheduleID AND sa.PaoID = po.PAObjectID ";

        selectAssignmentByEventId =
            "SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime, s.LastRunTime, sa.PaoID, po.PAOName, sa.Command, sa.disableOvUv "
                    +
                    "FROM PAOScheduleAssignment sa, PAOSchedule s, YukonPAObject po "
                    +
                    "WHERE s.ScheduleID = sa.ScheduleID AND sa.PaoID = po.PAObjectID AND sa.EventID = ?";

        assignCommandToSchedule =
            "INSERT INTO PAOScheduleAssignment (EventID, ScheduleID, PaoID, Command, disableOvUv) VALUES (?,?,?,?,?)";

        removeCommandFromScheduleByEventId = "DELETE FROM PAOScheduleAssignment WHERE EventID = ?";

        deletePaoSchedule = "DELETE FROM PAOSchedule WHERE ScheduleId = ?";

        deletePaoScheduleAssignmentByScheduleId =
            "DELETE FROM PAOScheduleAssignment WHERE ScheduleId = ?";

        assignmentRowMapper = new ParameterizedRowMapper<PaoScheduleAssignment>() {
            public PaoScheduleAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {

                PaoScheduleAssignment assignment = new PaoScheduleAssignment();

                assignment.setEventId(rs.getInt("EventID"));
                assignment.setScheduleId(rs.getInt("ScheduleID"));
                assignment.setPaoId(rs.getInt("PaoID"));
                assignment.setCommandName(rs.getString("Command"));
                assignment.setScheduleName(rs.getString("ScheduleName"));
                assignment.setDeviceName(rs.getString("PaoName"));
                assignment.setLastRunTime(rs.getTimestamp("LastRunTime"));
                assignment.setNextRunTime(rs.getTimestamp("NextRunTime"));
                assignment.setDisableOvUv(rs.getString("disableOvUv"));

                return assignment;
            }
        };

        paoScheduleRowMapper = new ParameterizedRowMapper<PAOSchedule>() {
            public PAOSchedule mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                PAOSchedule sched = new PAOSchedule();
                sched.setScheduleID(rs.getInt("ScheduleID"));
                sched.setNextRunTime(rs.getTimestamp("NextRunTime"));
                sched.setLastRunTime(rs.getTimestamp("LastRunTime"));
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
        simpleJdbcTemplate.update(insertSql,
                                  scheduleId,
                                  nextRunTime,
                                  lastRunTime,
                                  intervalRate,
                                  name,
                                  disabled ? "Y" : "N");
        return scheduleId;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaoScheduleAssignment> getAllScheduleAssignments() {
        List<PaoScheduleAssignment> assignmentList =
            simpleJdbcTemplate.query(selectAllAssignments, assignmentRowMapper);

        return assignmentList;
    }

    @Override
    @Transactional(readOnly = true)
    public PaoScheduleAssignment getScheduleAssignmentByEventId(Integer eventId) {
        PaoScheduleAssignment assignment =
            simpleJdbcTemplate.queryForObject(selectAssignmentByEventId,
                                              assignmentRowMapper,
                                              eventId);
        return assignment;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean updateAssignment(PaoScheduleAssignment assignment) {
        int rowsAffected =
            simpleJdbcTemplate.update(updateAssignment,
                                      assignment.getScheduleId(),
                                      assignment.getPaoId(),
                                      assignment.getCommandName(),
                                      assignment.getDisableOvUv(),
                                      assignment.getEventId());
        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PAOSchedule> getAllPaoScheduleNames() {
        List<PAOSchedule> scheduleNames =
            simpleJdbcTemplate.query(selectAllPaoSchedule, paoScheduleRowMapper);
        return scheduleNames;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean assignCommand(PaoScheduleAssignment pao) {
        int nextId = nextValueHelper.getNextValue("PAOScheduleAssignment");
        int rowsAffected = simpleJdbcTemplate.update(assignCommandToSchedule,
                                                     nextId,
                                                     pao.getScheduleId(),
                                                     pao.getPaoId(),
                                                     pao.getCommandName(),
                                                     pao.getDisableOvUv()
            );
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
        int rowsAffected = simpleJdbcTemplate.update(removeCommandFromScheduleByEventId, eventId);

        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deletePaoScheduleAssignmentsByScheduleId(int scheduleId) {
        int rowsAffected =
            simpleJdbcTemplate.update(deletePaoScheduleAssignmentByScheduleId, scheduleId);

        return rowsAffected == 1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean delete(int scheduleId) {
        deletePaoScheduleAssignmentsByScheduleId(scheduleId);
        int rowsAffected = simpleJdbcTemplate.update(deletePaoSchedule, scheduleId);

        return rowsAffected == 1;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}
