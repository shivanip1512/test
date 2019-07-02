package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SepCycleGearFields implements ProgramGearFields {

    private Boolean rampIn;
    private Boolean rampOut;
    private Boolean trueCycle;

    private Integer controlPercent;
    private Integer criticality;
    private HowToStopControl howToStopControl;
    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

    public Boolean getRampIn() {
        return rampIn;
    }

    public void setRampIn(Boolean rampIn) {
        this.rampIn = rampIn;
    }

    public Boolean getRampOut() {
        return rampOut;
    }

    public void setRampOut(Boolean rampOut) {
        this.rampOut = rampOut;
    }

    public Boolean getTrueCycle() {
        return trueCycle;
    }

    public void setTrueCycle(Boolean trueCycle) {
        this.trueCycle = trueCycle;
    }

    public Integer getControlPercent() {
        return controlPercent;
    }

    public void setControlPercent(Integer controlPercent) {
        this.controlPercent = controlPercent;
    }

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }

    public HowToStopControl getHowToStopControl() {
        return howToStopControl;
    }

    public void setHowToStopControl(HowToStopControl howToStopControl) {
        this.howToStopControl = howToStopControl;
    }

    public Integer getCapacityReduction() {
        return capacityReduction;
    }

    public void setCapacityReduction(Integer capacityReduction) {
        this.capacityReduction = capacityReduction;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    @Override
    public void buildModel(LMProgramDirectGear lmProgramDirectGear) {

        SepCycleGear sepCycleGear = (SepCycleGear) lmProgramDirectGear;

        setHowToStopControl(HowToStopControl.valueOf(sepCycleGear.getMethodStopType()));
        setCapacityReduction(sepCycleGear.getPercentReduction());
        setControlPercent(sepCycleGear.getControlPercent());
        setCriticality(sepCycleGear.getCriticality());
        setRampIn(sepCycleGear.isFrontRampEnabled());
        setRampOut(sepCycleGear.isBackRampEnabled());
        setTrueCycle(sepCycleGear.isTrueCycleEnabled());   

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(lmProgramDirectGear);
        setWhenToChangeFields(whenToChangeFields);
        
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear lmProgramDirectGear) {
        SepCycleGear sepCycleGear = (SepCycleGear) lmProgramDirectGear;

        sepCycleGear.setMethodStopType(getHowToStopControl().name());
        sepCycleGear.setPercentReduction(getCapacityReduction());
        sepCycleGear.setControlPercent(getControlPercent());
        sepCycleGear.setCriticality(getCriticality());
        sepCycleGear.setFrontRampEnabled(getRampIn());
        sepCycleGear.setBackRampEnabled(getRampOut());
        sepCycleGear.setTrueCycleEnabled(getTrueCycle());

        whenToChangeFields.buildDBPersistent(lmProgramDirectGear);
        
    }


}
