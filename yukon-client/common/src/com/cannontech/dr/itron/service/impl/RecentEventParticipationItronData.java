package com.cannontech.dr.itron.service.impl;

public class RecentEventParticipationItronData {
    private int programId;
    private long eventId;
    
    public RecentEventParticipationItronData(int programId, long eventId) {
        this.setProgramId(programId);
        this.setEventId(eventId);
    }

    public int getProgramId() {
        return programId;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

}
