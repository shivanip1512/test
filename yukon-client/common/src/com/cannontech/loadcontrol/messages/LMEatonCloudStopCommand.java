package com.cannontech.loadcontrol.messages;

import java.io.Serializable;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.model.EatonCloudStopType;

public class LMEatonCloudStopCommand implements Serializable {
    private Integer groupId;
    private Instant restoreTime;
    private EatonCloudStopType stopType;

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

    @Override
    public String toString() {
        return String.format("LMEatonCloudStopCommand [groupId=%s, restoreTime=%s, stopType=%s]", groupId, restoreTime, stopType);
    }
}
