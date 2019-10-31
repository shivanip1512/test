package com.cannontech.common.dr.setup;

import com.cannontech.common.dr.gear.setup.model.ProgramGear;

public class GearFilterResult {

    private Integer programId;
    private String programName;
    private ProgramGear programGear;
    private String gearDetails;

    public ProgramGear getProgramGear() {
        return programGear;
    }

    public void setProgramGear(ProgramGear programGear) {
        this.programGear = programGear;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getGearDetails() {
        return gearDetails;
    }

    public void setGearDetails(String gearDetails) {
        this.gearDetails = gearDetails;
    }

}
