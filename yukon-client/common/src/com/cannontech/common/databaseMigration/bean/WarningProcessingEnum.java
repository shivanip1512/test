package com.cannontech.common.databaseMigration.bean;

public enum WarningProcessingEnum {
    OVER_WRITE("Over Write"),
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