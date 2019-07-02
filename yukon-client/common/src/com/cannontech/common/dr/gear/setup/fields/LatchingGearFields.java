package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LatchingGearFields implements ProgramGearFields {
    private Integer startControlState;
    private Integer capacityReduction;

    public Integer getStartControlState() {
        return startControlState;
    }
    public void setStartControlState(Integer startControlState) {
        this.startControlState = startControlState;
    }
    public Integer getCapacityReduction() {
        return capacityReduction;
    }
    public void setCapacityReduction(Integer capacityReduction) {
        this.capacityReduction = capacityReduction;
    }

    @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        LatchingGear latchingGear = (LatchingGear) programDirectGear;
        setCapacityReduction(latchingGear.getPercentReduction());
        setStartControlState(latchingGear.getStartControlState());

    }

     @Override
     public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        LatchingGear latchingGear = (LatchingGear) programDirectGear;
        latchingGear.setPercentReduction(getCapacityReduction());
        latchingGear.setStartControlState(getStartControlState());

    }

}
