package com.cannontech.common.constants;

import com.cannontech.common.i18n.DisplayableEnum;

public enum SelectionListCategory implements DisplayableEnum {
    SYSTEM,
    HARDWARE,
    CALL_TRACKING,
    SERVICE_ORDER,
    CONTROL,
    THERMOSTAT,
    ACCOUNT,
    CAP_CONTROL,
    APPLIANCE,
    APPLIANCE_AIR_CONDITIONING,
    APPLIANCE_WATER_HEATER,
    APPLIANCE_DUEL_FUEL,
    APPLIANCE_GRAIN_DRYER,
    APPLIANCE_STORAGE_HEAT,
    APPLIANCE_HEAT_PUMP,
    APPLIANCE_IRRIGATION,
    APPLIANCE_GENERATOR,
    RESIDENCE;

    private final static String keyPrefix = "yukon.dr.selectionListCategory.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
