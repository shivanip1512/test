package com.cannontech.common.bulk.service.impl;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.service.BulkImportMethod;

public abstract class BulkImportMethodBase implements BulkImportMethod {

    private String name;
    
    public String getName() {
        return name;
    }
    
    @Required
    public void setName(String name) {
        this.name = name;
    }
    
}
