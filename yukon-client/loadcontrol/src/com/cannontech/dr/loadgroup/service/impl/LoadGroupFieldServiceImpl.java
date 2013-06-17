package com.cannontech.dr.loadgroup.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.loadgroup.model.LoadGroupBackingFieldBase;
import com.cannontech.dr.loadgroup.service.LoadGroupFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMDirectGroupBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class LoadGroupFieldServiceImpl extends DemandResponseFieldServiceBase<LMDirectGroupBase> 
                implements LoadGroupFieldService {
    
    @Autowired
    public void setBackingFieldList(List<LoadGroupBackingFieldBase> backingFieldList) {
        Builder<String, DemandResponseBackingField<LMDirectGroupBase>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<LMDirectGroupBase>>();
        for(LoadGroupBackingFieldBase backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        
        backingFieldMap = builder.build();
    }
}
