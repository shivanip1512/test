package com.cannontech.amr.meter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class MeterRowMapper extends BaseMeterRowMapper implements ParameterizedRowMapper<Meter> {
    
    public MeterRowMapper(PaoGroupsWrapper paoGroupsWrapper) {
        super(paoGroupsWrapper);
    }

    public Meter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Meter meter = new Meter();
        fillInMeter(rs, meter);
        return meter;
    }

}
