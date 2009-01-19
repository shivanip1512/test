package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.database.db.pao.PaoScheduleAssignment;
import com.cannontech.database.incrementer.NextValueHelper;

public class PaoScheduleDaoImpl implements PaoScheduleDao {

	private static final String assignCommandToSchedule;
	private static final String removeCommandFromScheduleByEventId;
	private static final String selectAllAssignments;
	private static final String selectAllPaoSchedule;
	
	private static final ParameterizedRowMapper<PaoScheduleAssignment> assignmentRowMapper;
	private static final ParameterizedRowMapper<PAOSchedule> paoScheduleRowMapper;
	private SimpleJdbcTemplate simpleJdbcTemplate;
		
	private NextValueHelper nextValueHelper = null;
	
	static{
		selectAllPaoSchedule = "SELECT ScheduleID,ScheduleName From PaoSchedule";
		
		selectAllAssignments = "SELECT sa.EventID, sa.ScheduleID, s.ScheduleName, s.NextRunTime, s.LastRunTime, sa.PaoID, po.PAOName, sa.Command " +
				               "FROM PAOScheduleAssignment sa, PAOSchedule s, YukonPAObject po " +
				               "WHERE s.ScheduleID = sa.ScheduleID AND sa.PaoID = po.PAObjectID ";
		
		assignCommandToSchedule = "INSERT INTO PAOScheduleAssignment (EventID, ScheduleID, PaoID, Command) VALUES (?,?,?,?)";
		
		removeCommandFromScheduleByEventId = "DELETE FROM PAOScheduleAssignment WHERE EventID = ?";
		
        assignmentRowMapper = new ParameterizedRowMapper<PaoScheduleAssignment>(){
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
            	
                return assignment;
            }
        };
		
        paoScheduleRowMapper = new ParameterizedRowMapper<PAOSchedule>(){ 
			public PAOSchedule mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				PAOSchedule sched = new PAOSchedule();
				sched.setScheduleID(rs.getInt("ScheduleID"));
				sched.setScheduleName(rs.getString("ScheduleName"));
				return sched;
			}
		};
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaoScheduleAssignment> getAllScheduleAssignments() {
		List<PaoScheduleAssignment> assignmentList = simpleJdbcTemplate.query(selectAllAssignments,assignmentRowMapper);
		
		return assignmentList;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PAOSchedule> getAllPaoScheduleNames() {
		List<PAOSchedule> scheduleNames = simpleJdbcTemplate.query(selectAllPaoSchedule,paoScheduleRowMapper);
		return scheduleNames;
	}
	
	@Override
	@Transactional(readOnly = false)
	public boolean assignCommand(PaoScheduleAssignment pao) {
		//NextValueHelper cannot be utilized until PAOScheduleAssign
		int nextId = getNextEventId();
		
		int rowsAffected = simpleJdbcTemplate.update( assignCommandToSchedule, 
				 									  nextId,
				 									  pao.getScheduleId(),
				 									  pao.getPaoId(),
				 									  pao.getCommandName()
													);
		return rowsAffected == 1;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean assignCommand(List<PaoScheduleAssignment> list) {
		boolean success = true;
		for(PaoScheduleAssignment assignment : list) {
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
	
	private int getNextEventId() {
		String sql = "SELECT Max(EventID)+1 FROM PAOScheduleAssignment"; 
		
		int id = simpleJdbcTemplate.queryForInt(sql);
		
		return id;
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
