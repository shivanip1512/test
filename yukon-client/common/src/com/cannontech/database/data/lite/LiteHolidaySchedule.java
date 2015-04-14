package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.holiday.HolidaySchedule;

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

    /**
     * retrieve method comment.
     */
    public void retrieve(String databaseAlias) {
        SqlStatement s = new SqlStatement("SELECT HolidayScheduleID,HolidayScheduleName " + 
                                          "FROM " + HolidaySchedule.TABLE_NAME + 
                                          " where HolidayScheduleID = " + getHolidayScheduleID(),
                                          CtiUtilities.getDatabaseAlias());

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find HolidaySchedule with holidayScheduleID = " + getLiteID());

            setHolidayScheduleID(new Integer(s.getRow(0)[0].toString()).intValue());
            setHolidayScheduleName(s.getRow(0)[1].toString());
        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
        }
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