package com.cannontech.web.bulk;

import com.cannontech.common.pao.PaoType;

public class MeterProgrammingModel {
    
    private boolean newProgram;
    private String existingProgramGuid;
    private String name;
    private PaoType paoType;

    public boolean isNewProgram() {
        return newProgram;
    }
    public void setNewProgram(boolean newProgram) {
        this.newProgram = newProgram;
    }
    public String getExistingProgramGuid() {
        return existingProgramGuid;
    }
    public void setExistingProgramGuid(String existingProgramGuid) {
        this.existingProgramGuid = existingProgramGuid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PaoType getPaoType() {
        return paoType;
    }
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }

}
