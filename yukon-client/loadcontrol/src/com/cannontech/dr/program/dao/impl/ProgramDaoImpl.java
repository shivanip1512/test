package com.cannontech.dr.program.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.model.ControllablePao;
import com.cannontech.dr.program.dao.ProgramDao;

public class ProgramDaoImpl implements ProgramDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String singleProgramByIdQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
        + " WHERE type = 'LM DIRECT PROGRAM' AND paObjectId = ?";

    private final static ParameterizedRowMapper<ControllablePao> programRowMapper =
        new ParameterizedRowMapper<ControllablePao>() {
        @Override
        public ControllablePao mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_DIRECT_PROGRAM);
            ControllablePao retVal = new ControllablePao(paoId,
                                                         rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public ControllablePao getProgram(int programId) {
        return simpleJdbcTemplate.queryForObject(singleProgramByIdQuery,
                                                 programRowMapper,
                                                 programId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
