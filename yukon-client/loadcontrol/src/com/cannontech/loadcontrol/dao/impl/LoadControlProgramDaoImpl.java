package com.cannontech.loadcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.loadcontrol.dao.LMProgramGearHistory;
import com.cannontech.loadcontrol.dao.LMProgramGearHistoryMapper;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.dao.ProgramIdMapper;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;

public class LoadControlProgramDaoImpl implements LoadControlProgramDao {

    private YukonJdbcOperations yukonJdbcOperations;
    
    @Override
    public int getProgramIdByProgramName(String programName) throws NotFoundException {
        
        try {
            String sql = "SELECT ypo.PAObjectID" +
                  " FROM YukonPaObject ypo" +
                  " INNER JOIN LMPROGRAM lmp ON (ypo.PAObjectID = lmp.DEVICEID)" +
                  " WHERE ypo.PAOName = ?";
            
            return yukonJdbcOperations.queryForInt(sql, programName);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No program named " + programName);
        }
    }
    
    @Override
    public int getScenarioIdForScenarioName(String scenarioName) throws NotFoundException {
        
        try {
            String sql = "SELECT ypo.PAObjectID" +
                        " FROM YukonPaObject ypo" +
                        " WHERE ypo.PAOName = ? AND ypo.Type = 'LMSCENARIO'";
            
            return yukonJdbcOperations.queryForInt(sql, scenarioName);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No scenario named " + scenarioName);
        }
    }
    
    @Override
    public List<Integer> getAllProgramIds() {
        
        String sql = "SELECT ypo.PAObjectID AS ProgramID" +
            " FROM YukonPaObject ypo" +
            " INNER JOIN LMPROGRAM lmp ON (ypo.PAObjectID = lmp.DEVICEID)";
        List<Integer> programIds = yukonJdbcOperations.query(sql, new ProgramIdMapper());
        
        return programIds;
    }
    
    @Override
    public List<Integer> getProgramIdsByScenarioId(int scenarioId) {
        
        String sql = "SELECT lmsc.ProgramID" +
                    " FROM LMControlScenarioProgram lmsc" +
                    " WHERE lmsc.scenarioId = ?";
        List<Integer> programIds = yukonJdbcOperations.query(sql, new ProgramIdMapper(), scenarioId);
        
        return programIds;
    }

    @Override
    public List<Integer> getProgramIdsByScenarioName(String scenarioName) throws NotFoundException {
        
        int scenarioId = getScenarioIdForScenarioName(scenarioName);
        
        return getProgramIdsByScenarioId(scenarioId);
    }
    
    @Override
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId) {
        
        String sql = "SELECT lmcsp.StartGear" +
                    " FROM LMControlScenarioProgram lmcsp" +
                    " WHERE lmcsp.programid = ? AND lmcsp.scenarioid = ?";

        return yukonJdbcOperations.queryForInt(sql, programId, scenarioId);
    }
    
    @Override
    public List<ProgramStartingGear> getProgramStartingGearsForScenarioId(int scenarioId) {
        
        String sql = "SELECT ypo.PAObjectID, ypo.PAOName AS ProgramName, lmpdg.GearName, lmpdg.GearNumber" +
        " FROM LMControlScenarioProgram lmcsp" +
        " INNER JOIN LMProgramDirectGear lmpdg ON (lmcsp.ProgramId = lmpdg.DeviceID AND lmcsp.StartGear = lmpdg.GearNumber)" +
        " INNER JOIN YukonPaObject ypo ON (lmpdg.DeviceID = ypo.PAObjectID)" +
        " WHERE lmcsp.scenarioid = ?";
        
        ParameterizedRowMapper<ProgramStartingGear> programStartingGearMapper = new ParameterizedRowMapper<ProgramStartingGear>() {
            
            public ProgramStartingGear mapRow(ResultSet rs, int rowNum) throws SQLException {
                
            	int programId = rs.getInt("PAObjectID");
                String programName = rs.getString("ProgramName");
                String gearName = rs.getString("GearName");
                int gearNumber = rs.getInt("GearNumber");
                
                ProgramStartingGear programStartingGear = new ProgramStartingGear(programId, programName, gearName, gearNumber);
                return programStartingGear;
            }
        };
           
        
        List<ProgramStartingGear> programStartingGears = yukonJdbcOperations.query(sql, programStartingGearMapper, scenarioId);
        return programStartingGears;
    }
    
    // kinda not so performance tuned, but the table is awkward to work with. 
    // could probably be done with a really fancy query..
    @Override
    public List<ProgramControlHistory> getAllProgramControlHistory(Date startDateTime, Date stopDateTime) {
    	return baseProgramControlHistory(null, startDateTime, stopDateTime);
    }

    @Override
	public List<ProgramControlHistory> getProgramControlHistoryByProgramId(int programId, Date startDateTime, Date stopDateTime) {
		return baseProgramControlHistory(programId, startDateTime, stopDateTime);
    }
    
    private List<ProgramControlHistory> baseProgramControlHistory(Integer programId, Date startDateTime, Date stopDateTime) {
        
    	// get raw LMProgramHistory rows
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ph.ProgramId, ph.ProgramName, pgh.*");
    	sql.append("FROM LMProgramHistory ph");
    	sql.append("INNER JOIN LMProgramGearHistory pgh ON (ph.LMProgramHistoryId = pgh.LMProgramHistoryId)");
    	sql.append("WHERE pgh.EventTime").gt(startDateTime);
    	sql.append("AND pgh.EventTime").lte(stopDateTime);
    	if (programId != null) {
    		sql.append("AND ph.ProgramId").eq(programId);
    	}
    	sql.append("ORDER BY pgh.LMProgramHistoryId, pgh.LMProgramGearHistoryId");
        
        List<LMProgramGearHistory> lmProgramGearHistory = yukonJdbcOperations.query(sql, new LMProgramGearHistoryMapper());
        
        // sort
        ProgramControlHistory currentProgramHistoryRecord = null;
        LinkedHashMap<Integer, ProgramControlHistory> histMap = new LinkedHashMap<Integer, ProgramControlHistory>();
        
        for (LMProgramGearHistory gearHist : lmProgramGearHistory) {
        	
        	Integer programHistoryId = gearHist.getProgramHistoryId();
        	currentProgramHistoryRecord = histMap.get(programHistoryId);
        	if (currentProgramHistoryRecord == null) {
        		
        		currentProgramHistoryRecord = new ProgramControlHistory(gearHist.getProgramId());
        		currentProgramHistoryRecord.setProgramName(gearHist.getProgramName());
        		currentProgramHistoryRecord.setGearName(gearHist.getGearName());
        	}
        	
        	boolean recordOk = true;
        	String action = gearHist.getAction();
        	Date eventTime = gearHist.getEventTime();
        	if (action.equals("Start")) {
        		
        		currentProgramHistoryRecord.setStartDateTime(eventTime);
        	
        	} else if (action.equals("Stop")) {
        		
        		currentProgramHistoryRecord.setStopDateTime(eventTime);
        		
        		// has no start because startDateTime is in middle of control range? need to go find start
        		if (currentProgramHistoryRecord.getStartDateTime() == null) {
        			
        			SqlStatementBuilder eventTimeSql = new SqlStatementBuilder();
        			eventTimeSql.append("SELECT pgh.EventTime");
        			eventTimeSql.append("FROM LMProgramGearHistory pgh");
        			eventTimeSql.append("WHERE pgh.LMProgramHistoryId").eq(programHistoryId);
        			eventTimeSql.append("AND pgh.Action = 'Start'");
        	    	
        			// case for some crazy reason no Start exists for this stop, exclude record completely
        			try {
	        			eventTime = yukonJdbcOperations.queryForObject(sql, new DateRowMapper());
	        			currentProgramHistoryRecord.setStartDateTime(eventTime);
        			} catch (EmptyResultDataAccessException e) {
        				recordOk = false;
        			} catch (IncorrectResultSizeDataAccessException e) {
        				recordOk = false;
        			}
        		}
        	}
        	
        	// add/update record
        	if (recordOk) {
        		histMap.put(programHistoryId, currentProgramHistoryRecord);
        	} else {
        		histMap.remove(programHistoryId);
        	}
        }
        
        return new ArrayList<ProgramControlHistory>(histMap.values());
    }
    
    @Override
    public int getGearNumberForGearName(int programId, String gearName) throws NotFoundException {
    	
    	try {
    		String sql = "SELECT lmpdg.GEARNUMBER" +
	        " FROM LMPROGRAMDIRECTGEAR lmpdg" +
	        " INNER JOIN LMPROGRAM lmp ON (lmp.DEVICEID = lmpdg.DEVICEID)" +
	        " WHERE lmpdg.GEARNAME = ?" +
	        " AND lmp.DEVICEID = ?";
    		
    		return yukonJdbcOperations.queryForInt(sql, gearName, programId);
    	
    	} catch (IncorrectResultSizeDataAccessException e) {
    		throw new NotFoundException("Gear not found (programId = " + programId + ", geanName = " + gearName + ")");
    	}
    }
    
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
		this.yukonJdbcOperations = yukonJdbcOperations;
	}
}
