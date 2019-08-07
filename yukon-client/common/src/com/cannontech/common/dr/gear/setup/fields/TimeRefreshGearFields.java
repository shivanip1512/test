package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.CycleCountSendType;
import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.StopOrder;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.database.data.device.lm.TimeRefreshGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class TimeRefreshGearFields implements ProgramGearFields {

    private CycleCountSendType refreshShedTime;
    private Integer shedTime;
    private Integer numberOfGroups;
    private Integer sendRate;

    private GroupSelectionMethod groupSelectionMethod;

    private Integer rampInPercent;
    private Integer rampInIntervalInSeconds;

    private HowToStopControl howToStopControl;
    private StopOrder stopOrder;

    private Integer rampOutPercent;
    private Integer rampOutIntervalInSeconds;

    private Integer stopCommandRepeat;
    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

    public Integer getShedTime() {
        return shedTime;
    }

    public void setShedTime(Integer shedTime) {
        this.shedTime = shedTime;
    }

    public Integer getNumberOfGroups() {
        return numberOfGroups;
    }

    public void setNumberOfGroups(Integer numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    public Integer getSendRate() {
        return sendRate;
    }

    public void setSendRate(Integer sendRate) {
        this.sendRate = sendRate;
    }

    public GroupSelectionMethod getGroupSelectionMethod() {
        return groupSelectionMethod;
    }

    public void setGroupSelectionMethod(GroupSelectionMethod groupSelectionMethod) {
        this.groupSelectionMethod = groupSelectionMethod;
    }

    public HowToStopControl getHowToStopControl() {
        return howToStopControl;
    }

    public void setHowToStopControl(HowToStopControl howToStopControl) {
        this.howToStopControl = howToStopControl;
    }

    public Integer getRampOutPercent() {
        return rampOutPercent;
    }

    public void setRampOutPercent(Integer rampOutPercent) {
        this.rampOutPercent = rampOutPercent;
    }

    public Integer getRampInPercent() {
        return rampInPercent;
    }

    public void setRampInPercent(Integer rampInPercent) {
        this.rampInPercent = rampInPercent;
    }

    public Integer getRampInIntervalInSeconds() {
        return rampInIntervalInSeconds;
    }

    public void setRampInIntervalInSeconds(Integer rampInIntervalInSeconds) {
        this.rampInIntervalInSeconds = rampInIntervalInSeconds;
    }

    public Integer getRampOutIntervalInSeconds() {
        return rampOutIntervalInSeconds;
    }

    public void setRampOutIntervalInSeconds(Integer rampOutIntervalInSeconds) {
        this.rampOutIntervalInSeconds = rampOutIntervalInSeconds;
    }

    public Integer getCapacityReduction() {
        return capacityReduction;
    }

    public void setCapacityReduction(Integer capacityReduction) {
        this.capacityReduction = capacityReduction;
    }

    public Integer getStopCommandRepeat() {
        return stopCommandRepeat;
    }

    public void setStopCommandRepeat(Integer stopCommandRepeat) {
        this.stopCommandRepeat = stopCommandRepeat;
    }

    public WhenToChangeFields getWhenToChangeFields() {
        return whenToChangeFields;
    }

    public void setWhenToChangeFields(WhenToChangeFields whenToChangeFields) {
        this.whenToChangeFields = whenToChangeFields;
    }

    public StopOrder getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(StopOrder stopOrder) {
        this.stopOrder = stopOrder;
    }

    public CycleCountSendType getRefreshShedTime() {
        return refreshShedTime;
    }

    public void setRefreshShedTime(CycleCountSendType refreshShedTime) {
        this.refreshShedTime = refreshShedTime;
    }

    @Override
    public void buildModel(LMProgramDirectGear lmProgramDirectGear) {
        TimeRefreshGear timeRefreshGear = (TimeRefreshGear) lmProgramDirectGear;

        if (timeRefreshGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO) == 0) {
            setHowToStopControl(HowToStopControl.RampOutTimeIn);
            setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            setRampOutPercent(timeRefreshGear.getRampOutPercent());
            setRampOutIntervalInSeconds(timeRefreshGear.getRampOutInterval());
        } else if (timeRefreshGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM) == 0) {
            setHowToStopControl(HowToStopControl.RampOutTimeIn);
            setStopOrder(StopOrder.RANDOM);
            setRampOutPercent(timeRefreshGear.getRampOutPercent());
            setRampOutIntervalInSeconds(timeRefreshGear.getRampOutInterval());
        } else if (timeRefreshGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE) == 0) {
            setHowToStopControl(HowToStopControl.RampOutRestore);
            setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            setRampOutPercent(timeRefreshGear.getRampOutPercent());
            setRampOutIntervalInSeconds(timeRefreshGear.getRampOutInterval());
        } else if (timeRefreshGear.getMethodStopType().compareTo(
            LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE) == 0) {
            setHowToStopControl(HowToStopControl.RampOutRestore);
            setStopOrder(StopOrder.RANDOM);
            setRampOutPercent(timeRefreshGear.getRampOutPercent());
            setRampOutIntervalInSeconds(timeRefreshGear.getRampOutInterval());
        } else {
            setHowToStopControl(HowToStopControl.valueOf(timeRefreshGear.getMethodStopType()));
        }

        if (timeRefreshGear.getRampInPercent().intValue() != 0 && timeRefreshGear.getRampInInterval().intValue() != 0) {
            setRampInIntervalInSeconds(timeRefreshGear.getRampInInterval());
            setRampInPercent(timeRefreshGear.getRampInPercent());
        }

        setCapacityReduction(timeRefreshGear.getPercentReduction());
        setStopCommandRepeat(timeRefreshGear.getStopCommandRepeat());
        setShedTime(timeRefreshGear.getShedTime());
        setNumberOfGroups(timeRefreshGear.getNumberOfGroups());

        if (timeRefreshGear.getMethodOptionType().compareTo(LMProgramDirectGear.OPTION_COUNT_DOWN) == 0) {
            setRefreshShedTime(CycleCountSendType.valueOf(LMProgramDirectGear.OPTION_DYNAMIC_SHED));
        } else {
            setRefreshShedTime(CycleCountSendType.valueOf(LMProgramDirectGear.OPTION_FIXED_SHED));
        }

        setSendRate(timeRefreshGear.getRefreshRate());
        setGroupSelectionMethod(GroupSelectionMethod.valueOf(timeRefreshGear.getGroupSelectionMethod()));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(lmProgramDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        TimeRefreshGear timeRefreshGear = (TimeRefreshGear) programDirectGear;
        if (getHowToStopControl() == HowToStopControl.RampOutTimeIn) {
            if (getStopOrder() == StopOrder.FIRSTINFIRSTOUT) {
                timeRefreshGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO);
            } else {
                timeRefreshGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM);
            }
            timeRefreshGear.setRampOutPercent(getRampOutPercent());
            Integer interval = getRampOutIntervalInSeconds();
            if (interval != null) {
                timeRefreshGear.setRampOutInterval(interval);

            } else {
                timeRefreshGear.setRampOutInterval(0);
            }
        } else if (getHowToStopControl() == HowToStopControl.RampOutRestore) {
            if (getStopOrder() == StopOrder.FIRSTINFIRSTOUT) {
                timeRefreshGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE);
            } else {
                timeRefreshGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE);
            }
            timeRefreshGear.setRampOutPercent(getRampOutPercent());
            Integer interval = getRampOutIntervalInSeconds();
            if (interval != null) {
                timeRefreshGear.setRampOutInterval(interval);
            } else {
                timeRefreshGear.setRampOutInterval(0);
            }
        } else {
            timeRefreshGear.setMethodStopType(getHowToStopControl().name());
        }

        if (getRampInPercent() != null && getRampInIntervalInSeconds() != null) {
            timeRefreshGear.setRampInInterval(getRampInIntervalInSeconds());
            timeRefreshGear.setRampInPercent(getRampInPercent());
        }

        if (getRefreshShedTime() == CycleCountSendType.DynamicShedTime) {
            timeRefreshGear.setMethodOptionType(LMProgramDirectGear.OPTION_COUNT_DOWN);
        } else {
            timeRefreshGear.setMethodOptionType(LMProgramDirectGear.OPTION_FIXED_COUNT);
        }

        timeRefreshGear.setPercentReduction(getCapacityReduction());
        timeRefreshGear.setStopCommandRepeat(getStopCommandRepeat());
        timeRefreshGear.setShedTime(getShedTime());
        timeRefreshGear.setNumberOfGroups(getNumberOfGroups());
        timeRefreshGear.setRefreshRate(getSendRate());
        timeRefreshGear.setGroupSelectionMethod(getGroupSelectionMethod().name());

        whenToChangeFields.buildDBPersistent(programDirectGear);

    }

}
