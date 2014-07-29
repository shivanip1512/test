package com.cannontech.dr.service.impl;

import java.util.Map;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.service.DemandResponseFieldService;

/**
 * Abstract Base Class for DemandResponseFieldService which implements generic field finding logic
 */
public abstract class DemandResponseFieldServiceBase<T> implements DemandResponseFieldService<T> {
    
    protected Map<String, DemandResponseBackingField<T>> backingFieldMap;

    @Override
    public DemandResponseBackingField<T> getBackingField(String fieldName) {
        
        DemandResponseBackingField<T> backingField = backingFieldMap.get(fieldName);
        if(backingField == null) {
            throw new RuntimeException("No backing field for field: " + fieldName);
        }
        
        return backingField;
    }

}
