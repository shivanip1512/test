package com.cannontech.dr.program.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    private final static String singleProgramByIdQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
        + " WHERE type = 'LM DIRECT PROGRAM' AND paObjectId = ?";

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
    public DisplayablePao getProgram(int programId) {
        return simpleJdbcTemplate.queryForObject(singleProgramByIdQuery,
                                                 programRowMapper,
                                                 programId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
