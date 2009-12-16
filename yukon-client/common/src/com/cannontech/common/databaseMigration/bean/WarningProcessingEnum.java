package com.cannontech.common.databaseMigration.bean;

public enum WarningProcessingEnum {
    OVERWRITE("Overwrite"),
    USE_EXISTING("Use Existing"),
    VALIDATE("Validate");
    
    String warningProcessingLabel;
    
    WarningProcessingEnum(String warningProcessingLabel){
        this.warningProcessingLabel = warningProcessingLabel;
    }
    
    public String getWarningProcessingLabel(){
        return warningProcessingLabel;
    };
}