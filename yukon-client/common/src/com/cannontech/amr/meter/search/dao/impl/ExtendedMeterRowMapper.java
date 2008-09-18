package com.cannontech.amr.meter.search.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.amr.meter.dao.impl.BaseMeterRowMapper;
import com.cannontech.amr.meter.search.model.ExtendedMeter;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class ExtendedMeterRowMapper extends BaseMeterRowMapper<ExtendedMeter> {

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
