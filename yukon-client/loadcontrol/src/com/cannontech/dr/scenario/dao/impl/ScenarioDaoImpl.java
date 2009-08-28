package com.cannontech.dr.scenario.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.scenario.dao.ScenarioDao;

public class ScenarioDaoImpl implements ScenarioDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String baseScenarioQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
            + " WHERE type = 'LMSCENARIO'";
    private final static String singleScenarioByIdQuery = baseScenarioQuery
        + " AND paObjectId = ?";
    private final static String scenariosByProgramIdQuery = baseScenarioQuery
        + " AND paObjectId IN (SELECT scenarioId FROM lmControlScenarioProgram"
        + " WHERE programId = ?)";

    private final static ParameterizedRowMapper<DisplayablePao> scenarioRowMapper =
        new ParameterizedRowMapper<DisplayablePao>() {
        @Override
        public DisplayablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO);
            DisplayablePao retVal = new DisplayableDevice(paoId,
                                                          rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<DisplayablePao> getScenarios() {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(baseScenarioQuery,
                                                         scenarioRowMapper);
        return retVal;
    }

    @Override
    public DisplayablePao getScenario(int scenarioId) {
        return simpleJdbcTemplate.queryForObject(singleScenarioByIdQuery,
                                                 scenarioRowMapper,
                                                 scenarioId);
    }

    @Override
    public List<DisplayablePao> getScenariosForProgram(int programId) {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(scenariosByProgramIdQuery,
                                                                  scenarioRowMapper,
                                                                  programId);
        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
