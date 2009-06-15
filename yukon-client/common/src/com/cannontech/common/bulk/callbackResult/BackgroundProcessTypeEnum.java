package com.cannontech.common.bulk.callbackResult;

public enum BackgroundProcessTypeEnum {

    UPDATE("Update", "update"),
    IMPORT("Import", "import"),
    MASS_CHANGE("Mass Change", "massChange"),
    MASS_DELETE("Mass Delete", "massDelete"),
    CHANGE_DEVICE_TYPE("Change Device Type", "changeDeviceType"),
    ADD_POINTS("Add Points", "addPoints"),
    REMOVE_POINTS("Remove Points", "removePoints"),
    ;
    
    private String title;
    private String pathValue;
    
    BackgroundProcessTypeEnum (String title, String pathValue) {
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
