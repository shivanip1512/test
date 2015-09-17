package com.cannontech.database.db.season;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.yukon.IDatabaseCache;

public class SeasonSchedule extends DBPersistent {

    private Integer scheduleID;
    private String scheduleName;

    public static final String SETTER_COLUMNS[] = { "ScheduleName" };

    public static final String CONSTRAINT_COLUMNS[] = { "ScheduleID" };

    public static final String TABLE_NAME = "SeasonSchedule";

    public SeasonSchedule() {
        super();
    }

    public void add() throws SQLException {
        Object addValues[] = { getScheduleId(), getScheduleName() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getScheduleId());
    }

    public Integer getScheduleId() {
        return scheduleID;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public final static Integer getNextSeasonScheduleID() {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteSeasonSchedule> seasonSchedules = cache.getAllSeasonSchedules();
            Collections.sort(seasonSchedules);

            int counter = 1;
            int currentID;

            for (int i = 0; i < seasonSchedules.size(); i++) {
                currentID = seasonSchedules.get(i).getScheduleID();

                if (currentID > counter)
                    break;
                else
                    counter = currentID + 1;
            }

            return new Integer(counter);
        }
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getScheduleId() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setScheduleName((String) results[0]);
        }

    }

    public void setScheduleID(Integer id) {
        scheduleID = id;
    }

    public void setScheduleName(String name) {
        scheduleName = name;

    }

    public void update() throws SQLException {
        Object setValues[] = { getScheduleName() };
        Object constraintValues[] = { getScheduleId() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public static SeasonSchedule[] getAllCBCSchedules() {
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        Vector<SeasonSchedule> vect = new Vector<>();

        // Get all the data from the database
        String sql = "SELECT ScheduleID, ScheduleName " + 
                     "FROM " + TABLE_NAME + " " +
                     "ORDER BY ScheduleName";

        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    Integer scheduleId = new Integer(rset.getInt(1));
                    if (scheduleId != 0) { // ignore the 'Empty Schedule' schedule for capcontrol purposes
                        SeasonSchedule cbcSS = new SeasonSchedule();

                        cbcSS.setScheduleID(scheduleId);
                        cbcSS.setScheduleName(rset.getString(2));
                        vect.add(cbcSS);
                    }
                }
            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        SeasonSchedule[] strats = new SeasonSchedule[vect.size()];
        return vect.toArray(strats);
    }

}
