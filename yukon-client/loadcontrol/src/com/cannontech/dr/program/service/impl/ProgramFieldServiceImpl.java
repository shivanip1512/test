package com.cannontech.dr.program.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.program.model.ProgramBackingFieldBase;
import com.cannontech.dr.program.service.ProgramFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ProgramFieldServiceImpl extends DemandResponseFieldServiceBase<LMProgramBase> 
                implements ProgramFieldService {
    
    @Autowired
    public void setBackingFieldList(List<ProgramBackingFieldBase> backingFieldList) {
        Builder<String, DemandResponseBackingField<LMProgramBase>> builder = 
            new ImmutableMap.Builder<String, DemandResponseBackingField<LMProgramBase>>();
        for(DemandResponseBackingField<LMProgramBase> backingField : backingFieldList) {
            builder.put(backingField.getFieldName(), backingField);
        }
        backingFieldMap = builder.build();
    }
}
