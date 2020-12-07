package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.EatonCloudCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.dr.eatonCloud.model.EatonCloudCycleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EatonCloudCycleGearFields implements ProgramGearFields{

    private EatonCloudCycleType dutyCycleType;
    private Integer dutyCyclePercent;
    private Integer dutyCyclePeriodInMinutes;
    private Integer criticality;
    private HowToStopControl howToStopControl;
    private Boolean rampIn;
    private Boolean rampOut;

    private Integer capacityReduction;
    private WhenToChangeFields whenToChangeFields;

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }

    public EatonCloudCycleType getDutyCycleType() {
        return dutyCycleType;
    }

    public void setDutyCycleType(EatonCloudCycleType dutyCycleType) {
        this.dutyCycleType = dutyCycleType;
    }

    public Integer getDutyCyclePeriodInMinutes() {
        return dutyCyclePeriodInMinutes;
    }

    public void setDutyCyclePeriodInMinutes(Integer dutyCyclePeriodInMinutes) {
        this.dutyCyclePeriodInMinutes = dutyCyclePeriodInMinutes;
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
        EatonCloudCycleGear eatonCloudCycleGear = (EatonCloudCycleGear) programDirectGear;

        setHowToStopControl(HowToStopControl.valueOf(eatonCloudCycleGear.getMethodStopType()));
        setCapacityReduction(eatonCloudCycleGear.getPercentReduction());
        setDutyCyclePercent(eatonCloudCycleGear.getControlPercent());
        setRampIn(eatonCloudCycleGear.isFrontRampEnabled());
        setRampOut(eatonCloudCycleGear.isBackRampEnabled());
        setCriticality(eatonCloudCycleGear.getCriticality());
        setDutyCyclePeriodInMinutes(eatonCloudCycleGear.getCyclePeriod());
        setDutyCycleType(EatonCloudCycleType.of(eatonCloudCycleGear.getCycleType()));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        EatonCloudCycleGear eatonCloudCycleGear = (EatonCloudCycleGear) programDirectGear;

        eatonCloudCycleGear.setMethodStopType(getHowToStopControl().name());
        eatonCloudCycleGear.setPercentReduction(getCapacityReduction());
        eatonCloudCycleGear.setControlPercent(getDutyCyclePercent());
        eatonCloudCycleGear.setFrontRampEnabled(getRampIn());
        eatonCloudCycleGear.setBackRampEnabled(getRampOut());
        eatonCloudCycleGear.setCriticality(getCriticality());
        eatonCloudCycleGear.setCyclePeriod(getDutyCyclePeriodInMinutes());
        eatonCloudCycleGear.setCycleType(getDutyCycleType().toString());

        whenToChangeFields.buildDBPersistent(programDirectGear);
        
    }
}
