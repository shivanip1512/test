package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.HoneywellCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class HoneywellCycleGearFields implements ProgramGearFields {

    private Boolean rampInOut;
    private Integer controlPercent;
    private Integer cyclePeriod;

    private HowToStopControl howToStopControl;
    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

    public Integer getControlPercent() {
        return controlPercent;
    }

    public void setControlPercent(Integer controlPercent) {
        this.controlPercent = controlPercent;
    }

    public Integer getCyclePeriod() {
        return cyclePeriod;
    }

    public void setCyclePeriod(Integer cyclePeriod) {
        this.cyclePeriod = cyclePeriod;
    }

    public Boolean getRampInOut() {
        return rampInOut;
    }

    public void setRampInOut(Boolean rampInOut) {
        this.rampInOut = rampInOut;
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
    public void buildModel(LMProgramDirectGear gear) {
        HoneywellCycleGear honeywellCycleGear = (HoneywellCycleGear) gear;

        setHowToStopControl(HowToStopControl.valueOf(honeywellCycleGear.getMethodStopType()));
        setControlPercent(honeywellCycleGear.getControlPercent());
        setCapacityReduction(honeywellCycleGear.getPercentReduction());
        setCyclePeriod(honeywellCycleGear.getCyclePeriod());
        setRampInOut(honeywellCycleGear.isFrontRampEnabled());

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(gear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        HoneywellCycleGear honeywellCycleGear = (HoneywellCycleGear) programDirectGear;

        honeywellCycleGear.setMethodStopType(getHowToStopControl().name());
        honeywellCycleGear.setPercentReduction(getCapacityReduction());
        honeywellCycleGear.setControlPercent(getControlPercent());
        honeywellCycleGear.setFrontRampEnabled(getRampInOut());
        honeywellCycleGear.setCyclePeriod(getCyclePeriod());

        whenToChangeFields.buildDBPersistent(honeywellCycleGear);

    }

}
