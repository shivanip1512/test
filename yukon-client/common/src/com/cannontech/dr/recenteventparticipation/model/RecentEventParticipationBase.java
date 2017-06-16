package com.cannontech.dr.recenteventparticipation.model;

import org.joda.time.Instant;

public abstract class RecentEventParticipationBase {
    private final int controlEventId;
    private final String programName;
    private final String groupName;
    private final Instant startTime;

    public RecentEventParticipationBase(int controlEventId, String programName, String groupName, Instant startTime) {
        this.controlEventId = controlEventId;
        this.programName = programName;
        this.groupName = groupName;
        this.startTime = startTime;
    }

    public String getProgramName() {
        return programName;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getControlEventId() {
        return controlEventId;
    }

}
