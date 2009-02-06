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
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.dao.LMProgramGearHistory;
import com.cannontech.loadcontrol.dao.LMProgramGearHistoryMapper;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.dao.ProgramIdMapper;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;

public class LoadControlProgramDaoImpl implements LoadControlProgramDao {

    private SimpleJdbcOperations simpleJdbcTemplate;
    
    public int getProgramIdByProgramName(String programName) throws NotFoundException {
        
        try {
            String sql = "SELECT ypo.PAObjectID" +
                  " FROM YukonPaObject ypo" +
                  " INNER JOIN LMPROGRAM lmp ON (ypo.PAObjectID = lmp.DEVICEID)" +
                  " WHERE ypo.PAOName = ?";
            
            return simpleJdbcTemplate.queryForInt(sql, programName);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No program named " + programName);
        }
    }
    
    public int getScenarioIdForScenarioName(String scenarioName) throws NotFoundException {
        
        try {
            String sql = "SELECT ypo.PAObjectID" +
                        " FROM YukonPaObject ypo" +
                        " WHERE ypo.PAOName = ? AND ypo.Type = 'LMSCENARIO'";
            
            return simpleJdbcTemplate.queryForInt(sql, scenarioName);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No scenario named " + scenarioName);
        }
    }
    
    public List<Integer> getAllProgramIds() {
        
        String sql = "SELECT ypo.PAObjectID AS ProgramID" +
            " FROM YukonPaObject ypo" +
            " INNER JOIN LMPROGRAM lmp ON (ypo.PAObjectID = lmp.DEVICEID)";
        List<Integer> programIds = simpleJdbcTemplate.query(sql, new ProgramIdMapper());
        
        return programIds;
    }
    
    public List<Integer> getProgramIdsByScenarioId(int scenarioId) {
        
        String sql = "SELECT lmsc.ProgramID" +
                    " FROM LMControlScenarioProgram lmsc" +
                    " WHERE lmsc.scenarioId = ?";
        List<Integer> programIds = simpleJdbcTemplate.query(sql, new ProgramIdMapper(), scenarioId);
        
        return programIds;
    }

    public List<Integer> getProgramIdsByScenarioName(String scenarioName) throws NotFoundException {
        
        int scenarioId = getScenarioIdForScenarioName(scenarioName);
        
        return getProgramIdsByScenarioId(scenarioId);
    }
    
    public int getStartingGearForScenarioAndProgram(int programId, int scenarioId) {
        
        String sql = "SELECT lmcsp.StartGear" +
                    " FROM LMControlScenarioProgram lmcsp" +
                    " WHERE lmcsp.programid = ? AND lmcsp.scenarioid = ?";

        return simpleJdbcTemplate.queryForInt(sql, programId, scenarioId);
    }
    
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
           
        
        List<ProgramStartingGear> programStartingGears = simpleJdbcTemplate.query(sql, programStartingGearMapper, scenarioId);
        return programStartingGears;
    }
    
    // kinda not so performance tuned, but the table is awkward to work with. 
    // could probably be done with a really fancy query..
    public List<ProgramControlHistory> getProgramControlHistory(int programId, Date startDateTime, Date stopDateTime) {
        
    	// get raw LMProgramHistory rows
        String sql = "SELECT ph.ProgramId," +
        			" ph.ProgramName," +
        			" pgh.*" +
                    " FROM LMProgramHistory ph" +
                    " INNER JOIN LMProgramGearHistory pgh ON (ph.LMProgramHistoryId = pgh.LMProgramHistoryId)" +
                    " WHERE ph.ProgramId = ?" +
                    " AND pgh.EventTime > ?" +
                    " AND pgh.EventTime <= ?" +
                    " ORDER BY pgh.LMProgramHistoryId, pgh.LMProgramGearHistoryId";
        List<LMProgramGearHistory> lmProgramGearHistory = simpleJdbcTemplate.query(sql, new LMProgramGearHistoryMapper(), programId, startDateTime, stopDateTime);
        
        // sort
        ProgramControlHistory currentProgramHistoryRecord = null;
        LinkedHashMap<Integer, ProgramControlHistory> histMap = new LinkedHashMap<Integer, ProgramControlHistory>();
        
        for (LMProgramGearHistory gearHist : lmProgramGearHistory) {
        	
        	Integer programHistoryId = gearHist.getProgramHistoryId();
        	currentProgramHistoryRecord = histMap.get(programHistoryId);
        	if (currentProgramHistoryRecord == null) {
        		
        		currentProgramHistoryRecord = new ProgramControlHistory();
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
        			
        			sql = "SELECT pgh.EventTime" +
		           		 " FROM LMProgramGearHistory pgh" +
		           		 " WHERE pgh.LMProgramHistoryId = ?" +
		           		 " AND pgh.Action = 'Start'";
        			
        			// case for some crazy reason no Start exists for this stop, exclude record completely
        			try {
	        			eventTime = simpleJdbcTemplate.queryForObject(sql, Date.class, programHistoryId);
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
    
    public int getGearNumberForGearName(int programId, String gearName) throws NotFoundException {
    	
    	try {
    		String sql = "SELECT lmpdg.GEARNUMBER" +
	        " FROM LMPROGRAMDIRECTGEAR lmpdg" +
	        " INNER JOIN LMPROGRAM lmp ON (lmp.DEVICEID = lmpdg.DEVICEID)" +
	        " WHERE lmpdg.GEARNAME = ?" +
	        " AND lmp.DEVICEID = ?";
    		
    		return simpleJdbcTemplate.queryForInt(sql, gearName, programId);
    	
    	} catch (IncorrectResultSizeDataAccessException e) {
    		throw new NotFoundException("Gear not found (programId = " + programId + ", geanName = " + gearName + ")");
    	}
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
