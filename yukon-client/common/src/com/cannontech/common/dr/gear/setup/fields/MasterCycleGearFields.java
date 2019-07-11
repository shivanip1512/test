package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.common.dr.gear.setup.StopOrder;
import com.cannontech.database.data.device.lm.MasterCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MasterCycleGearFields implements ProgramGearFields {

    private Integer controlPercent;
    private Integer cyclePeriodInMinutes;
    private GroupSelectionMethod groupSelectionMethod;

    private Integer rampInPercent;
    private Integer rampInIntervalInSeconds;

    private HowToStopControl howToStopControl;
    private StopOrder stopOrder;
    private Integer rampOutPercent;
    private Integer rampOutIntervalInSeconds;;

    private Integer capacityReduction;

    private WhenToChangeFields whenToChangeFields;

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

    public GroupSelectionMethod getGroupSelectionMethod() {
        return groupSelectionMethod;
    }

    public void setGroupSelectionMethod(GroupSelectionMethod groupSelectionMethod) {
        this.groupSelectionMethod = groupSelectionMethod;
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

    @Override
    public void buildModel(LMProgramDirectGear lmProgramDirectGear) {

        MasterCycleGear masterCycleGear = (MasterCycleGear) lmProgramDirectGear;

        if (masterCycleGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO) == 0) {
            setHowToStopControl(HowToStopControl.RampOutTimeIn);
            setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutIntervalInSeconds(masterCycleGear.getRampOutInterval());
        } else if (masterCycleGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM) == 0) {
            setHowToStopControl(HowToStopControl.RampOutTimeIn);
            setStopOrder(StopOrder.RANDOM);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutIntervalInSeconds(masterCycleGear.getRampOutInterval());
        } else if (masterCycleGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE) == 0) {
            setHowToStopControl(HowToStopControl.RampOutRestore);
            setStopOrder(StopOrder.FIRSTINFIRSTOUT);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutIntervalInSeconds(masterCycleGear.getRampOutInterval());
        } else if (lmProgramDirectGear.getMethodStopType().compareTo(
            LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE) == 0) {
            setHowToStopControl(HowToStopControl.RampOutRestore);
            setStopOrder(StopOrder.RANDOM);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutIntervalInSeconds(masterCycleGear.getRampOutInterval());
        } else {
            setHowToStopControl(HowToStopControl.valueOf(masterCycleGear.getMethodStopType()));
        }

        if (masterCycleGear.getRampInPercent().intValue() != 0 && masterCycleGear.getRampInInterval().intValue() != 0) {
            setRampInIntervalInSeconds(masterCycleGear.getRampInInterval());
            setRampInPercent(masterCycleGear.getRampInPercent());
        }

        setCapacityReduction(masterCycleGear.getPercentReduction());
        setControlPercent(masterCycleGear.getControlPercent());
        setCyclePeriodInMinutes(masterCycleGear.getCyclePeriodLength() / 60);
        setGroupSelectionMethod(GroupSelectionMethod.valueOf(masterCycleGear.getGroupSelectionMethod()));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(lmProgramDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        MasterCycleGear masterCycleGear = (MasterCycleGear) programDirectGear;

        if (getHowToStopControl() == HowToStopControl.RampOutTimeIn) {
            if (getStopOrder() == StopOrder.FIRSTINFIRSTOUT) {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO);
            } else {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM);
            }
            masterCycleGear.setRampOutPercent(getRampOutPercent());
            Integer interval = getRampOutIntervalInSeconds();
            if (interval != null) {
                masterCycleGear.setRampOutInterval(interval);

            } else {
                masterCycleGear.setRampOutInterval(0);
            }
        } else if (getHowToStopControl() == HowToStopControl.RampOutRestore) {
            if (getStopOrder() == StopOrder.FIRSTINFIRSTOUT) {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE);
            } else {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE);
            }
            masterCycleGear.setRampOutPercent(getRampOutPercent());
            Integer interval = getRampOutIntervalInSeconds();
            if (interval != null) {
                masterCycleGear.setRampOutInterval(interval);
            } else {
                masterCycleGear.setRampOutInterval(0);
            }
        } else {
            masterCycleGear.setMethodStopType(getHowToStopControl().name());
        }

        if (getRampInPercent() != null && getRampInIntervalInSeconds() != null) {
            masterCycleGear.setRampInPercent(getRampInPercent());
            masterCycleGear.setRampInInterval(getRampInIntervalInSeconds());
        }

        masterCycleGear.setPercentReduction(getCapacityReduction());
        masterCycleGear.setControlPercent(getControlPercent());
        masterCycleGear.setCyclePeriodLength(getCyclePeriodInMinutes() * 60);
        masterCycleGear.setGroupSelectionMethod(getGroupSelectionMethod().name());

        whenToChangeFields.buildDBPersistent(programDirectGear);
    }

}
