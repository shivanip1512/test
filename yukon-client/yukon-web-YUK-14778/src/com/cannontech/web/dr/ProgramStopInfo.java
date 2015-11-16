/**
 * 
 */
package com.cannontech.web.dr;

public class ProgramStopInfo {
    private int programId;
    private int gearNumber;
    private boolean stopProgram;
    private boolean useStopGear;
    private boolean overrideConstraints;

    public ProgramStopInfo() {
    }

    public ProgramStopInfo(int programId, int gearNumber, boolean stopProgram) {
        this.programId = programId;
        this.gearNumber = gearNumber;
        this.stopProgram = stopProgram;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public int getGearNumber() {
        return gearNumber;
    }

    public void setGearNumber(int gearNumber) {
        this.gearNumber = gearNumber;
    }
    
    public boolean isStopProgram() {
        return stopProgram;
    }

    public void setStopProgram(boolean stopProgram) {
        this.stopProgram = stopProgram;
    }

    public boolean isOverrideConstraints() {
        return overrideConstraints;
    }

    public void setOverrideConstraints(boolean overrideConstraints) {
        this.overrideConstraints = overrideConstraints;
    }

    public boolean isUseStopGear() {
        return useStopGear;
    }

    public void setUseStopGear(boolean useStopGear) {
        this.useStopGear = useStopGear;
    }
}