package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.season.SeasonSchedule;

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

    /**
     * retrieve method comment.
     */
    public void retrieve(String databaseAlias) {
        SqlStatement s = new SqlStatement("SELECT ScheduleID, ScheduleName " + 
                                          "FROM " + SeasonSchedule.TABLE_NAME + 
                                          " where ScheduleID = " + getScheduleID(),
                                          CtiUtilities.getDatabaseAlias());

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find Season with ScheduleID = " + getLiteID());

            setScheduleID(new Integer(s.getRow(0)[0].toString()).intValue());
            setScheduleName(s.getRow(0)[1].toString());
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        }
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