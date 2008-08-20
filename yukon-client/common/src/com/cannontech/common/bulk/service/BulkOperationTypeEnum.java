package com.cannontech.common.bulk.service;

public enum BulkOperationTypeEnum {

    UPDATE("Update", "update"),
    IMPORT("Import", "import"),
    MASS_CHANGE("Mass Change", "massChange"),
    MASS_DELETE("Mass Delete", "massDelete")
    ;
    
    private String title;
    private String name;
    
    BulkOperationTypeEnum (String title, String name) {
        this.title = title;
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getName() {
        return this.name;
    }
}
