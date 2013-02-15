package com.cannontech.common.databaseMigration.model;


public enum ExportTypeEnum {
    ALTERNATE_PROGRAM,
    APP_CATEGORY,
    DIRECT_PROGRAM,
    EXPRESSCOM_LOAD_GROUP,
    LM_GROUP_PERMISSIONS,
    LOGIN_GROUP;
    
    private final static String typeKeyPrefix = "yukon.web.modules.support.databaseMigration.label.";
    private final static String itemKeyPrefix = "yukon.web.modules.support.databaseMigrationTemplate.";

    public String getTypeKey() {
        return typeKeyPrefix + name();
    }
    
    public String getItemKey() {
        return itemKeyPrefix + name();
    }
    
    public String getName(){
        return name();
    }
}
