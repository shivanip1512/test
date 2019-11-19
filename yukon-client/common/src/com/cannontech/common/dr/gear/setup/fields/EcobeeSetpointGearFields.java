package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.database.data.device.lm.EcobeeSetpointGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EcobeeSetpointGearFields implements ProgramGearFields {
    private Boolean mandatory;
    private Integer setpointOffset;
    private Mode mode;
    
    private HowToStopControl howToStopControl;
    private Integer capacityReduction;
    
    private WhenToChangeFields whenToChangeFields;
    
    public Integer getSetpointOffset() {
        return setpointOffset;
    }
    
    public void setSetpointOffset(Integer setpointOffset) {
        this.setpointOffset = setpointOffset;
    }
    
    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    public Mode getMode() {
        return mode;
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
        EcobeeSetpointGear ecobeeSetpointGear = (EcobeeSetpointGear) programDirectGear;
        
        setHowToStopControl(HowToStopControl.valueOf(ecobeeSetpointGear.getMethodStopType()));
        setCapacityReduction(ecobeeSetpointGear.getPercentReduction());
        setMandatory(ecobeeSetpointGear.isMandatorySelected(ecobeeSetpointGear.getMethodOptionType()));
        setSetpointOffset(ecobeeSetpointGear.getSetpointOffset());
        setMode(ecobeeSetpointGear.getHeatCool().getMode());
        
        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }
    
    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        EcobeeSetpointGear ecobeeSetpointGear = (EcobeeSetpointGear) programDirectGear;
        
        ecobeeSetpointGear.setMethodStopType(getHowToStopControl().name());
        ecobeeSetpointGear.setPercentReduction(getCapacityReduction());
        ecobeeSetpointGear.setMethodOptionType(getMandatory());
        ecobeeSetpointGear.setSetpointOffset(getSetpointOffset());
        ecobeeSetpointGear.setHeatCool(EcobeeSetpointGear.HeatCool.fromMode(getMode()));
        
        whenToChangeFields.buildDBPersistent(ecobeeSetpointGear);
    }
}
