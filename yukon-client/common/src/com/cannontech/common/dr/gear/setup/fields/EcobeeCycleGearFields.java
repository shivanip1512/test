package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.EcobeeCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EcobeeCycleGearFields implements ProgramGearFields {

    private Boolean mandatory;
    private Integer controlPercent;
    private HowToStopControl howToStopControl;
    private Boolean rampInOut;

    private Integer capacityReduction;
    private WhenToChangeFields whenToChangeFields;

    public Integer getControlPercent() {
        return controlPercent;
    }

    public void setControlPercent(Integer controlPercent) {
        this.controlPercent = controlPercent;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
    public Boolean getRampInOut() {
        return rampInOut;
    }

    public void setRampInOut(Boolean rampInOut) {
        this.rampInOut = rampInOut;
    }
    
    public Integer getCapacityReduction() {
        return capacityReduction;
    }

    public void setCapacityReduction(Integer capacityReduction) {
        this.capacityReduction = capacityReduction;
    }

    public HowToStopControl getHowToStopControl() {
        return howToStopControl;
    }

    public void setHowToStopControl(HowToStopControl howToStopControl) {
        this.howToStopControl = howToStopControl;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        EcobeeCycleGear ecobeeCycleGear = (EcobeeCycleGear) programDirectGear;

        setHowToStopControl(HowToStopControl.valueOf(ecobeeCycleGear.getMethodStopType()));
        setCapacityReduction(ecobeeCycleGear.getPercentReduction());
        setControlPercent(ecobeeCycleGear.getControlCyclePercent());
        setMandatory(ecobeeCycleGear.isMandatorySelected(ecobeeCycleGear.getMethodOptionType()));
        setRampInOut(ecobeeCycleGear.isFrontRampEnabled());

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        EcobeeCycleGear ecobeeCycleGear = (EcobeeCycleGear) programDirectGear;

        ecobeeCycleGear.setMethodStopType(getHowToStopControl().name());
        ecobeeCycleGear.setPercentReduction(getCapacityReduction());
        ecobeeCycleGear.setControlCyclePercent(getControlPercent());
        ecobeeCycleGear.setFrontRampEnabled(getRampInOut());
        ecobeeCycleGear.setMethodOptionType(getMandatory());
        
        whenToChangeFields.buildDBPersistent(ecobeeCycleGear);

    }

}
