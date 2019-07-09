package com.cannontech.web.support.waterNode.batteryLevel;

import com.cannontech.common.i18n.DisplayableEnum;

public enum WaterNodeBatteryLevel implements DisplayableEnum {
    NORMAL,
    LOW,
    CRITICALLY_LOW,
    UNREPORTED;
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.waterNode." + name();
    }

}
