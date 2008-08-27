package com.cannontech.stars.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.database.data.lite.stars.LiteMeterHardwareBase;
import com.cannontech.spring.SeparableRowMapper;

public class LiteMeterHardwareBaseRowMapper extends SeparableRowMapper<LiteMeterHardwareBase> {

    public LiteMeterHardwareBaseRowMapper() {
        super(new LiteInventoryBaseRowMapper());
    }
    
    @Override
    protected LiteMeterHardwareBase createObject(ResultSet rs) throws SQLException {
        return new LiteMeterHardwareBase();
    }

    @Override
    protected void mapRow(ResultSet rs, LiteMeterHardwareBase liteMeter) throws SQLException {
        liteMeter.setMeterNumber(rs.getString("MeterNumber"));
        liteMeter.setMeterTypeID(rs.getInt("MeterTypeId"));
    }

}
