package com.cannontech.amr.meter.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.amr.meter.model.Meter;

public class MeterRowMapper extends BaseMeterRowMapper<Meter> {
    
    public MeterRowMapper() {
    }

    public Meter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Meter meter = new Meter();
        fillInMeter(rs, meter);
        return meter;
    }

}
