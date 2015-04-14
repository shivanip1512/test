package com.cannontech.database.data.lite;


public class LiteSeasonSchedule extends LiteBase {
    private String scheduleName;

    public LiteSeasonSchedule() {
        super();
        setLiteType(LiteTypes.SEASON_SCHEDULE);
    }

    public LiteSeasonSchedule(int seasID) {
        super();
        setLiteID(seasID);
        setLiteType(LiteTypes.SEASON_SCHEDULE);
    }

    public LiteSeasonSchedule(int seasID, String schdName_) {
        this(seasID);
        setScheduleName(schdName_);
    }

    public int getScheduleID() {
        return getLiteID();
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleID(int seasID) {
        setLiteID(seasID);
    }

    public void setScheduleName(String name) {
        scheduleName = name;
    }

    @Override
    public String toString() {
        return getScheduleName();
    }
}