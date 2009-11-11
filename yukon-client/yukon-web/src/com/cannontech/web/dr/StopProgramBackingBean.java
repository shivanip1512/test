/**
 * 
 */
package com.cannontech.web.dr;

public class StopProgramBackingBean extends StopProgramBackingBeanBase {
    private int programId;
    private boolean useStopGear;
    private int gearNumber;

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public boolean isUseStopGear() {
        return useStopGear;
    }

    public void setUseStopGear(boolean useStopGear) {
        this.useStopGear = useStopGear;
    }

    public int getGearNumber() {
        return gearNumber;
    }

    public void setGearNumber(int gearNumber) {
        this.gearNumber = gearNumber;
    }
}