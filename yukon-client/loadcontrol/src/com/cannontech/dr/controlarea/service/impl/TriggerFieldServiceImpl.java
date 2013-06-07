package com.cannontech.dr.controlarea.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.model.TriggerBackingFieldBase;
import com.cannontech.dr.controlarea.service.TriggerFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class TriggerFieldServiceImpl extends DemandResponseFieldServiceBase<ControlAreaTriggerItem> 
                implements TriggerFieldService {
    
    @Autowired
    public void setBackingFieldList(List<TriggerBackingFieldBase> backingFieldList) {
        Builder<String, DemandResponseBackingField<ControlAreaTriggerItem>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<ControlAreaTriggerItem>>();
        for(DemandResponseBackingField<ControlAreaTriggerItem> backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        backingFieldMap = builder.build();
    }
}
