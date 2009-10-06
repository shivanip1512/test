package com.cannontech.dr.loadgroup.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.loadgroup.model.LoadGroupBackingFieldBase;
import com.cannontech.dr.loadgroup.service.LoadGroupFieldService;
import com.cannontech.loadcontrol.data.LMGroupBase;

public class LoadGroupFieldServiceImpl implements LoadGroupFieldService {

    private Map<String, DemandResponseBackingField<LMGroupBase>> backingFieldMap;
    
    @Override
    public DemandResponseBackingField<LMGroupBase> getBackingField(String fieldName) {
        
        DemandResponseBackingField<LMGroupBase> backingField = backingFieldMap.get(fieldName);
        if(backingField == null) {
            throw new RuntimeException("No backing field for field: " + fieldName);
        }
        
        return backingField;
    }
    
    @Autowired
    public void setBackingFieldList(List<LoadGroupBackingFieldBase> backingFieldList) {
        backingFieldMap = 
            new HashMap<String, DemandResponseBackingField<LMGroupBase>>();
        
        for(DemandResponseBackingField<LMGroupBase> backingField : backingFieldList) {
            backingFieldMap.put(backingField.getFieldName(), backingField);
        }
    }

}
