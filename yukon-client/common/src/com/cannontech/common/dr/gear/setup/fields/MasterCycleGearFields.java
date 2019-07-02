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
    private Integer cyclePeriod;
    private GroupSelectionMethod groupSelectionMethod;

    private Integer rampInPercent;
    private Integer rampInInterval;

    private HowToStopControl howToStopControl;
    private StopOrder stopOrder;
    private Integer rampOutPercent;
    private Integer rampOutInterval;

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

    public Integer getRampOutPercent() {
        return rampOutPercent;
    }

    public void setRampOutPercent(Integer rampOutPercent) {
        this.rampOutPercent = rampOutPercent;
    }

    public Integer getRampOutInterval() {
        return rampOutInterval;
    }

    public void setRampOutInterval(Integer rampOutInterval) {
        this.rampOutInterval = rampOutInterval;
    }

    public Integer getRampInPercent() {
        return rampInPercent;
    }

    public void setRampInPercent(Integer rampInPercent) {
        this.rampInPercent = rampInPercent;
    }

    public Integer getRampInInterval() {
        return rampInInterval;
    }

    public void setRampInInterval(Integer rampInInterval) {
        this.rampInInterval = rampInInterval;
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
            setStopOrder(StopOrder.FirstInFirstOut);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutInterval(masterCycleGear.getRampOutInterval());
        } else if (masterCycleGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM) == 0) {
            setHowToStopControl(HowToStopControl.RampOutTimeIn);
            setStopOrder(StopOrder.Random);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutInterval(masterCycleGear.getRampOutInterval());
        } else if (masterCycleGear.getMethodStopType().compareTo(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE) == 0) {
            setHowToStopControl(HowToStopControl.RampOutRestore);
            setStopOrder(StopOrder.FirstInFirstOut);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutInterval(masterCycleGear.getRampOutInterval());
        } else if (lmProgramDirectGear.getMethodStopType().compareTo(
            LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE) == 0) {
            setHowToStopControl(HowToStopControl.RampOutRestore);
            setStopOrder(StopOrder.Random);
            setRampOutPercent(masterCycleGear.getRampOutPercent());
            setRampOutInterval(masterCycleGear.getRampOutInterval());
        } else {
            setHowToStopControl(HowToStopControl.valueOf(masterCycleGear.getMethodStopType()));
        }

        if (masterCycleGear.getRampInPercent().intValue() != 0 && masterCycleGear.getRampInInterval().intValue() != 0) {
            setRampInInterval(masterCycleGear.getRampInInterval());
            setRampInPercent(masterCycleGear.getRampInPercent());
        }

        setCapacityReduction(masterCycleGear.getPercentReduction());
        setControlPercent(masterCycleGear.getControlPercent());
        setCyclePeriod(masterCycleGear.getCyclePeriodLength() / 60);
        setGroupSelectionMethod(GroupSelectionMethod.valueOf(masterCycleGear.getGroupSelectionMethod()));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(lmProgramDirectGear);
        setWhenToChangeFields(whenToChangeFields);
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {
        MasterCycleGear masterCycleGear = (MasterCycleGear) programDirectGear;

        if (getHowToStopControl() == HowToStopControl.RampOutTimeIn) {
            if (getStopOrder() == StopOrder.FirstInFirstOut) {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO);
            } else {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM);
            }
            masterCycleGear.setRampOutPercent(getRampOutPercent());
            Integer interval = getRampOutInterval();
            if (interval != null) {
                masterCycleGear.setRampOutInterval(interval);

            } else {
                masterCycleGear.setRampOutInterval(0);
            }
        } else if (getHowToStopControl() == HowToStopControl.RampOutRestore) {
            if (getStopOrder() == StopOrder.FirstInFirstOut) {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_FIFO_RESTORE);
            } else {
                masterCycleGear.setMethodStopType(LMProgramDirectGear.STOP_RAMP_OUT_RANDOM_RESTORE);
            }
            masterCycleGear.setRampOutPercent(getRampOutPercent());
            Integer interval = getRampOutInterval();
            if (interval != null) {
                masterCycleGear.setRampOutInterval(interval);
            } else {
                masterCycleGear.setRampOutInterval(0);
            }
        } else {
            masterCycleGear.setMethodStopType(getHowToStopControl().name());
        }

        if (getRampInPercent() != null && getRampInInterval() != null) {
            masterCycleGear.setRampInPercent(getRampInPercent());
            masterCycleGear.setRampInInterval(getRampInInterval());
        }

        masterCycleGear.setPercentReduction(getCapacityReduction());
        masterCycleGear.setControlPercent(getControlPercent());
        masterCycleGear.setCyclePeriodLength(getControlPercent() * 60);
        masterCycleGear.setGroupSelectionMethod(getGroupSelectionMethod().name());

        whenToChangeFields.buildDBPersistent(programDirectGear);
    }

}
