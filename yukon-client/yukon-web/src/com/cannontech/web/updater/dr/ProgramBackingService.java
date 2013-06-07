package com.cannontech.web.updater.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.DatedObject;
import com.cannontech.dr.DemandResponseBackingField;
import com.cannontech.dr.program.service.ProgramFieldService;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingServiceBase;

public class ProgramBackingService extends UpdateBackingServiceBase<Program> {
    private ProgramService programService = null;
    private ProgramFieldService programFieldService;

    @Override
    public DatedObject<Program> getDatedObject(int programId) {
        DatedObject<Program> datedProgram = programService.findDatedProgram(programId);
        return datedProgram;
    }
    
    @Override
    public Object getValue(DatedObject<Program> datedObject, String[] idBits,
                           YukonUserContext userContext) {

        String fieldName = idBits[1];

        DemandResponseBackingField<Program> backingField = 
            programFieldService.getBackingField(fieldName);
        
        Program program = null;
        if (datedObject != null) {
            program = datedObject.getObject();
        }
        
        return backingField.getValue(program, userContext);
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

