package com.cannontech.loadcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.dao.ProgramControlHistoryMapper;
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
        
    	SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT");
		sql.append("ph.ProgramId,");
		sql.append("ph.ProgramName,");
		sql.append("lmpgh1.GearName,");
		sql.append("lmpgh1.EventTime AS startTime,");
		sql.append("lmpgh2.EventTime AS stopTime");
		sql.append("FROM LMProgramGearHistory lmpgh1");
		sql.append("LEFT JOIN LMProgramGearHistory lmpgh2 ON (lmpgh1.LMProgramHistoryId = lmpgh2.LMProgramHistoryId)");
		sql.append("JOIN LMProgramHistory ph ON ((lmpgh1.LMProgramHistoryId = ph.LMProgramHistoryId))");
		sql.append("WHERE lmpgh1.Action = 'Start'");
		sql.append("AND lmpgh2.Action = 'Stop'");
		sql.append("AND lmpgh1.EventTime <=").appendArgument(stopDateTime);
		sql.append("AND lmpgh2.EventTime >=").appendArgument(startDateTime);
		if (programId != null) {
    		sql.append("AND ph.ProgramId").eq(programId);
    	}
		sql.append("ORDER BY lmpgh1.LMProgramHistoryId");
    	
		List<ProgramControlHistory> programControlHistory = yukonJdbcOperations.query(sql, new ProgramControlHistoryMapper());
    	return programControlHistory;
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
