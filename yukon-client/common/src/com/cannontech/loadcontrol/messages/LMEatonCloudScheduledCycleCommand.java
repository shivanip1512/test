package com.cannontech.loadcontrol.messages;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
    private Integer virtualRelayId;

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

    public Integer getVirtualRelayId() {
        return virtualRelayId;
    }

    public void setVirtualRelayId(Integer virtualRelayId) {
        this.virtualRelayId = virtualRelayId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
