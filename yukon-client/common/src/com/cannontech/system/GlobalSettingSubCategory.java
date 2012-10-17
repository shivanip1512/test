package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum GlobalSettingSubCategory implements DisplayableEnum {

    BILLING(GlobalSettingCategory.APPLICATION),
    CALC_HISTORICAL(GlobalSettingCategory.APPLICATION),
    GRAPHING(GlobalSettingCategory.APPLICATION),
    
    MULTISPEAK(GlobalSettingCategory.INTEGRATION),
    VOICE(GlobalSettingCategory.INTEGRATION),
    
    MISC(GlobalSettingCategory.OTHER),
    
    AMI(GlobalSettingCategory.SYSTEM_SETUP),
    AUTHENTICATION(GlobalSettingCategory.SYSTEM_SETUP),
    DR(GlobalSettingCategory.SYSTEM_SETUP),
    YUKON_SERVICES(GlobalSettingCategory.SYSTEM_SETUP),
    WEB_SERVER(GlobalSettingCategory.SYSTEM_SETUP);
    
    private GlobalSettingCategory category;
    
    private GlobalSettingSubCategory(GlobalSettingCategory category) {
        this.category = category;
    }
    
    public GlobalSettingCategory getCategory() {
        return category;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.setting.subcategory." + name();
    }
    
    public String getDescriptionKey() {
        return "yukon.common.setting.subcategory." + name() + ".description";
    }
    
}