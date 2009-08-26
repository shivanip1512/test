package com.cannontech.dr.controlarea.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;

public class ControlAreaDaoImpl implements ControlAreaDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    private final static String baseControlAreaQuery =
        "SELECT paObjectId, paoName FROM yukonPAObject"
            + " WHERE type = 'LM CONTROL AREA'";
    private final static String singleControlAreaByIdQuery =
        baseControlAreaQuery + " AND paObjectId = ?";
    private final static String singleControlAreaByProgramIdQuery =
        baseControlAreaQuery + " AND paObjectId IN (SELECT deviceId"
        + " FROM lmControlAreaProgram WHERE lmProgramDeviceId = ?)";

    private final static ParameterizedRowMapper<DisplayableDevice> controlAreaRowMapper =
        new ParameterizedRowMapper<DisplayableDevice>() {
        @Override
        public DisplayableDevice mapRow(ResultSet rs, int rowNum)
                throws SQLException {
            PaoIdentifier paoId = new PaoIdentifier(rs.getInt("paObjectId"),
                                                    PaoType.LM_CONTROL_AREA);
            DisplayableDevice retVal = new DisplayableDevice(paoId,
                                                             rs.getString("paoName"));
            return retVal;
        }};


    @Override
    public List<DisplayableDevice> getControlAreas() {
        List<DisplayableDevice> retVal = simpleJdbcTemplate.query(baseControlAreaQuery,
                                                                  controlAreaRowMapper);
        return retVal;
    }

    @Override
    public DisplayableDevice getControlArea(int controlAreaId) {
        return simpleJdbcTemplate.queryForObject(singleControlAreaByIdQuery,
                                                 controlAreaRowMapper,
                                                 controlAreaId);
    }

    @Override
    public DisplayableDevice getControlAreaForProgram(int programId) {
        try {
            return simpleJdbcTemplate.queryForObject(singleControlAreaByProgramIdQuery,
                                                     controlAreaRowMapper,
                                                     programId);
        } catch (EmptyResultDataAccessException emptyResultSetException) {
            // in case the program doesn't have a parent control area yet
            return null;
        }
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}
