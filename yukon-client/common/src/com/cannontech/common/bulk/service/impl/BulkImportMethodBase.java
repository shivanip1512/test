package com.cannontech.common.bulk.service.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.service.BulkImportMethod;
import com.cannontech.common.bulk.service.BulkImportType;

public abstract class BulkImportMethodBase implements BulkImportMethod {
    
    public BulkImportType type;

    private String name;
    
    public String getName() {
        return name;
    }
    
    @Required
    public void setName(String name) {
        this.name = name;
    }

    public BulkImportType getType() {
        return type;
    }
    
    @Required
    public void setType(BulkImportType type) {
        this.type = type;
    }
    
}
