package com.cannontech.cc.model;

import java.util.Date;

public abstract class BaseEvent implements Comparable<BaseEvent> {

    public abstract String getDisplayName();
    public abstract Integer getId();
    public abstract Date getStartTime();
    public abstract Program getProgram();
    public abstract Date getStopTime();
    public abstract Date getNotificationTime();
    
    public int compareTo(BaseEvent o) {
        return getStartTime().compareTo(o.getStartTime());
    }
    /**
     * @return event duration in minutes
     */
    public Integer getDuration() {
        long startMillis = getStartTime().getTime();
        long stopMillis = getStopTime().getTime();
        long diffMillis = stopMillis - startMillis;
        return (int) (diffMillis / (60 * 1000));
    }
}
