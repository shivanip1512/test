package com.cannontech.common.dr.program.setup.model;

import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.pao.PaoType;

public class LoadProgramCopy {

    private String name;
    private PaoType type;
    private ProgramConstraint constraint;
    private Boolean copyLoadGroups;
    private Boolean copyMemberControl;
    private OperationalState operationalState;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PaoType getType() {
        return type;
    }
    public void setType(PaoType type) {
        this.type = type;
    }
    public ProgramConstraint getConstraint() {
        return constraint;
    }
    public void setConstraint(ProgramConstraint constraint) {
        this.constraint = constraint;
    }
    public Boolean isCopyLoadGroups() {
        return copyLoadGroups;
    }
    public void setCopyLoadGroups(Boolean copyLoadGroups) {
        this.copyLoadGroups = copyLoadGroups;
    }
    public Boolean isCopyMemberControl() {
        return copyMemberControl;
    }
    public void setCopyMemberControl(Boolean copyMemberControl) {
        this.copyMemberControl = copyMemberControl;
    }
    public OperationalState getOperationalState() {
        return operationalState;
    }
    public void setOperationalState(OperationalState operationalState) {
        this.operationalState = operationalState;
    }

}
