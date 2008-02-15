package com.cannontech.database.model;

public class Holiday {
    
    private Integer scheduleId = 1;
    private String holidayName;
    
    /**
     * Season constructor.
     */
    public Holiday() {
        super();
    }

    public String getHolidayName() {
        return holidayName;
    }
    
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String toString() {
        return holidayName;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((scheduleId == null) ? 0 : scheduleId.hashCode());
        result = PRIME * result + ((holidayName == null) ? 0 : holidayName.hashCode());
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
        final Holiday other = (Holiday) obj;
        if (scheduleId == null) {
            if (other.scheduleId != null)
                return false;
        } else if (!scheduleId.equals(other.scheduleId))
            return false;
        if (holidayName == null) {
            if (other.holidayName != null)
                return false;
        } else if (!holidayName.equals(other.holidayName))
            return false;
        return true;
    }
}
