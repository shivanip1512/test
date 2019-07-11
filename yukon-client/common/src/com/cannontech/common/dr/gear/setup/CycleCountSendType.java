package com.cannontech.common.dr.gear.setup;

import com.cannontech.common.i18n.DisplayableEnum;

/**
 * This enum represent database store values and a part of request.
 */
public enum CycleCountSendType implements DisplayableEnum {

    FixedCount, 
    CountDown,
    LimitedCountDown, 
    FixedShedTime, 
    DynamicShedTime;

    private String baseKey = "yukon.web.modules.dr.setup.gear.";

    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
