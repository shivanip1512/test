package com.cannontech.loadcontrol.messages;

import java.io.Serializable;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.model.EatonCloudCycleType;

public class LMEatonCloudScheduledCycleCommand implements Serializable {
    private Integer groupId;
    private Instant controlStartDateTime;
    private Instant controlEndDateTime;
    private Boolean isRampIn;
    private Boolean isRampOut;
    private EatonCloudCycleType cyclingOption;
    private Integer dutyCyclePercentage;
    private Integer dutyCyclePeriod;
    private Integer criticality;

    @Override
    public String toString() {
        return String.format(
                "LMEatonCloudScheduledCycleCommand [groupId=%s, controlStartDateTime=%s, controlEndDateTime=%s, isRampIn=%s, isRampOut=%s, cyclingOption=%s, dutyCyclePercentage=%s, dutyCyclePeriod=%s, criticality=%s]",
                groupId, controlStartDateTime, controlEndDateTime, isRampIn, isRampOut, cyclingOption, dutyCyclePercentage,
                dutyCyclePeriod, criticality);
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Instant getControlStartDateTime() {
        return controlStartDateTime;
    }

    public void setControlStartDateTime(Instant controlStartDateTime) {
        this.controlStartDateTime = controlStartDateTime;
    }

    public Instant getControlEndDateTime() {
        return controlEndDateTime;
    }

    public void setControlEndDateTime(Instant controlEndDateTime) {
        this.controlEndDateTime = controlEndDateTime;
    }

    public Boolean getIsRampIn() {
        return isRampIn;
    }

    public void setIsRampIn(Boolean isRampIn) {
        this.isRampIn = isRampIn;
    }

    public Boolean getIsRampOut() {
        return isRampOut;
    }

    public void setIsRampOut(Boolean isRampOut) {
        this.isRampOut = isRampOut;
    }

    public EatonCloudCycleType getCyclingOption() {
        return cyclingOption;
    }

    public void setCyclingOption(EatonCloudCycleType cyclingOption) {
        this.cyclingOption = cyclingOption;
    }

    public Integer getDutyCyclePercentage() {
        return dutyCyclePercentage;
    }

    public void setDutyCyclePercentage(Integer dutyCyclePercentage) {
        this.dutyCyclePercentage = dutyCyclePercentage;
    }

    public Integer getDutyCyclePeriod() {
        return dutyCyclePeriod;
    }

    public void setDutyCyclePeriod(Integer dutyCyclePeriod) {
        this.dutyCyclePeriod = dutyCyclePeriod;
    }

    public Integer getCriticality() {
        return criticality;
    }

    public void setCriticality(Integer criticality) {
        this.criticality = criticality;
    }

}
