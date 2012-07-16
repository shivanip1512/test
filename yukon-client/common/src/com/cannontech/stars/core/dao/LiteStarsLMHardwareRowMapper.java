package com.cannontech.stars.core.dao;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

public class LiteStarsLMHardwareRowMapper extends SeparableRowMapper<LiteLmHardwareBase> {

    public LiteStarsLMHardwareRowMapper() {
        super(new LiteInventoryBaseRowMapper());
    }
    
    @Override
    protected LiteLmHardwareBase createObject(YukonResultSet rs) throws SQLException {
        return new LiteLmHardwareBase();
    }

    @Override
    protected void mapRow(YukonResultSet rs, LiteLmHardwareBase liteHardware) throws SQLException {
        liteHardware.setManufacturerSerialNumber(rs.getString("ManufacturerSerialNumber"));
        liteHardware.setLmHardwareTypeID(rs.getInt("LMHardwareTypeId"));
        liteHardware.setRouteID(rs.getInt("RouteId"));
        liteHardware.setConfigurationID(rs.getInt("ConfigurationId"));
    }

}