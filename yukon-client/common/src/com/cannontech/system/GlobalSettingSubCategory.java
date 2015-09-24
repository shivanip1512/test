package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;

public enum GlobalSettingSubCategory implements DisplayableEnum {

    DATA_EXPORT(GlobalSettingCategory.APPLICATION),
    GRAPHING(GlobalSettingCategory.APPLICATION),
    
    MULTISPEAK(GlobalSettingCategory.INTEGRATION),
    VOICE(GlobalSettingCategory.INTEGRATION),
    OPEN_ADR(GlobalSettingCategory.INTEGRATION),
    WEATHER(GlobalSettingCategory.INTEGRATION),
    
    MISC(GlobalSettingCategory.OTHER),
    SECURITY(GlobalSettingCategory.OTHER),
    
    NETWORK_MANAGER(GlobalSettingCategory.INTEGRATION),
    AMI(GlobalSettingCategory.SYSTEM_SETUP),
    AUTHENTICATION(GlobalSettingCategory.SYSTEM_SETUP),
    DR(GlobalSettingCategory.SYSTEM_SETUP),
    YUKON_SERVICES(GlobalSettingCategory.SYSTEM_SETUP),
    THEMES(GlobalSettingCategory.SYSTEM_SETUP),
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
        return getFormatKey() + ".description";
    }
    
}