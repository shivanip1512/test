package com.cannontech.cc.model;


public class ProgramTypeParameter {
    private Integer id;
    private ProgramType programType;
    private String parameterName;
    private String parameterDisplay;
    private String parameterValueDefault;
    
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    public String getParameterValueDefault() {
        return parameterValueDefault;
    }
    
    public void setParameterValueDefault(String parameterValueDefault) {
        this.parameterValueDefault = parameterValueDefault;
    }
    
    public ProgramType getProgramType() {
        return programType;
    }
    
    public void setProgramType(ProgramType programType) {
        this.programType = programType;
    }

    public String getParameterDisplay() {
        return parameterDisplay;
    }

    public void setParameterDisplay(String parameterDisplay) {
        this.parameterDisplay = parameterDisplay;
    }
}
