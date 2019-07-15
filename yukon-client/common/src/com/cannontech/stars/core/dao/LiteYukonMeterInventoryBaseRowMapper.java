package com.cannontech.stars.core.dao;

import java.sql.SQLException;

import com.cannontech.database.YukonResultSet;
import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;

public class LiteYukonMeterInventoryBaseRowMapper extends SeparableRowMapper<LiteInventoryBase> {

    public LiteYukonMeterInventoryBaseRowMapper() {
        super(new LiteInventoryBaseRowMapper());
    }

    @Override
    protected LiteInventoryBase createObject(YukonResultSet rs) throws SQLException {
        Integer hardwareId = rs.getNullableInt("LMHardwareTypeId");
        if (hardwareId != null) {
            return new LiteLmHardwareBase();
        }
        return new LiteInventoryBase();
    }

    @Override
    protected void mapRow(YukonResultSet rs, LiteInventoryBase t) throws SQLException {
        Integer hardwareId = rs.getNullableInt("LMHardwareTypeId");
        if (hardwareId != null) {
            LiteLmHardwareBase liteHardware = (LiteLmHardwareBase) t;
            liteHardware.setManufacturerSerialNumber(rs.getString("ManufacturerSerialNumber"));
            liteHardware.setLmHardwareTypeID(hardwareId);
            liteHardware.setRouteID(rs.getInt("RouteId"));
            liteHardware.setConfigurationID(rs.getInt("ConfigurationId"));
        }
    }
}