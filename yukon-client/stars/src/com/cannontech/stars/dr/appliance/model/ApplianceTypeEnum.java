package com.cannontech.stars.dr.appliance.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ApplianceTypeEnum implements DisplayableEnum {
    DEFAULT,
    AIR_CONDITIONER,
    STORAGE_HEAT,
    IRRIGATION,
    GRAIN_DRYER,
    HEAT_PUMP,
    GENERATOR,
    CHILLER,
    DUAL_STAGE,
    DUAL_FUEL,
    WATER_HEATER;

    private final String keyPrefix = "yukon.dr.appliance.displayname.";

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
