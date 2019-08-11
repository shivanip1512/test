package com.cannontech.common.dr.program.setup.model;

import javax.validation.Valid;

import com.cannontech.common.dr.gear.setup.OperationalState;

public class LoadProgramCopy {

    private String name;

    @Valid
    private ProgramConstraint constraint;
    private Boolean copyMemberControl;
    private OperationalState operationalState;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ProgramConstraint getConstraint() {
        return constraint;
    }
    public void setConstraint(ProgramConstraint constraint) {
        this.constraint = constraint;
    }

    public Boolean getCopyMemberControl() {
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
