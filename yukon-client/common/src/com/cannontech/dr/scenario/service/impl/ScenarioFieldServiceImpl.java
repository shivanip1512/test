package com.cannontech.dr.scenario.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.scenario.model.Scenario;
import com.cannontech.dr.scenario.model.ScenarioBackingFieldBase;
import com.cannontech.dr.scenario.service.ScenarioFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ScenarioFieldServiceImpl extends DemandResponseFieldServiceBase<Scenario> 
                implements ScenarioFieldService {
    
    @Autowired
    public void setBackingFieldList(List<ScenarioBackingFieldBase> backingFieldList) {

        Builder<String, DemandResponseBackingField<Scenario>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<Scenario>>();

        for(DemandResponseBackingField<Scenario> backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }

        backingFieldMap = builder.build();
    }
}
