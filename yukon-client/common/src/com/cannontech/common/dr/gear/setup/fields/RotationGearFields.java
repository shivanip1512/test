package com.cannontech.common.dr.gear.setup.fields;

import com.cannontech.common.dr.gear.setup.GroupSelectionMethod;
import com.cannontech.common.dr.gear.setup.HowToStopControl;
import com.cannontech.database.data.device.lm.RotationGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RotationGearFields implements ProgramGearFields {

    private Integer shedTime;
    private Integer numberOfGroups;
    private Integer sendRate;
    private GroupSelectionMethod groupSelectionMethod;
    private HowToStopControl howToStopControl;
    
    private Integer capacityReduction;
    private WhenToChangeFields whenToChangeFields;

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
        RotationGear rotationGear = (RotationGear) programDirectGear;

        setHowToStopControl(HowToStopControl.valueOf(rotationGear.getMethodStopType()));
        setCapacityReduction(rotationGear.getPercentReduction());
        setShedTime(rotationGear.getShedTime());
        setNumberOfGroups(rotationGear.getNumberOfGroups());
        setSendRate(rotationGear.getSendRate());
        setGroupSelectionMethod(GroupSelectionMethod.valueOf(rotationGear.getGroupSelectionMethod()));

        WhenToChangeFields whenToChangeFields = new WhenToChangeFields();
        whenToChangeFields.buildModel(programDirectGear);
        setWhenToChangeFields(whenToChangeFields);

    }

    @Override
    public void buildDBPersistent(LMProgramDirectGear programDirectGear) {

        RotationGear rotationGear = (RotationGear) programDirectGear;

        rotationGear.setMethodStopType(getHowToStopControl().name());
        rotationGear.setPercentReduction(getCapacityReduction());
        rotationGear.setShedTime(getShedTime());
        rotationGear.setNumberOfGroups(getNumberOfGroups());
        rotationGear.setSendRate(getSendRate());
        rotationGear.setGroupSelectionMethod(getGroupSelectionMethod().name());

        whenToChangeFields.buildDBPersistent(programDirectGear);

    }

}
