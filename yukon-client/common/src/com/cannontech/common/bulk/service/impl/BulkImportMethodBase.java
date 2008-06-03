package com.cannontech.common.bulk.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;
import com.cannontech.common.bulk.service.BulkImportMethod;

public abstract class BulkImportMethodBase implements BulkImportMethod {

    private Set<BulkFieldColumnHeader> requiredColumns = null;
    private String name;
    
    public Set<BulkFieldColumnHeader> getRequiredColumns() {
        return requiredColumns;
    }
    
    public String getName() {
        return name;
    }
    
    @Required
    public void setName(String name) {
        this.name = name;
    }
    
}
