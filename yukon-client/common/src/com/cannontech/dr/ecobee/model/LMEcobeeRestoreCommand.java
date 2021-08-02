package com.cannontech.dr.ecobee.model;

import java.io.Serializable;

import org.joda.time.Instant;

public class LMEcobeeRestoreCommand implements Serializable {
    private Integer groupId;
    private Instant restoreTime;

    public Integer getGroupId() {
        return groupId;
    }

    public Instant getRestoreTime() {
        return restoreTime;
    }

    public LMEcobeeRestoreCommand(Integer groupId, Instant restoreTime) {
        this.groupId = groupId;
        this.restoreTime = restoreTime;
    }

}
