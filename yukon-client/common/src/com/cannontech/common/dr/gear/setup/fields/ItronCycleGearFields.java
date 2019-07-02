package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.ItronCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.dr.itron.model.ItronCycleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ItronCycleGearFields implements ProgramGearFields {

    private ItronCycleType cycleType;
    private Boolean rampIn;
    private Boolean rampOut;
    private Integer dutyCyclePercent;
    private Integer dutyCyclePeriod;
    private Integer criticality;
    private Integer capacityReduction;
    private HowToStopControl howToStopControl;
    private WhenToChangeFields whenToChangeFields;

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }

    public ItronCycleType getCycleType() {
        return cycleType;
    }

    public void setCycleType(ItronCycleType cycleType) {
        this.cycleType = cycleType;
    }

    public Integer getDutyCyclePeriod() {
        return dutyCyclePeriod;
    }

    public void setDutyCyclePeriod(Integer dutyCyclePeriod) {
        this.dutyCyclePeriod = dutyCyclePeriod;
    }

    public Integer getDutyCyclePercent() {
        return dutyCyclePercent;
    }

    public void setDutyCyclePercent(Integer dutyCyclePercent) {
        this.dutyCyclePercent = dutyCyclePercent;
    }

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
    public void buildModel(LMProgramDirectGear programDirectGear) {
        ItronCycleGear itronCycleGear = (ItronCycleGear) programDirectGear;

        setHowToStopControl(HowToStopControl.valueOf(itronCycleGear.getMethodStopType()));
        setCapacityReduction(itronCycleGear.getPercentReduction());
        setDutyCyclePercent(itronCycleGear.getControlPercent());
        setRampIn(itronCycleGear.isFrontRampEnabled());
        setRampOut(itronCycleGear.isBackRampEnabled());
        setCriticality(itronCycleGear.getCriticality());
        setDutyCyclePeriod(itronCycleGear.getCyclePeriod());
        setCycleType(ItronCycleType.of(itronCycleGear.getCycleType()));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        ItronCycleGear itronCycleGear = (ItronCycleGear) programDirectGear;

        itronCycleGear.setMethodStopType(getHowToStopControl().name());
        itronCycleGear.setPercentReduction(getCapacityReduction());
        itronCycleGear.setControlPercent(getDutyCyclePercent());
        itronCycleGear.setFrontRampEnabled(getRampIn());
        itronCycleGear.setBackRampEnabled(getRampOut());
        itronCycleGear.setCriticality(getCriticality());
        itronCycleGear.setCyclePeriod(getDutyCyclePeriod());
        itronCycleGear.setCycleType(getCycleType().toString());

        whenToChangeFields.buildDBPersistent(programDirectGear);
        
    }

}
