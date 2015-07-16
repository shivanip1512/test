package com.cannontech.cc.model;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class BaseEvent implements Comparable<BaseEvent> {

    final public String getDisplayName() {
        return getProgram().getIdentifierPrefix() + getIdentifier();
    }
    public abstract Integer getId();
    public abstract Date getStartTime();
    public abstract Program getProgram();
    public abstract Date getStopTime();
    public abstract Date getNotificationTime();
    public abstract Integer getIdentifier();
    public abstract String getStateDescription();
    
    @Override
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
    
    /**
     * Compares events by event id and program id
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof BaseEvent) {
            BaseEvent rhs = (BaseEvent) obj;
            return new EqualsBuilder().append(getProgram().getId(), rhs.getProgram().getId())
                                      .append(getIdentifier(), rhs.getIdentifier())
                                      .isEquals();
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getProgram().getId()).append(getIdentifier()).toHashCode();
    }

}
