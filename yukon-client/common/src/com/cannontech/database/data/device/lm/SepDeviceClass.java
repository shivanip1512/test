package com.cannontech.database.data.device.lm;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

public enum SepDeviceClass implements DisplayableEnum, DatabaseRepresentationSource {
    HVAC_COMPRESSOR_FURNACE((short)0x0001),
    BASEBOARD_HEAT((short)0x0002),
    WATER_HEATER((short)0x0004),
    POOL_PUMP((short)0x0008),
    SMART_APPLIANCE((short)0x0010),
    IRRIGATION_PUMP((short)0x0020),
    MANAGED_COMMERCIAL_INDUSTRIAL((short)0x0040),
    SIMPLE_RESIDENTIAL_ON_OFF((short)0x0080),
    EXTERIOR_LIGHTING((short)0x0100),
    INTERIOR_LIGHTING((short)0x0200),
    ELECTRIC_VEHICLE((short)0x0400),
    GENERATION_SYSTEMS((short)0x0800);

    //This is which bit is set for each class in a 16 bit value.
    private short bitValue;
    
    private SepDeviceClass(short bitValue) {
        this.bitValue = bitValue;
    }
    
    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
    
    public short getBitValue() {
        return bitValue;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.dr.setup.loadGroup.digisep." + name();
    }
}
