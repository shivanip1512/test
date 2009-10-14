package com.cannontech.dr.controlarea.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.model.TriggerBackingFieldBase;
import com.cannontech.dr.controlarea.service.TriggerFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class TriggerFieldServiceImpl extends DemandResponseFieldServiceBase<LMControlAreaTrigger> 
                implements TriggerFieldService {
    
    @Autowired
    public void setBackingFieldList(List<TriggerBackingFieldBase> backingFieldList) {
        Builder<String, DemandResponseBackingField<LMControlAreaTrigger>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<LMControlAreaTrigger>>();
        for(DemandResponseBackingField<LMControlAreaTrigger> backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        backingFieldMap = builder.build();
    }
}
