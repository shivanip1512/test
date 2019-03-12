package com.cannontech.dr.service.impl;

import org.joda.time.DateTime;

/**
 * Object representation of DR device shed state at a particular point in time, for calculating shedtime.
 */
public class DatedShedtimeStatus extends DatedStatus {
    private final ShedtimeStatus shedtimeStatus;
    
    public DatedShedtimeStatus(ShedtimeStatus status, DateTime date) {
        super(date);
        this.shedtimeStatus = status;
    }
    
    @Override
    public boolean isActive() {
        return shedtimeStatus == ShedtimeStatus.SHED;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDate() == null) ? 0 : getDate().hashCode());
        result = prime * result + ((shedtimeStatus == null) ? 0 : shedtimeStatus.hashCode());
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
        DatedShedtimeStatus other = (DatedShedtimeStatus) obj;
        if (getDate() == null) {
            if (other.getDate() != null) {
                return false;
            }
        } else if (!getDate().equals(other.getDate())) {
            return false;
        }
        if (shedtimeStatus != other.shedtimeStatus) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DatedShedtimeStatus [shedtimeStatus=" + shedtimeStatus + ", date=" + getDate() + "]";
    }
}
