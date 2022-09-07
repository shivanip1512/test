package com.cannontech.dr.scenario.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class ScenarioDaoImpl implements ScenarioDao {
	
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    private SqlStatementBuilder singleScenarioByIdQuery;
    {
        singleScenarioByIdQuery = new SqlStatementBuilder();
        singleScenarioByIdQuery.append("SELECT PAO.PAObjectId, PAO.PAOName, COUNT(LMCSP.ProgramId) ProgramCount");
        singleScenarioByIdQuery.append("FROM YukonPAObject PAO");
        singleScenarioByIdQuery.append("LEFT JOIN LMControlScenarioProgram LMCSP ON LMCSP.ScenarioId = PAO.PAObjectId");
        singleScenarioByIdQuery.append("WHERE PAO.Type").eq(PaoType.LM_SCENARIO);
    }
    
    private SqlStatementBuilder singleScenarioByIdQueryGroupBy;
    {
        singleScenarioByIdQueryGroupBy = new SqlStatementBuilder();
        singleScenarioByIdQueryGroupBy.append("GROUP BY PAO.PAObjectId, PAO.PAOName");
    }
    
    private final static RowMapper<Scenario> scenarioRowMapper =
        new RowMapper<Scenario>() {
            @Override
            public Scenario mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                PaoIdentifier paoId = new PaoIdentifier(rs.getInt("PAObjectId"),
                                                        PaoType.LM_SCENARIO);
                Scenario retVal = new Scenario(paoId,
                                               rs.getString("PAOName"),
                                               rs.getInt("ProgramCount"));
                return retVal;
            }
        };

    private final static RowMapper<ScenarioProgram> scenarioProgramRowMapper =
        new RowMapper<ScenarioProgram>() {
        @Override
        public ScenarioProgram mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            ScenarioProgram scenarioProgram = new ScenarioProgram(rs.getInt("scenarioId"),
                                                                  rs.getInt("programId"),
                                                                  rs.getInt("startOffset"),
                                                                  rs.getInt("stopOffset"),
                                                                  rs.getInt("startGear"));
            return scenarioProgram;
        }};

    @Override
    public Scenario getScenario(int scenarioId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(singleScenarioByIdQuery);
        sql.append("AND PAO.PAObjectId").eq(scenarioId);
        sql.append(singleScenarioByIdQueryGroupBy);
        
        return jdbcTemplate.queryForObject(sql, scenarioRowMapper);
    }
    
    @Override
    public List<Scenario> getAllScenarios() {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.appendFragment(singleScenarioByIdQuery);
    	sql.append(singleScenarioByIdQueryGroupBy);
    	
        return jdbcTemplate.query(sql, scenarioRowMapper);
    }

    @Override
    public List<Scenario> findScenariosForProgram(int programId) {
        SqlStatementBuilder innerSql = new SqlStatementBuilder();
        innerSql.append("SELECT LMCSP.ScenarioId");
        innerSql.append("FROM LMControlScenarioProgram LMCSP"); 
        innerSql.append("WHERE LMCSP.ProgramId").eq(programId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(singleScenarioByIdQuery);
        sql.append("AND PAO.PAObjectId").in(innerSql);
        sql.append(singleScenarioByIdQueryGroupBy);
        
        List<Scenario> retVal = jdbcTemplate.query(sql, scenarioRowMapper);
        return retVal;
    }

    @Override
    public Map<Integer, ScenarioProgram> findScenarioProgramsForScenario(
            int scenarioId) {
        Map<Integer, ScenarioProgram> retVal = Maps.newHashMap();

        List<ScenarioProgram> scenarioPrograms =
                jdbcTemplate.query("SELECT scenarioId, programId, " +
            		"startOffset, stopOffset, startGear" +
            		" FROM lmControlScenarioProgram WHERE scenarioid = ?",
            		scenarioProgramRowMapper, scenarioId);
        for (ScenarioProgram scenarioProgram : scenarioPrograms) {
            retVal.put(scenarioProgram.getProgramId(), scenarioProgram);
        }

        return retVal;
    }
    
    @Override
    public  Multimap<Integer, Integer> getGroupIdsByScenarioId(Set<Integer> scenarioIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(jdbcTemplate);
        Multimap<Integer, Integer> result = HashMultimap.create();
        template.query(e -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT lcsp.ScenarioId, ldg.LMGroupDeviceId");
            sql.append("FROM LmControlScenarioProgram lcsp");
            sql.append("JOIN LMProgramDirectGroup ldg ON ldg.DeviceId=lcsp.ProgramId");
            sql.append("WHERE lcsp.ScenarioId").in(scenarioIds);
            return sql;
        }, scenarioIds, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                result.put(rs.getInt("ScenarioId"), rs.getInt("LMGroupDeviceId"));
            }
        });
        return result;
    }
}
