package com.cannontech.loadcontrol.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.dao.ProgramIdMapper;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;

public class LoadControlProgramDaoImpl implements LoadControlProgramDao {

    private SimpleJdbcOperations simpleJdbcTemplate;
    
    public int getProgramIdByProgramName(String programName) throws IllegalArgumentException {
        
        try {
            String sql = "SELECT ypo.PAObjectID" +
                  " FROM YukonPaObject ypo" +
                  " INNER JOIN LMPROGRAM lmp ON (ypo.PAObjectID = lmp.DEVICEID)" +
                  " WHERE ypo.PAOName = ?";
            
            return simpleJdbcTemplate.queryForInt(sql, programName);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("No program named " + programName);
        }
    }
    
    public int getScenarioIdForScenarioName(String scenarioName) throws IllegalArgumentException {
        
        try {
            String sql = "SELECT ypo.PAObjectID" +
                        " FROM YukonPaObject ypo" +
                        " WHERE ypo.PAOName = ? AND ypo.Type = 'LMSCENARIO'";
            
            return simpleJdbcTemplate.queryForInt(sql, scenarioName);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("No scenario named " + scenarioName);
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

    public List<Integer> getProgramIdsByScenarioName(String scenarioName) throws IllegalArgumentException {
        
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
        
        String sql = "SELECT ypo.PAOName AS ProgramName, lmpdg.GearName, lmpdg.GearNumber" +
        " FROM LMControlScenarioProgram lmcsp" +
        " INNER JOIN LMProgramDirectGear lmpdg ON (lmcsp.ProgramId = lmpdg.DeviceID AND lmcsp.StartGear = lmpdg.GearNumber)" +
        " INNER JOIN YukonPaObject ypo ON (lmpdg.DeviceID = ypo.PAObjectID)" +
        " WHERE lmcsp.scenarioid = ?";
        
        ParameterizedRowMapper<ProgramStartingGear> programStartingGearMapper = new ParameterizedRowMapper<ProgramStartingGear>() {
            
            public ProgramStartingGear mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                String programName = rs.getString("ProgramName");
                String gearName = rs.getString("GearName");
                int gearNumber = rs.getInt("GearNumber");
                
                ProgramStartingGear programStartingGear = new ProgramStartingGear(programName, gearName, gearNumber);
                return programStartingGear;
            }
        };
           
        
        List<ProgramStartingGear> programStartingGears = simpleJdbcTemplate.query(sql, programStartingGearMapper, scenarioId);
        return programStartingGears;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcOperations simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
