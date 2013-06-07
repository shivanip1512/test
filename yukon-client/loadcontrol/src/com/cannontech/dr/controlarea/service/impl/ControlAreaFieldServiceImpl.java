package com.cannontech.dr.controlarea.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.model.ControlAreaBackingFieldBase;
import com.cannontech.dr.controlarea.service.ControlAreaFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ControlAreaFieldServiceImpl extends DemandResponseFieldServiceBase<ControlAreaItem> 
                implements ControlAreaFieldService {
    
    @Autowired
    public void setBackingFieldList(List<ControlAreaBackingFieldBase> backingFieldList) {
        Builder<String, DemandResponseBackingField<ControlAreaItem>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<ControlAreaItem>>();
        for(DemandResponseBackingField<ControlAreaItem> backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        backingFieldMap = builder.build();
    }
}
