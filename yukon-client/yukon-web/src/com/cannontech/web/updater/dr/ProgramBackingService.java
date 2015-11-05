package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.program.service.ProgramFieldService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class ProgramBackingService extends UpdateBackingServiceBase<LMProgramBase> {
    private ProgramService programService = null;
    private ProgramFieldService programFieldService;

    @Override
    public DatedObject<LMProgramBase> getDatedObject(int programId) {
        DatedObject<LMProgramBase> datedProgram = programService.findDatedProgram(programId);
        return datedProgram;
    }
    
    @Override
    public Object getValue(DatedObject<LMProgramBase> datedObject, String[] idBits,
                           YukonUserContext userContext) {
        
        String fieldName = idBits[1];
        DemandResponseBackingField<LMProgramBase> backingField = programFieldService.getBackingField(fieldName);
        
        LMProgramBase program = null;
        if (datedObject != null) {
            program = datedObject.getObject();
        }
        if(idBits.length == 3) {
            Integer scenarioId = Integer.parseInt(idBits[2]);/// this still smells. 
            return backingField.getValue(program, scenarioId, userContext);
        } else {
            return backingField.getValue(program, userContext);   
        }
    }
    
    @Autowired
    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    @Autowired
    public void setProgramFieldService(ProgramFieldService programFieldService) {
        this.programFieldService = programFieldService;
    }
}

