/**
 * 
 */
package com.cannontech.web.dr;

public class ProgramGearChangeInfo {
    private int programId;
    private int gearNumber;
    private boolean changeGear;

    public ProgramGearChangeInfo() {
    }

    public ProgramGearChangeInfo(int programId, int gearNumber,
            boolean changeGear) {
        this.programId = programId;
        this.gearNumber = gearNumber;
        this.changeGear = changeGear;
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

    public boolean isChangeGear() {
        return changeGear;
    }

    public void setChangeGear(boolean changeGear) {
        this.changeGear = changeGear;
    }
}