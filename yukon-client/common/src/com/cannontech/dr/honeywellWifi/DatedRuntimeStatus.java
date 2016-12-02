package com.cannontech.dr.honeywellWifi;

import org.joda.time.DateTime;

import com.cannontech.dr.service.impl.RuntimeStatus;

/**
 * Object representation of DR device relay state at a particular point in time, for calculating runtime.
 */
public class DatedRuntimeStatus {
    private final RuntimeStatus runtimeStatus;
    private final DateTime date;
    
    public DatedRuntimeStatus(RuntimeStatus runtimeStatus, DateTime date) {
            this.runtimeStatus = runtimeStatus;
            this.date = date;
    }
    
    public RuntimeStatus getRuntimeStatus() {
        return runtimeStatus;
    }

    public DateTime getDate() {
        return date;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
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
        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!date.equals(other.date)) {
            return false;
        }
        if (runtimeStatus != other.runtimeStatus) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatedRuntimeStatus [runtimeStatus=" + runtimeStatus + ", date=" + date + "]";
    }
}
