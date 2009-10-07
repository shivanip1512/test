package com.cannontech.dr.controlarea.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.controlarea.model.TriggerBackingFieldBase;
import com.cannontech.dr.controlarea.service.TriggerFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;

public class TriggerFieldServiceImpl extends DemandResponseFieldServiceBase<LMControlAreaTrigger> 
                implements TriggerFieldService {
    
    @Autowired
    public void setBackingFieldList(List<TriggerBackingFieldBase> backingFieldList) {
        backingFieldMap = new HashMap<String, DemandResponseBackingField<LMControlAreaTrigger>>();
        
        for(DemandResponseBackingField<LMControlAreaTrigger> backingField : backingFieldList) {
            backingFieldMap.put(backingField.getFieldName(), backingField);
        }
    }
}
