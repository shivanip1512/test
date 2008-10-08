package com.cannontech.common.bulk.service;

public enum BulkOperationTypeEnum {

    UPDATE("Update", "update"),
    IMPORT("Import", "import"),
    MASS_CHANGE("Mass Change", "massChange"),
    MASS_DELETE("Mass Delete", "massDelete"),
    CHANGE_DEVICE_TYPE("Change Device Type", "changeDeviceType")
    ;
    
    private String title;
    private String pathValue;
    
    BulkOperationTypeEnum (String title, String pathValue) {
        this.title = title;
        this.pathValue = pathValue;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getPathValue() {
        return this.pathValue;
    }
}
