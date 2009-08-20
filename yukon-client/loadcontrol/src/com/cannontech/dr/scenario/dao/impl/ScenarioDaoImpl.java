package com.cannontech.dr.scenario.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.PaoCategory;
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

    private final static ParameterizedRowMapper<DisplayableDevice> scenarioRowMapper =
        new ParameterizedRowMapper<DisplayableDevice>() {
        @Override
        public DisplayableDevice mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_SCENARIO,
                                                    PaoCategory.LOADMANAGEMENT);
            DisplayableDevice retVal = new DisplayableDevice(paoId,
                                                             rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<DisplayableDevice> getScenarios() {
        List<DisplayableDevice> retVal = simpleJdbcTemplate.query(baseScenarioQuery,
                                                         scenarioRowMapper);
        return retVal;
    }

    @Override
    public DisplayableDevice getScenario(int scenarioId) {
        return simpleJdbcTemplate.queryForObject(singleScenarioByIdQuery,
                                                 scenarioRowMapper,
                                                 scenarioId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
