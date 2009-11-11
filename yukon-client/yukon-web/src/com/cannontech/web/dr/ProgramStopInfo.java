/**
 * 
 */
package com.cannontech.web.dr;

public class ProgramStopInfo {
    private int programId;
    private boolean stopProgram;
    private boolean overrideConstraints;

    public ProgramStopInfo() {
    }

    public ProgramStopInfo(int programId, boolean stopProgram) {
        this.programId = programId;
        this.stopProgram = stopProgram;
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
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
}