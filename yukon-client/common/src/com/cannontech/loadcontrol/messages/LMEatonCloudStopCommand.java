package com.cannontech.loadcontrol.messages;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.model.EatonCloudStopType;

public class LMEatonCloudStopCommand implements Serializable {
    private Integer groupId;
    private Instant restoreTime;
    private EatonCloudStopType stopType;
    private Integer virtualRelayId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Instant getRestoreTime() {
        return restoreTime;
    }

    public void setRestoreTime(Instant restoreTime) {
        this.restoreTime = restoreTime;
    }

    public EatonCloudStopType getStopType() {
        return stopType;
    }

    public void setStopType(EatonCloudStopType stopType) {
        this.stopType = stopType;
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
