package com.cannontech.dr.scenario.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.google.common.collect.Maps;

public class ScenarioDaoImpl implements ScenarioDao {
	
    private YukonJdbcOperations yukonJdbcOperations;

    private final static String scenarioQuery =
        "SELECT PAO.PAObjectId, PAO.PAOName, COUNT(LMCSP.ProgramId) ScenarioProgramCount "+
        "FROM YukonPAObject PAO "+
        "LEFT JOIN LMControlScenarioProgram LMCSP ON LMCSP.ScenarioId = PAO.PAObjectId "+
        "WHERE PAO.Type = '"+ PaoType.LM_SCENARIO.getDatabaseRepresentation() +"' ";

    private final static ParameterizedRowMapper<Scenario> scenarioRowMapper =
        new ParameterizedRowMapper<Scenario>() {
        @Override
        public Scenario mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO);
            String paoName = rs.getString("paoName");
            int scenarioProgramCount = rs.getInt("ScenarioProgramCount");
            Scenario retVal = new Scenario(paoId,
                                           paoName,
                                           scenarioProgramCount);
            
            return retVal;
        }
    };

    private final static ParameterizedRowMapper<ScenarioProgram> scenarioProgramRowMapper =
        new ParameterizedRowMapper<ScenarioProgram>() {
        @Override
        public ScenarioProgram mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            ScenarioProgram scenarioProgram = new ScenarioProgram(rs.getInt("scenarioId"),
                                                                  rs.getInt("programId"),
                                                                  rs.getInt("startOffset"),
                                                                  rs.getInt("stopOffset"),
                                                                  rs.getInt("startGear"));
            return scenarioProgram;
        }
    };

    @Override
    public Scenario getScenario(int scenarioId) {
        SqlStatementBuilder sql = new SqlStatementBuilder(scenarioQuery);
        sql.append("AND PAO.PAObjectId ").eq(scenarioId);
        sql.append("GROUP BY PAO.PAObjectId, PAO.PAOName");                                       

        Scenario scenario = yukonJdbcOperations.queryForObject(sql,
                                                               scenarioRowMapper);
        
        return scenario;
    }
    
    @Override
    public List<Scenario> getAllScenarios() {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder(scenarioQuery);
        sql.append("GROUP BY PAO.PAObjectId, PAO.PAOName");                                       

        return yukonJdbcOperations.query(sql, scenarioRowMapper);
    }

    @Override
    public List<Scenario> findScenariosForProgram(int programId) {

        SqlStatementBuilder sql = new SqlStatementBuilder(scenarioQuery);
        sql.append("AND PAO.PAObjectId IN (SELECT ScenarioId ");
        sql.append("                       FROM LMControlScenarioProgram ");
        sql.append("                       WHERE ProgramId").eq(programId).append(")");
        sql.append("GROUP BY PAO.PAObjectId, PAO.PAOName");                                       
        
        return yukonJdbcOperations.query(sql, scenarioRowMapper);
    }

    @Override
    public Map<Integer, ScenarioProgram> findScenarioProgramsForScenario(
            int scenarioId) {
        Map<Integer, ScenarioProgram> retVal = Maps.newHashMap();

        List<ScenarioProgram> scenarioPrograms =
        	yukonJdbcOperations.query("SELECT scenarioId, programId, " +
            		"startOffset, stopOffset, startGear" +
            		" FROM lmControlScenarioProgram WHERE scenarioid = ?",
            		scenarioProgramRowMapper, scenarioId);
        for (ScenarioProgram scenarioProgram : scenarioPrograms) {
            retVal.put(scenarioProgram.getProgramId(), scenarioProgram);
        }

        return retVal;
    }

    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
		this.yukonJdbcOperations = yukonJdbcOperations;
	}
}
