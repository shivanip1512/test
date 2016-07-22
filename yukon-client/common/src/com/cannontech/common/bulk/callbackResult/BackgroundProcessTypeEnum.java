package com.cannontech.common.bulk.callbackResult;

public enum BackgroundProcessTypeEnum {
    UPDATE("Update", "update"),
    IMPORT("Import", "import"),
    MASS_CHANGE("Mass Change", "massChange"),
    MASS_DELETE("Mass Delete", "massDelete"),
    CHANGE_DEVICE_TYPE("Change Device Type", "changeDeviceType"),
    ADD_POINTS("Add Points", "addPoints"),
    REMOVE_POINTS("Remove Points", "removePoints"),
    UPDATE_POINTS("Update Points", "updatePoints"),
    ASSIGN_CONFIG("Assign Config", "assignConfig"),
    ARCHIVE_DATA_ANALYSIS("Archive Data Analysis", "archiveDataAnalysis"),
    IMPORT_FDR_TRANSLATION("Import FDR Translation", "fdrTranslationManager"),
    IMPORT_POINT("Import Point", "importPoint"),
    DATA_STREAMING("Data Streaming", "dataStreaming")
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
