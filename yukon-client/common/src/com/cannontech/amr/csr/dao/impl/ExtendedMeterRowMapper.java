package com.cannontech.amr.csr.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.amr.csr.model.ExtendedMeter;
import com.cannontech.amr.meter.dao.impl.BaseMeterRowMapper;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class ExtendedMeterRowMapper extends BaseMeterRowMapper  implements ParameterizedRowMapper<ExtendedMeter> {

    public ExtendedMeterRowMapper(PaoGroupsWrapper paoGroupsWrapper) {
        super(paoGroupsWrapper);
    }
    

    public ExtendedMeter mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExtendedMeter pao = new ExtendedMeter();
        fillInMeter(rs, pao);
        pao.setMeterNumber(rs.getString("meternumber"));
        return pao;
    }

}
