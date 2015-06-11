package com.cannontech.database.model;

public class Season {
    
    private Integer scheduleId = 1;
    private String seasonName;
    
    /**
     * Season constructor.
     */
    public Season() {
        super();
    }
    
    public Season(String name, Integer scheduleId) {
        super();
        this.seasonName = name;
        this.scheduleId = scheduleId;
    }

    public String getSeasonName() {
        return seasonName;
    }
    
    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String toString() {
        return seasonName;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((scheduleId == null) ? 0 : scheduleId.hashCode());
        result = PRIME * result + ((seasonName == null) ? 0 : seasonName.hashCode());
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
        final Season other = (Season) obj;
        if (scheduleId == null) {
            if (other.scheduleId != null)
                return false;
        } else if (!scheduleId.equals(other.scheduleId))
            return false;
        if (seasonName == null) {
            if (other.seasonName != null)
                return false;
        } else if (!seasonName.equals(other.seasonName))
            return false;
        return true;
    }

}
