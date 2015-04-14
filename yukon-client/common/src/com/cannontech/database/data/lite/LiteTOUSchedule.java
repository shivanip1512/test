package com.cannontech.database.data.lite;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.tou.TOUSchedule;

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

    /**
     * retrieve method comment.
     */
    public void retrieve(String databaseAlias) {

        SqlStatement s = new SqlStatement("SELECT TOUScheduleID, TOUScheduleName, TOUDefaultRate " + 
                                          "FROM " + TOUSchedule.TABLE_NAME +
                                          " where TOUScheduleID = " + getScheduleID(), 
                                          CtiUtilities.getDatabaseAlias());

        try {
            s.execute();

            if (s.getRowCount() <= 0)
                throw new IllegalStateException("Unable to find TOU schedule with ID = " + getLiteID());

            setScheduleID(new Integer(s.getRow(0)[0].toString()).intValue());
            setScheduleName(s.getRow(0)[1].toString());
            setDefaultRate(s.getRow(0)[2].toString());
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
        }

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