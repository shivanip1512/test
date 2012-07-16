package com.cannontech.stars.core.dao;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.stars.database.data.lite.LiteMeterHardwareBase;

public class LiteMeterHardwareBaseRowMapper extends SeparableRowMapper<LiteMeterHardwareBase> {

    public LiteMeterHardwareBaseRowMapper() {
        super(new LiteInventoryBaseRowMapper());
    }
    
    @Override
    protected LiteMeterHardwareBase createObject(YukonResultSet rs) throws SQLException {
        return new LiteMeterHardwareBase();
    }

    @Override
    protected void mapRow(YukonResultSet rs, LiteMeterHardwareBase liteMeter) throws SQLException {
        liteMeter.setMeterNumber(rs.getString("MeterNumber"));
        liteMeter.setMeterTypeID(rs.getInt("MeterTypeId"));
    }

}
