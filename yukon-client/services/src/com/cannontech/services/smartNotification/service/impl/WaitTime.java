package com.cannontech.services.smartNotification.service.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Instant;

public class WaitTime {
    
    private int interval;
    private Instant runTime;
    
    public Instant getRunTime() {
        return runTime;
    }
    
    public int getCurrentInterval() {
        return interval;
    }

    public WaitTime(Instant runTime, int interval) {
        this.runTime = runTime;
        this.interval = interval;
    }
    
    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.appendSuper(super.toString());
        tsb.append("interval", interval);
        tsb.append("runTime", runTime.toDateTime().toString("MM-dd-yyyy HH:mm:ss.SSS"));
        return tsb.toString();
    }
}
