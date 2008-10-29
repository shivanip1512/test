package com.cannontech.loadcontrol.service.data;


public class ProgramControlHistory {

    private int programId;
    private String programName;
    private String bunchOfHistoryStuffAndThingsProbablyAndManyMoreFields;
    
    public int getProgramId() {
        return programId;
    }
    public void setProgramId(int programId) {
        this.programId = programId;
    }
    public String getProgramName() {
        return programName;
    }
    public void setProgramName(String programName) {
        this.programName = programName;
    }
    public String getBunchOfHistoryStuffAndThingsProbablyAndManyMoreFields() {
        return bunchOfHistoryStuffAndThingsProbablyAndManyMoreFields;
    }
    public void setBunchOfHistoryStuffAndThingsProbablyAndManyMoreFields(
            String bunchOfHistoryStuffAndThingsProbablyAndManyMoreFields) {
        this.bunchOfHistoryStuffAndThingsProbablyAndManyMoreFields = bunchOfHistoryStuffAndThingsProbablyAndManyMoreFields;
    }
}
