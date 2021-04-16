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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + interval;
        result = prime * result + ((runTime == null) ? 0 : runTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WaitTime other = (WaitTime) obj;
        if (interval != other.interval)
            return false;
        if (runTime == null) {
            if (other.runTime != null)
                return false;
        } else if (!runTime.equals(other.runTime))
            return false;
        return true;
    }

}
