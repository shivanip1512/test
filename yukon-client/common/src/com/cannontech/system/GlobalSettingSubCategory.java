package com.cannontech.system;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;

public enum GlobalSettingSubCategory implements DisplayableEnum {

    DATA_IMPORT_EXPORT(GlobalSettingCategory.APPLICATION),
    GRAPHING(GlobalSettingCategory.APPLICATION),
    DASHBOARD_WIDGET(GlobalSettingCategory.APPLICATION, YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS),
    
    MULTISPEAK(GlobalSettingCategory.INTEGRATION),
    VOICE(GlobalSettingCategory.INTEGRATION),
    OPEN_ADR(GlobalSettingCategory.INTEGRATION),
    WEATHER(GlobalSettingCategory.INTEGRATION),
    
    MISC(GlobalSettingCategory.OTHER),
    SECURITY(GlobalSettingCategory.OTHER),
    
    AMI(GlobalSettingCategory.SYSTEM_SETUP),
    AUTHENTICATION(GlobalSettingCategory.SYSTEM_SETUP),
    DR(GlobalSettingCategory.SYSTEM_SETUP),
    YUKON_LOGGERS(GlobalSettingCategory.SYSTEM_SETUP),
    YUKON_SERVICES(GlobalSettingCategory.SYSTEM_SETUP),
    THEMES(GlobalSettingCategory.SYSTEM_SETUP),
    WEB_SERVER(GlobalSettingCategory.SYSTEM_SETUP),
    DASHBOARD_ADMIN(GlobalSettingCategory.SYSTEM_SETUP, YukonRoleProperty.ADMIN_MANAGE_DASHBOARDS),
    ATTRIBUTES(GlobalSettingCategory.SYSTEM_SETUP, YukonRoleProperty.ADMIN_MANAGE_ATTRIBUTES, HierarchyPermissionLevel.OWNER);
    
    private GlobalSettingCategory category;
    private YukonRoleProperty roleProperty;
    private HierarchyPermissionLevel level;
    
    private GlobalSettingSubCategory(GlobalSettingCategory category) {
        this.category = category;
    }
    
    private GlobalSettingSubCategory(GlobalSettingCategory category, YukonRoleProperty roleProperty) {
        this.category = category;
        this.roleProperty = roleProperty;
    }
    
    private GlobalSettingSubCategory(GlobalSettingCategory category, YukonRoleProperty roleProperty, HierarchyPermissionLevel level) {
        this.category = category;
        this.roleProperty = roleProperty;
        this.level = level;
    }
    
    public GlobalSettingCategory getCategory() {
        return category;
    }
    
    public YukonRoleProperty getRoleProperty() {
        return roleProperty;
    }
    
    public HierarchyPermissionLevel getLevel() {
        return level;
    }

    @Override
    public String getFormatKey() {
        return "yukon.common.setting.subcategory." + name();
    }
    
    public String getDescriptionKey() {
        return getFormatKey() + ".description";
    }
    
}