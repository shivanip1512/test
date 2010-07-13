package com.cannontech.cc.model;


public class ProgramParameter {
    private Integer id;
    private Program program;
    private ProgramParameterKey parameterKey;
    private String parameterValue;
    
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getParameterValue() {
        return parameterValue;
    }
    
    public void setParameterValue(String value) {
        this.parameterValue = value;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public ProgramParameterKey getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(ProgramParameterKey parameterKey) {
        this.parameterKey = parameterKey;
    }
}
