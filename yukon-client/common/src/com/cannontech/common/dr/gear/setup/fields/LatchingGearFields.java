package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.ControlStartState;
import com.cannontech.database.data.device.lm.LatchingGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LatchingGearFields implements ProgramGearFields {
    private ControlStartState startControlState;
    private Integer capacityReduction;

    public ControlStartState getStartControlState() {
        return startControlState;
    }
    public void setStartControlState(ControlStartState startControlState) {
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
        ControlStartState startControlState = latchingGear.getStartControlState() == 0 ? ControlStartState.Open : ControlStartState.Close;
        setStartControlState(startControlState);

    }

     @Override
     public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        LatchingGear latchingGear = (LatchingGear) programDirectGear;
        latchingGear.setPercentReduction(getCapacityReduction());
        Integer startControlState = getStartControlState() == ControlStartState.Open ? 0 : 1;
        latchingGear.setStartControlState(startControlState);
    }

}
