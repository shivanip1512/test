package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum OnOff implements DisplayableEnum {
    ON,
    OFF;

    @Override
    public String getFormatKey() {
        if(this == ON){
            return "yukon.common.on";
        }
        return "yukon.common.off";
    }

    public boolean isOn() {
        return this == ON;
    }
}