package com.cannontech.loadcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.loadcontrol.dao.LmProgramGearHistory;
import com.cannontech.loadcontrol.dao.LmProgramGearHistoryMapper;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.dao.ProgramIdMapper;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.google.common.collect.Lists;

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
    
    @Override
    public List<ProgramControlHistory> getAllProgramControlHistory(Date startDateTime, Date stopDateTime) {
    	return baseProgramControlHistory(null, startDateTime, stopDateTime);
    }

    @Override
	public List<ProgramControlHistory> getProgramControlHistoryByProgramId(int programId, Date startDateTime, Date stopDateTime) {
		return baseProgramControlHistory(programId, startDateTime, stopDateTime);
    }
    
    private List<ProgramControlHistory> baseProgramControlHistory(Integer programId, Date startDateTime, Date stopDateTime) {
    	
    	// optional stopDateTime
    	if (stopDateTime == null) {
    		stopDateTime = new Date();
    	}
        
    	// extend the range on both ends so we pick up control that begins or ends outside the range. Intentional imprecise use of addMonths. Assumes control lasting over a month is totally unreasonable.
    	Date extendedStatDateTime = DateUtils.addMonths(startDateTime, -1);
    	Date extendedStopDateTime = DateUtils.addMonths(stopDateTime, 1);
    	
    	// get raw history
    	// the ORDER BY is crucial to the loop functionality
    	SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT");
		sql.append("ph.ProgramId,");
		sql.append("ph.ProgramName,");
		sql.append("hist.*");
		sql.append("FROM LMProgramGearHistory hist");
		sql.append("JOIN LMProgramHistory ph ON (hist.LMProgramHistoryId = ph.LMProgramHistoryId)");
		sql.append("JOIN YukonPAObject ypo ON (ph.programId = ypo.PAObjectId)");
		sql.append("WHERE hist.EventTime").gte(extendedStatDateTime);
		sql.append("AND hist.EventTime").lte(extendedStopDateTime);
		if (programId != null) {
    		sql.append("AND ph.ProgramId").eq(programId);
    	}
		sql.append("ORDER BY ph.ProgramId, hist.LMProgramHistoryId, hist.EventTime, hist.LMProgramGearHistoryId");
		List<LmProgramGearHistory> rawHistory = yukonJdbcOperations.query(sql, new LmProgramGearHistoryMapper());
		
		// generate ProgramControlHistory list by creating a new object for each non "Stop" action and filling in the object stop time with the EventTime of the following record (if one exists) 
		// * ProgramControlHistory may be organized in a few different ways:
		// 		Simple: Start-Stop with matching LMProgramGearHistory.LMProgramHistoryId
		// 		Gear Change: Start-Gear Change[-Gear Change]-Stop where all records have matching LMProgramGearHistory.LMProgramHistoryId
		// 		BGE-Style Gear Change: Start-Start[-Start]-Stop where the final Start-Stop have matching LMProgramGearHistory.LMProgramHistoryId but preceding Starts may not.
		// * All control that is not ongoing should end with a Stop record. Two Stops for the same LMProgramGearHistory.LMProgramHistoryId is not valid.
		List<ProgramControlHistory> results = Lists.newArrayListWithExpectedSize(rawHistory.size() / 2);
		for (int i = 0; i < rawHistory.size(); i++) {
			
			LmProgramGearHistory hist = rawHistory.get(i);
			
			// stops will never create new ProgramControlHistory objects
			if ("Stop".equals(hist.getAction())) {
				continue;
			}
			
			// new ProgramControlHistory
			ProgramControlHistory programControlHistory = new ProgramControlHistory(hist.getProgramId());
			programControlHistory.setProgramName(hist.getProgramName());
			programControlHistory.setGearName(hist.getGearName());
			programControlHistory.setStartDateTime(hist.getEventTime());
			programControlHistory.setKnownGoodStopDateTime(false);
			
			// nextHist
			LmProgramGearHistory nextHist = null;
			if (i < rawHistory.size() - 1) {
				nextHist = rawHistory.get(i + 1);
			}
			
			// in order for the nextHist to be the "Stop" for the current hist it has to be for the same program
			// if there is no nextHist then it is ongoing control
			if (nextHist != null && nextHist.getProgramId() == hist.getProgramId()) {
				
				// use nextHist EventTime as our stop time
				programControlHistory.setStopDateTime(nextHist.getEventTime());
				
				// This boolean should be set when it is known that the "Start" and "Stop" LMProgramGearHistory records that were found to create this ProgramControlHistory
				// share the same LMProgramGearHistory.LMProgramHistoryId.
				// If set to false, there is a good chance it is still valid, but if the duration between startDateTime and stopDateTime is suspiciously
				// long it may be that the LMProgramGearHistory identified as the "Stop" is not actually related.
				if (nextHist.getProgramHistoryId() == hist.getProgramHistoryId()) {
					programControlHistory.setKnownGoodStopDateTime(true);
				}
			}
			
			// check date range
			// is good programControlHistory if control was happening at ANY time within (inclusive) the range specified
			boolean withinDateRange = programControlHistory.getStartDateTime().compareTo(stopDateTime) <= 0
					   				  && (programControlHistory.getStopDateTime() == null || programControlHistory.getStopDateTime().compareTo(startDateTime) >= 0);
			
			// save result
			if (withinDateRange) {
				results.add(programControlHistory);
			}
		}
		
    	return results;
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
