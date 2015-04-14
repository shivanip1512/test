package com.cannontech.database.data.lite;


public class LiteHolidaySchedule extends LiteBase {
    private String holidayScheduleName;

    public LiteHolidaySchedule() {
        super();
        setLiteType(LiteTypes.HOLIDAY_SCHEDULE);
    }

    public LiteHolidaySchedule(int scheduleID) {
        super();
        setLiteID(scheduleID);
        setLiteType(LiteTypes.HOLIDAY_SCHEDULE);
    }

    public LiteHolidaySchedule(int scheduleID, String schdName_) {
        this(scheduleID);
        setHolidayScheduleName(schdName_);
    }

    public int getHolidayScheduleID() {
        return getLiteID();
    }

    public String getHolidayScheduleName() {
        return holidayScheduleName;
    }

    public void setHolidayScheduleID(int scheduleID) {
        setLiteID(scheduleID);
    }

    public void setHolidayScheduleName(String name) {
        holidayScheduleName = name;
    }

    @Override
    public String toString() {
        return getHolidayScheduleName();
    }
}