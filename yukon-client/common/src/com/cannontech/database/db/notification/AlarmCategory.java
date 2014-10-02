package com.cannontech.database.db.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * This type was created in VisualAge.
 */
public class AlarmCategory extends DBPersistent implements CTIDbChange, EditorPanel {
    private Integer alarmCategoryID = null;
    private String categoryName = null;
    private Integer notificationGroupID = null;

    private final String CONSTRAINT_COLUMNS[] = { "AlarmCategoryID" };
    private final String SELECT_COLUMNS[] = { "CategoryName", "NotificationGroupID" };

    public static final String TABLE_NAME = "AlarmCategory";

    /**
     * State constructor comment.
     */
    public AlarmCategory() {
        super();
        initialize(null, null, null);
    }

    /**
     * State constructor comment.
     */
    public AlarmCategory(Integer alarmStateID, String alarmStateName, Integer notificationGroupID) {
        super();
        initialize(alarmStateID, alarmStateName, notificationGroupID);
    }

    /**
     * add method comment.
     */
    @Override
    public void add() throws SQLException {
        // Do nothing
    }

    /**
     * delete method comment.
     */
    @Override
    public void delete() throws SQLException {
        // Do nothing
    }

    /**
     * This method was created in VisualAge.
     * 
     * @return com.cannontech.database.db.point.State[]
     * @param stateGroup java.lang.Integer
     */
    public static final AlarmCategory[] getAlarmCategories(Integer alCategoryID, String databaseAlias)
            throws SQLException {
        ArrayList tmpList = new ArrayList(50);
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String sql =
            "SELECT CategoryName,NotificationGroupID " + "FROM " + TABLE_NAME + " WHERE AlarmCategoryID = ? "
                + "ORDER BY CategoryName";

        try {
            conn = PoolManager.getInstance().getConnection(databaseAlias);

            if (conn == null) {
                throw new IllegalStateException("Error getting database connection.");
            } else {
                pstmt = conn.prepareStatement(sql.toString());
                pstmt.setInt(1, alCategoryID.intValue());

                rset = pstmt.executeQuery();

                while (rset.next()) {
                    tmpList.add(new AlarmCategory(alCategoryID, rset.getString("CategoryName"), new Integer(
                        rset.getInt("NotificationGroupID"))));
                }

            }
        } catch (SQLException e) {
            CTILogger.error(e.getMessage(), e);
        } finally {
            SqlUtils.close(rset, pstmt, conn);
        }

        AlarmCategory retVal[] = new AlarmCategory[tmpList.size()];
        tmpList.toArray(retVal);

        return retVal;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9/21/2001 11:46:13 AM)
     * 
     * @return java.lang.Integer
     */
    public Integer getAlarmCategoryID() {
        return alarmCategoryID;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9/21/2001 11:46:13 AM)
     * 
     * @return java.lang.String
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (12/19/2001 1:45:25 PM)
     * 
     * @return com.cannontech.message.dispatch.message.DBChangeMsg[]
     */
    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] msgs =
            { new DBChangeMsg(getAlarmCategoryID().intValue(), DBChangeMsg.CHANGE_ALARM_CATEGORY_DB,
                DBChangeMsg.CAT_ALARMCATEGORY, DBChangeMsg.CAT_ALARMCATEGORY, dbChangeType) };

        return msgs;
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/16/00 2:30:10 PM)
     * 
     * @return java.lang.Integer
     */
    public Integer getNotificationGroupID() {
        return notificationGroupID;
    }

    /**
     * This method was created in VisualAge.
     * 
     * @param stateGroupID java.lang.Integer
     * @param rawState java.lang.Integer
     * @param text java.lang.String
     * @param foregroundColor java.lang.Integer
     * @param backgoundColor java.lang.Integer
     */
    private void initialize(Integer alCategoryID, String catName, Integer notificationGroupID) {
        setAlarmCategoryID(alCategoryID);
        setCategoryName(catName);
        setNotificationGroupID(notificationGroupID);
    }

    /**
     * retrieve method comment.
     */
    @Override
    public void retrieve() throws SQLException {
        Object constraintValues[] = { getAlarmCategoryID() };

        Object results[] = retrieve(SELECT_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SELECT_COLUMNS.length) {
            setCategoryName((String) results[0]);
            setNotificationGroupID((Integer) results[1]);
        } else {
            throw new Error(getClass() + "::retrieve - Incorrect number of results");
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (9/21/2001 11:46:13 AM)
     * 
     * @param newAlarmCategoryID java.lang.Integer
     */
    public void setAlarmCategoryID(Integer newAlarmCategoryID) {
        alarmCategoryID = newAlarmCategoryID;
    }

    /**
     * Insert the method's description here.
     * Creation date: (9/21/2001 11:46:13 AM)
     * 
     * @param newCategoryName java.lang.String
     */
    public void setCategoryName(String newCategoryName) {
        categoryName = newCategoryName;
    }

    /**
     * Insert the method's description here.
     * Creation date: (11/16/00 2:30:10 PM)
     * 
     * @param newNotificationGroupID java.lang.Integer
     */
    public void setNotificationGroupID(Integer newNotificationGroupID) {
        notificationGroupID = newNotificationGroupID;
    }

    /**
     * This method was created in VisualAge.
     * 
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return getCategoryName();
    }

    /**
     * update method comment.
     */
    @Override
    public void update() throws SQLException {
        Object constraintValues[] = { getAlarmCategoryID() };

        Object setValues[] = { getCategoryName(), getNotificationGroupID() };

        update(TABLE_NAME, SELECT_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
}
