package com.cannontech.common.bulk.service;

public enum BulkOperationTypeEnum {

    UPDATE("Update"),
    IMPORT("Import")
    ;
    
    private String title;
    
    BulkOperationTypeEnum (String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
}
