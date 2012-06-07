package com.cannontech.stars.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;

public class LiteStarsLMHardwareRowMapper extends SeparableRowMapper<LiteStarsLMHardware> {

    public LiteStarsLMHardwareRowMapper() {
        super(new LiteInventoryBaseRowMapper());
    }
    
    @Override
    protected LiteStarsLMHardware createObject(ResultSet rs) throws SQLException {
        return new LiteStarsLMHardware();
    }

    @Override
    protected void mapRow(ResultSet rs, LiteStarsLMHardware liteHardware) throws SQLException {
        liteHardware.setManufacturerSerialNumber(rs.getString("ManufacturerSerialNumber"));
        liteHardware.setLmHardwareTypeID(rs.getInt("LMHardwareTypeId"));
        liteHardware.setRouteID(rs.getInt("RouteId"));
        liteHardware.setConfigurationID(rs.getInt("ConfigurationId"));
    }

}
