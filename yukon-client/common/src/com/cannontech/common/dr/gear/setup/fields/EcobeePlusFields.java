package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.database.data.device.lm.EcobeeSetpointGear;
import com.cannontech.database.data.device.lm.HeatCool;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EcobeePlusFields implements ProgramGearFields {
    private HowToStopControl howToStopControl;
    private Integer capacityReduction;
    private WhenToChangeFields whenToChangeFields;
    private Boolean rampInOut;
    private Boolean heatingEvent;

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
    
   @Override
    public void buildModel(LMProgramDirectGear programDirectGear) {
        EcobeeSetpointGear ecobeeSetpointGear = (EcobeeSetpointGear) programDirectGear;
        
        setHowToStopControl(HowToStopControl.valueOf(ecobeeSetpointGear.getMethodStopType()));
        setCapacityReduction(ecobeeSetpointGear.getPercentReduction());
        
        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }
    
    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        EcobeeSetpointGear ecobeeSetpointGear = (EcobeeSetpointGear) programDirectGear;
        
        ecobeeSetpointGear.setMethodStopType(getHowToStopControl().name());
        ecobeeSetpointGear.setPercentReduction(getCapacityReduction());
       
        whenToChangeFields.buildDBPersistent(ecobeeSetpointGear);
    }
}
