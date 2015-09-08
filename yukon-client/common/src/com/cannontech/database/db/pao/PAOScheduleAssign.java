package com.cannontech.database.db.pao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

public class PAOScheduleAssign extends DBPersistent {
    private Integer eventID = null;
    private Integer scheduleID = INVALID_SCHEDULEID;
    private Integer paoID = null;
    private String command = CtiUtilities.STRING_NONE;
    private String disableOvUv = "N";

    public static final String SETTER_COLUMNS[] = { "ScheduleID", "PaoID", "Command", "DisableOvUv" };

    public static final String CONSTRAINT_COLUMNS[] = { "EventID" };

    public static final String TABLE_NAME = "PAOScheduleAssignment";

    // how many schedules we allow per PAO
    public static final int MAX_SHEDULES_PER_PAO = 10;

    // represents a invalid PAOScheduleAssign instance
    public static final int INVALID_SCHEDULEID = -1;

    private static final String ALL_SCHEDULES_SQL = "select EventID, ScheduleID, PaoID, Command, DisableOvUv " + 
            "FROM " + TABLE_NAME + " " + 
            "WHERE paoID = ? " + "ORDER BY paoID";

    public PAOScheduleAssign() {
        super();
    }

    public PAOScheduleAssign(Integer evID) {
        this();
        setEventID(evID);
    }

    public void add() throws SQLException {
        if (getScheduleID() == INVALID_SCHEDULEID)
            return;

        if (getEventID() == null)
            setEventID(getNextEventID());

        Object addValues[] = { getEventID(), getScheduleID(), getPaoID(), getCommand(), getDisableOvUv() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {
        Object values[] = { getEventID() };

        delete(TABLE_NAME, CONSTRAINT_COLUMNS, values);
    }

    public static final PAOScheduleAssign[] getAllPAOSchedAssignments(int parentPaoID, Connection conn) {
        Vector<PAOScheduleAssign> tmpList = new Vector<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        try {
            if (conn == null) {
                throw new IllegalStateException("Database connection should not be null.");
            } else {
                pstmt = conn.prepareStatement(ALL_SCHEDULES_SQL);
                pstmt.setInt(1, parentPaoID);

                rset = pstmt.executeQuery();

                while (rset.next()) {
                    PAOScheduleAssign item = new PAOScheduleAssign();

                    item.setEventID(rset.getInt(1));
                    item.setScheduleID(rset.getInt(2));
                    item.setPaoID(rset.getInt(3));
                    item.setCommand(rset.getString(4));
                    item.setDisableOvUv(rset.getString(5));

                    tmpList.add(item);
                }
            }

        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (rset != null)
                    rset.close();
            } catch (SQLException e2) {
                CTILogger.error(e2.getMessage(), e2);
            }
        }

        PAOScheduleAssign retVal[] = new PAOScheduleAssign[tmpList.size()];
        tmpList.toArray(retVal);
        return retVal;
    }

    public static synchronized boolean deleteAllPAOScheduleAssignments(int paoID, Connection conn) {
        Statement stat = null;
        try {
            if (conn == null)
                throw new IllegalStateException("Database connection should not be null.");

            stat = conn.createStatement();

            stat.execute("DELETE FROM " + TABLE_NAME + " WHERE PAOid=" + paoID);

        } catch (Exception e) {
            CTILogger.error(e.getMessage(), e);
            return false;
        } finally {
            SqlUtils.close(stat);
        }

        return true;
    }

    public int getNextEventID() {
        NextValueHelper nextValueHelper = YukonSpringHook.getBean(NextValueHelper.class);

        return nextValueHelper.getNextValue("PAOScheduleAssignment");
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getEventID() };
        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setScheduleID((Integer) results[0]);
            setPaoID((Integer) results[1]);
            setCommand((String) results[2]);
            setDisableOvUv((String) results[3]);
        }

    }

    public void update() throws SQLException {
        if (getScheduleID() == INVALID_SCHEDULEID)
            return;

        Object setValues[] = { getScheduleID(), getPaoID(), getCommand(), getDisableOvUv() };

        Object constraintValues[] = { getEventID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }

    public Integer getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(Integer integer) {
        scheduleID = integer;
    }

    public String getCommand() {
        return command;
    }

    public Integer getEventID() {
        return eventID;
    }

    public Integer getPaoID() {
        return paoID;
    }

    public void setCommand(String string) {
        command = string;
    }

    public void setEventID(Integer integer) {
        eventID = integer;
    }

    public void setPaoID(Integer integer) {
        paoID = integer;
    }

    public void setDisableOvUv(String disableOvUv) {
        this.disableOvUv = disableOvUv;
    }

    public String getDisableOvUv() {
        return disableOvUv;
    }
   
    public boolean isDisableOvUvBoolean() {
        return "Y".equals(disableOvUv);
    }

    public void setDisableOvUvBoolean(boolean disableOvUv) {
        if (disableOvUv) {
            this.disableOvUv = "Y";
        } else {
            this.disableOvUv = "N";
        }
    }

    public boolean getDisableOvUvBoolean() {
        return disableOvUv.equalsIgnoreCase("N") ? false : true;
    }

}