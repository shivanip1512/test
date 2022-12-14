package com.cannontech.dr.service.impl;

import org.joda.time.DateTime;

/**
 * Object representation of DR device relay state at a particular point in time, for calculating runtime.
 */
public class DatedRuntimeStatus extends DatedStatus {
    private final RuntimeStatus runtimeStatus;
    
    public DatedRuntimeStatus(RuntimeStatus runtimeStatus, DateTime date) {
        super(date);
        this.runtimeStatus = runtimeStatus;
    }
    
    @Override
    public boolean isActive() {
        return runtimeStatus == RuntimeStatus.RUNNING;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((runtimeStatus == null) ? 0 : runtimeStatus.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatedRuntimeStatus other = (DatedRuntimeStatus) obj;
        if (getDate() == null) {
            if (other.getDate() != null) {
                return false;
            }
        } else if (!getDate().equals(other.getDate())) {
            return false;
        }
        if (runtimeStatus != other.runtimeStatus) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatedRuntimeStatus [runtimeStatus=" + runtimeStatus + ", date=" + getDate() + "]";
    }
}
