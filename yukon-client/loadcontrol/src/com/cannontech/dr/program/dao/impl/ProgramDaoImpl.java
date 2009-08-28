package com.cannontech.dr.program.dao.impl;

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
import com.cannontech.dr.program.dao.ProgramDao;

public class ProgramDaoImpl implements ProgramDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String baseProgramQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
            + " WHERE type = 'LM DIRECT PROGRAM'";
    private final static String programsForScenarioQuery =
        baseProgramQuery + " AND paObjectId IN (SELECT programId"
            + " FROM lmControlScenarioProgram WHERE scenarioId = ?)";
    private final static String programsForControlAreaQuery =
        baseProgramQuery + " AND paObjectId IN (SELECT lmProgramDeviceId"
            + " FROM lmControlAreaProgram WHERE deviceId = ?)";
    private final static String singleProgramByIdQuery =
        baseProgramQuery + " AND paObjectId = ?";
    private final static String programsForLoadGroupQuery =
        baseProgramQuery + " AND paObjectId IN (SELECT deviceId"
            + " FROM lmProgramDirectGroup WHERE lmGroupDeviceId = ?)";

    private final static ParameterizedRowMapper<DisplayablePao> programRowMapper =
        new ParameterizedRowMapper<DisplayablePao>() {
        @Override
        public DisplayablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_DIRECT_PROGRAM);
            DisplayablePao retVal = new DisplayableDevice(paoId,
                                                          rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public List<DisplayablePao> getProgramsForScenario(int scenarioId) {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(programsForScenarioQuery,
                                                               programRowMapper,
                                                               scenarioId);
        return retVal;
    }

    @Override
    public List<DisplayablePao> getProgramsForControlArea(int controlAreaId) {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(programsForControlAreaQuery,
                                                               programRowMapper,
                                                               controlAreaId);
        return retVal;
    }

    @Override
    public List<DisplayablePao> getPrograms() {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(baseProgramQuery,
                                                               programRowMapper);
        return retVal;
    }

    @Override
    public DisplayablePao getProgram(int programId) {
        return simpleJdbcTemplate.queryForObject(singleProgramByIdQuery,
                                                 programRowMapper,
                                                 programId);
    }

    @Override
    public List<DisplayablePao> getProgramsForLoadGroup(int loadGroupId) {
        List<DisplayablePao> retVal = simpleJdbcTemplate.query(programsForLoadGroupQuery,
                                                               programRowMapper,
                                                               loadGroupId);
        return retVal;
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
