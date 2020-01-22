package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.Mode;
import com.cannontech.database.data.device.lm.HeatCool;
import com.cannontech.database.data.device.lm.HoneywellSetpointGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class HoneywellSetpointGearFields implements ProgramGearFields {
    private Boolean mandatory;
    private Integer setpointOffset;
    private Integer precoolOffset;
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
    
    public Integer getPrecoolOffset() {
        return precoolOffset;
    }
    
    public void setPrecoolOffset(Integer precoolOffset) {
        this.precoolOffset = precoolOffset;
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
        HoneywellSetpointGear honeywellSetpointGear = (HoneywellSetpointGear) programDirectGear;
        
        setHowToStopControl(HowToStopControl.valueOf(honeywellSetpointGear.getMethodStopType()));
        setCapacityReduction(honeywellSetpointGear.getPercentReduction());
        setMandatory(honeywellSetpointGear.isMandatorySelected(honeywellSetpointGear.getMethodOptionType()));
        setSetpointOffset(honeywellSetpointGear.getSetpointOffset());
        setPrecoolOffset(honeywellSetpointGear.getPrecoolOffset());
        setMode(honeywellSetpointGear.getHeatCool().getMode());
    }
    
    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        HoneywellSetpointGear honeywellSetpointGear = (HoneywellSetpointGear) programDirectGear;
        
        honeywellSetpointGear.setMethodStopType(getHowToStopControl().name());
        honeywellSetpointGear.setPercentReduction(getCapacityReduction());
        honeywellSetpointGear.setMethodOptionType(getMandatory());
        honeywellSetpointGear.setSetpointOffset(getSetpointOffset());
        honeywellSetpointGear.setPrecoolOffset(getPrecoolOffset());
        honeywellSetpointGear.setHeatCool(HeatCool.fromMode(getMode()));
        
        whenToChangeFields.buildDBPersistent(honeywellSetpointGear);
    }
}
