package com.cannontech.dr.program.dao.impl;

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
import com.cannontech.dr.program.dao.ProgramDao;

public class ProgramDaoImpl implements ProgramDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String programsForScenarioQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
            + " WHERE paObjectId IN (SELECT programId"
            + " FROM lmControlScenarioProgram WHERE scenarioId = ?)";

    private final static ParameterizedRowMapper<DisplayableDevice> programRowMapper =
        new ParameterizedRowMapper<DisplayableDevice>() {
        @Override
        public DisplayableDevice mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_DIRECT_PROGRAM,
                                                    PaoCategory.LOADMANAGEMENT);
            DisplayableDevice retVal = new DisplayableDevice(paoId,
                                                             rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<DisplayableDevice> getProgramsForScenario(int scenarioId) {
        List<DisplayableDevice> retVal = simpleJdbcTemplate.query(programsForScenarioQuery,
                                                                  programRowMapper,
                                                                  scenarioId);
        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
