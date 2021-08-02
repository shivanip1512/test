package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.EcobeePlusGear;
import com.cannontech.database.data.device.lm.HeatCool;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EcobeePlusGearFields implements ProgramGearFields {
    private Boolean rampInOut;
    private Boolean heatingEvent;
    private HowToStopControl howToStopControl;
    private Integer capacityReduction;
    private WhenToChangeFields whenToChangeFields;

    public Boolean getRampInOut() {
        return rampInOut;
    }

    public void setRampInOut(Boolean rampInOut) {
        this.rampInOut = rampInOut;
    }

    public Boolean getHeatingEvent() {
        return heatingEvent;
    }

    public void setHeatingEvent(Boolean heatingEvent) {
        this.heatingEvent = heatingEvent;
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
        EcobeePlusGear ecobeePlusGear = (EcobeePlusGear) programDirectGear;
        if (ecobeePlusGear.getRandomTimeSeconds() > 0) {
            setRampInOut(true);
        } else {
            setRampInOut(false);
        }
        if (ecobeePlusGear.getHeatCool() == HeatCool.HEAT) {
            setHeatingEvent(true);
        } else {
            setHeatingEvent(false);
        }
        setHowToStopControl(HowToStopControl.valueOf(ecobeePlusGear.getMethodStopType()));
        setCapacityReduction(ecobeePlusGear.getPercentReduction());

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        EcobeePlusGear ecobeePlusGear = (EcobeePlusGear) programDirectGear;
        if (rampInOut) {
            ecobeePlusGear.setRandomTimeSeconds(1800);
        }
        if (heatingEvent) {
            ecobeePlusGear.setHeatCool(HeatCool.HEAT);
        } else {
            ecobeePlusGear.setHeatCool(HeatCool.COOL);
        }

        ecobeePlusGear.setMethodStopType(getHowToStopControl().name());
        ecobeePlusGear.setPercentReduction(getCapacityReduction());

        whenToChangeFields.buildDBPersistent(ecobeePlusGear);
    }
}
