package com.cannontech.dr.controlarea.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.model.ControlAreaBackingFieldBase;
import com.cannontech.dr.controlarea.service.ControlAreaFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ControlAreaFieldServiceImpl extends DemandResponseFieldServiceBase<LMControlArea> 
                implements ControlAreaFieldService {
    
    @Autowired
    public void setBackingFieldList(List<ControlAreaBackingFieldBase> backingFieldList) {
        Builder<String, DemandResponseBackingField<LMControlArea>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<LMControlArea>>();
        for(DemandResponseBackingField<LMControlArea> backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        backingFieldMap = builder.build();
    }
}
