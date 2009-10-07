package com.cannontech.dr.program.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.program.model.ProgramBackingFieldBase;
import com.cannontech.dr.program.service.ProgramFieldService;
import com.cannontech.dr.service.impl.DemandResponseFieldServiceBase;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class ProgramFieldServiceImpl extends DemandResponseFieldServiceBase<LMProgramBase> 
                implements ProgramFieldService {
    
    @Autowired
    public void setBackingFieldList(List<ProgramBackingFieldBase> backingFieldList) {
        backingFieldMap = new HashMap<String, DemandResponseBackingField<LMProgramBase>>();
        
        for(DemandResponseBackingField<LMProgramBase> backingField : backingFieldList) {
            backingFieldMap.put(backingField.getFieldName(), backingField);
        }
    }
}
