package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OnOff implements DisplayableEnum {
    ON,
    OFF;

    @Override
    public String getFormatKey() {
        if(this == ON){
            return "yukon.web.defaults.on";
        }
        return "yukon.web.defaults.off";
    }

    public boolean isOn() {
        return this == ON;
    }
}