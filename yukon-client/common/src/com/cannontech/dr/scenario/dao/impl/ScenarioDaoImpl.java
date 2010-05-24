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
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.google.common.collect.Maps;

public class ScenarioDaoImpl implements ScenarioDao {
	
    private YukonJdbcOperations yukonJdbcOperations;

    private final static String scenariosByProgramIdQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
        + " WHERE type = 'LMSCENARIO'"
        + " AND paObjectId IN (SELECT scenarioId FROM lmControlScenarioProgram"
        + " WHERE programId = ?)";

    private final static String scenarioQuery =
        "SELECT PAO.PAObjectId, PAO.PAOName, COUNT(LMCSP.ProgramId) ScenarioProgramCount "+
        "FROM YukonPAObject PAO "+
        "LEFT JOIN LMControlScenarioProgram LMCSP ON LMCSP.ScenarioId = PAO.PAObjectId "+
        "WHERE PAO.Type = '"+ PaoType.LM_SCENARIO.getDatabaseRepresentation() +"' ";

    private final static ParameterizedRowMapper<ControllablePao> scenarioRowMapper =
        new ParameterizedRowMapper<ControllablePao>() {
        @Override
        public ControllablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO);
            ControllablePao retVal = new ControllablePao(paoId,
                                                         rs.getString("paoName"));
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

    private final static ParameterizedRowMapper<Scenario> scenariosRowMapper =
        new ParameterizedRowMapper<Scenario>() {
        @Override
        public Scenario mapRow(ResultSet rs, int rowNum) throws SQLException {

            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("PAObjectId"),
                                                    PaoType.LM_SCENARIO);
            String paoName = rs.getString("PAOName");
            int scenarioProgramCount = rs.getInt("ScenarioProgramCount");

            Scenario scenario = new Scenario(paoId, paoName, scenarioProgramCount);
            
            return scenario;
        }};

        
    @Override
    public Scenario getScenario(int scenarioId) {
        SqlStatementBuilder sql = new SqlStatementBuilder(scenarioQuery);
        sql.append("AND PAO.PAObjectId ").eq(scenarioId);
        sql.append("GROUP BY PAO.PAObjectId, PAO.PAOName");                                       

        Scenario scenario = yukonJdbcOperations.queryForObject(sql,
                                                               scenariosRowMapper);
        
        return scenario;
    }
    
    @Override
    public List<ControllablePao> getAllScenarios() {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ypo.PAObjectID, ypo.paoName");
    	sql.append("FROM YukonPaObject ypo");
    	sql.append("WHERE ypo.Type = ").appendArgument("LMSCENARIO");
    	
        return yukonJdbcOperations.query(sql, scenarioRowMapper);
    }

    @Override
    public List<ControllablePao> findScenariosForProgram(int programId) {
        List<ControllablePao> retVal = yukonJdbcOperations.query(scenariosByProgramIdQuery,
                                                               scenarioRowMapper,
                                                               programId);
        return retVal;
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
