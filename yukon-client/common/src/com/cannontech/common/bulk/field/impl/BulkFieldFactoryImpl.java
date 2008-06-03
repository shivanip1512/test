package com.cannontech.common.bulk.field.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.field.BulkField;
import com.cannontech.common.bulk.field.BulkFieldFactory;

public class BulkFieldFactoryImpl implements BulkFieldFactory {

    private List<BulkField<?, ?>> bulkFields = Collections.emptyList();
    
    public BulkField<?, ?> getBulkField(String fieldName) {
        
        for (BulkField<?, ?> bulkField : getBulkFields()) {
            if (bulkField.getInputSource().getField().equals(fieldName)) {
                return bulkField;
            }
        }
        
        return null;
    }
    
    
    public List<BulkField<?, ?>> getBulkFields() {
        return bulkFields;
    }
    
    @Autowired
    public void setBulkFields(List<BulkField<?, ?>> bulkFields) {
        this.bulkFields = bulkFields;
    }
}
