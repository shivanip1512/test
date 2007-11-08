package com.cannontech.jobs.model;

import java.util.Date;

public class ScheduledOneTimeJob extends YukonJob {
    private Date startTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
