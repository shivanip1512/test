package com.cannontech.database.data.device.lm;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum SepDeviceClass implements DatabaseRepresentationSource {
    HVAC_COMPRESSOR_FURNACE,
    BASEBOARD_HEAT,
    WATER_HEATER,
    POOL_PUMP,
    SMART_APPLIANCE,
    IRRIGATION_PUMP,
    MANAGED_COMMERCIAL_INDUSTRIAL,
    SIMPLE_RESIDENTIAL_ON_OFF,
    EXTERIOR_LIGHTING,
    INTERIOR_LIGHTING,
    ELECTRIC_VEHICLE,
    GENERATION_SYSTEMS;

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
}
