/**
 * 
 */
package com.cannontech.web.dr;

public class ProgramStartInfo {
    private int programId;
    private int gearNumber;
    private boolean startProgram;
    private boolean overrideConstraints;

    public ProgramStartInfo() {
    }

    public ProgramStartInfo(int programId, int gearNumber,
            boolean startProgram) {
        this.programId = programId;
        this.gearNumber = gearNumber;
        this.startProgram = startProgram;
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

    public boolean isStartProgram() {
        return startProgram;
    }

    public void setStartProgram(boolean startProgram) {
        this.startProgram = startProgram;
    }

    public boolean isOverrideConstraints() {
        return overrideConstraints;
    }

    public void setOverrideConstraints(boolean overrideConstraints) {
        this.overrideConstraints = overrideConstraints;
    }
}