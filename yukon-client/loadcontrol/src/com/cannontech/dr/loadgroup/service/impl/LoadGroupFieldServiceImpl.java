package com.cannontech.dr.loadgroup.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.loadgroup.model.LoadGroupBackingFieldBase;
import com.cannontech.dr.loadgroup.service.LoadGroupFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;

public class LoadGroupFieldServiceImpl extends DemandResponseFieldServiceBase<LMDirectGroupBase> 
                implements LoadGroupFieldService {
    
    @Autowired
    public void setBackingFieldList(List<LoadGroupBackingFieldBase> backingFieldList) {
        backingFieldMap = 
            new HashMap<String, DemandResponseBackingField<LMDirectGroupBase>>();
        
        for(LoadGroupBackingFieldBase backingField : backingFieldList) {
            backingFieldMap.put(backingField.getFieldName(), backingField);
        }
    }
}
