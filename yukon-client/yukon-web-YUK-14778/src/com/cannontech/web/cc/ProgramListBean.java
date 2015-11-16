package com.cannontech.web.cc;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.util.ParentWrapper;

public class ProgramListBean {
    private LiteYukonUser yukonUser;
    private ProgramService programService;
    
    public ProgramListBean() {
    }
    
    public List<ParentWrapper<ProgramType, Program>> getProgramTypeList() {
        List<ProgramType> programTypeList = programService.getProgramTypeList(getYukonUser());
        List<ParentWrapper<ProgramType, Program>> wrappedProgramTypes 
            = new ArrayList<ParentWrapper<ProgramType, Program>>(programTypeList.size());
        for (ProgramType programType : programTypeList) {
            List<Program> programs = programService.getProgramList(programType);
            ParentWrapper<ProgramType, Program> pw = new ParentWrapper<ProgramType, Program>(programType, programs);
            wrappedProgramTypes.add(pw);
        }
        return wrappedProgramTypes;
    }
    
    public List<Program> getProgramList() {
        return programService.getProgramList(getYukonUser());
    }
    
    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }
    
    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

}
