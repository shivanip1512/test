package com.cannontech.common.dr.program.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={ "constraintName"}, allowGetters= true, ignoreUnknown = true)
public class ProgramConstraint {

    private Integer constraintId;
    private String constraintName;

    public Integer getConstraintId() {
        return constraintId;
    }
    public void setConstraintId(Integer constraintId) {
        this.constraintId = constraintId;
    }
    public String getConstraintName() {
        return constraintName;
    }
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }
    
    
}
