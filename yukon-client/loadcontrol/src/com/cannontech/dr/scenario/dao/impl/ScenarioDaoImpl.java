package com.cannontech.dr.scenario.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.scenario.dao.ScenarioDao;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.google.common.collect.Maps;

public class ScenarioDaoImpl implements ScenarioDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String singleScenarioByIdQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
        + " WHERE type = 'LMSCENARIO'"
        + " AND paObjectId = ?";
    private final static String scenariosByProgramIdQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
        + " WHERE type = 'LMSCENARIO'"
        + " AND paObjectId IN (SELECT scenarioId FROM lmControlScenarioProgram"
        + " WHERE programId = ?)";

    private final static ParameterizedRowMapper<DisplayablePao> scenarioRowMapper =
        new ParameterizedRowMapper<DisplayablePao>() {
        @Override
        public DisplayablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO);
            DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                           rs.getString("paoName"));
            return retVal;
        }};

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
        }};

    @Override
    public DisplayablePao getScenario(int scenarioId) {
        return simpleJdbcTemplate.queryForObject(singleScenarioByIdQuery,
                                                 scenarioRowMapper,
                                                 scenarioId);
    }

    @Override
    public List<DisplayablePao> findScenariosForProgram(int programId) {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(scenariosByProgramIdQuery,
                                                               scenarioRowMapper,
                                                               programId);
        return retVal;
    }

    @Override
    public Map<Integer, ScenarioProgram> findScenarioProgramsForScenario(
            int scenarioId) {
        Map<Integer, ScenarioProgram> retVal = Maps.newHashMap();

        List<ScenarioProgram> scenarioPrograms =
            simpleJdbcTemplate.query("SELECT scenarioId, programId, " +
            		"startOffset, stopOffset, startGear" +
            		" FROM lmControlScenarioProgram WHERE scenarioid = ?",
            		scenarioProgramRowMapper, scenarioId);
        for (ScenarioProgram scenarioProgram : scenarioPrograms) {
            retVal.put(scenarioProgram.getProgramId(), scenarioProgram);
        }

        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
