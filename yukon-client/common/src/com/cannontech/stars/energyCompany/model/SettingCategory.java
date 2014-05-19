package com.cannontech.stars.energyCompany.model;

import com.cannontech.common.i18n.DisplayableEnum;


public enum SettingCategory implements DisplayableEnum {
    ACCOUNT,
    HARDWARE,
    MISC,
    ;

    @Override
    public String getFormatKey() {
        return "yukon.common.energyCompanySetting.category." + name();
    }
}
