package com.cannontech.common.dr.gear.setup.fields;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.data.device.lm.SmartCycleGear;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SmartCycleGearFields implements ProgramGearFields {

    private Boolean noRamp;
    private Integer controlPercent;
    private Integer cyclePeriodInMinutes;
    private CycleCountSendType cycleCountSendType;
    private Integer maxCycleCount;
    private Integer startingPeriodCount;

    private Integer sendRate;
    private Integer stopCommandRepeat;

    private HowToStopControl howToStopControl;
    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

    public SmartCycleGearFields() {
    }

    public Boolean getNoRamp() {
        return noRamp;
    }

    public void setNoRamp(Boolean noRamp) {
        this.noRamp = noRamp;
    }

    public Integer getControlPercent() {
        return controlPercent;
    }

    public void setControlPercent(Integer controlPercent) {
        this.controlPercent = controlPercent;
    }

    public Integer getCyclePeriodInMinutes() {
        return cyclePeriodInMinutes;
    }

    public void setCyclePeriodInMinutes(Integer cyclePeriodInMinutes) {
        this.cyclePeriodInMinutes = cyclePeriodInMinutes;
    }

    public CycleCountSendType getCycleCountSendType() {
        return cycleCountSendType;
    }

    public void setCycleCountSendType(CycleCountSendType cycleCountSendType) {
        this.cycleCountSendType = cycleCountSendType;
    }

    public Integer getMaxCycleCount() {
        return maxCycleCount;
    }

    public void setMaxCycleCount(Integer maxCycleCount) {
        this.maxCycleCount = maxCycleCount;
    }

    public Integer getStartingPeriodCount() {
        return startingPeriodCount;
    }

    public void setStartingPeriodCount(Integer startingPeriodCount) {
        this.startingPeriodCount = startingPeriodCount;
    }

    public Integer getSendRate() {
        return sendRate;
    }

    public void setSendRate(Integer sendRate) {
        this.sendRate = sendRate;
    }

    public Integer getStopCommandRepeat() {
        return stopCommandRepeat;
    }

    public void setStopCommandRepeat(Integer stopCommandRepeat) {
        this.stopCommandRepeat = stopCommandRepeat;
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
        SmartCycleGear smartCycleGear = (SmartCycleGear) lmProgramDirectGear;

        setCapacityReduction(smartCycleGear.getPercentReduction());
        setHowToStopControl(HowToStopControl.valueOf(smartCycleGear.getMethodStopType()));
        setControlPercent(smartCycleGear.getControlPercent());
        setStopCommandRepeat(smartCycleGear.getStopCommandRepeat());
        setCyclePeriodInMinutes(smartCycleGear.getCyclePeriodLength() / 60);
        setStartingPeriodCount(smartCycleGear.getStartingPeriodCnt());
        setSendRate(smartCycleGear.getResendRate());

        if (smartCycleGear.getMethodOptionMax().intValue() > 0) {
            setMaxCycleCount(smartCycleGear.getMethodOptionMax());
        } else {
            setMaxCycleCount(0);
        }
        setCycleCountSendType(CycleCountSendType.valueOf(smartCycleGear.getMethodOptionType()));

        if (smartCycleGear.getFrontRampOption().compareTo(CtiUtilities.STRING_NONE) != 0) {
            setNoRamp(true);
        } else {
            setNoRamp(false);
        }

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(lmProgramDirectGear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear lmProgramDirectGear) {
        SmartCycleGear smartCycleGear = (SmartCycleGear) lmProgramDirectGear;

        smartCycleGear.setMethodStopType(getHowToStopControl().name());
        smartCycleGear.setPercentReduction(getCapacityReduction());

        if ((lmProgramDirectGear.getControlMethod() == GearControlMethod.SmartCycle)
            || (lmProgramDirectGear.getControlMethod() == GearControlMethod.TrueCycle)) {
            smartCycleGear.setStopCommandRepeat(getStopCommandRepeat());
        } else {
            smartCycleGear.setStopCommandRepeat(0);
        }

        smartCycleGear.setControlPercent(getControlPercent());
        smartCycleGear.setCyclePeriodLength(getCyclePeriodInMinutes() * 60);
        smartCycleGear.setStartingPeriodCnt(getStartingPeriodCount());

        smartCycleGear.setResendRate(getSendRate());

        if (getMaxCycleCount() != null) {
            smartCycleGear.setMethodOptionMax(getMaxCycleCount());
        } else {
            smartCycleGear.setMethodOptionMax(0);
        }

        smartCycleGear.setMethodOptionType(getCycleCountSendType().name());

        if (getNoRamp()) {
            smartCycleGear.setFrontRampOption("NoRamp");
        }

        whenToChangeFields.buildDBPersistent(smartCycleGear);
    }

}
