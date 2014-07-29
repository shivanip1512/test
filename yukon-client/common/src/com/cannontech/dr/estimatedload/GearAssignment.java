package com.cannontech.dr.estimatedload;

import com.cannontech.loadcontrol.data.LMProgramDirectGear;

public class GearAssignment {

    private LMProgramDirectGear gear;
    private Integer formulaId; // null if not assigned


    public GearAssignment(LMProgramDirectGear gear, Integer formulaId) {
        this.gear = gear;
        this.formulaId = formulaId;
    }

    public Integer getFormulaId() {
        return formulaId;
    }

    public void setFormulaId(Integer formulaId) {
        this.formulaId = formulaId;
    }

    public LMProgramDirectGear getGear() {
        return gear;
    }

    public void setGear(LMProgramDirectGear gear) {
        this.gear = gear;
    }
}
