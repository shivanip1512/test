package com.cannontech.database.data.lite;


public class LiteTOUSchedule extends LiteBase {
    private String scheduleName;
    private String defaultRate;

    public LiteTOUSchedule() {
        super();
        setLiteType(LiteTypes.TOU_SCHEDULE);
    }

    public LiteTOUSchedule(int schedID) {
        super();
        setLiteID(schedID);
        setLiteType(LiteTypes.TOU_SCHEDULE);
    }

    public LiteTOUSchedule(int schedID, String schedName_, String defaultRate_) {
        this(schedID);
        setScheduleName(schedName_);
        setDefaultRate(defaultRate_);
    }

    public int getScheduleID() {
        return getLiteID();
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleID(int schedID) {
        setLiteID(schedID);
    }

    public void setScheduleName(String name) {
        scheduleName = name;
    }

    @Override
    public String toString() {
        return getScheduleName();
    }

    public String getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(String defaultRate) {
        this.defaultRate = defaultRate;
    }
}