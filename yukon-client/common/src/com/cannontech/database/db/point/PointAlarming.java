package com.cannontech.database.db.point;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class PointAlarming extends DBPersistent {
    
    public static final int NONE_NOTIFICATIONID = 1;
    public static final String NONE_VALUE_STRING = "(none)";
    public static final String EXCLUDE_NOTIFY_VALUE_STRING = "Exclude Notify";
    public static final String AUTO_ACK_VALUE_STRING = "Auto Ack";
    public static final String BOTH_OPTIONS_VALUE_STRING = "Exclude Notify & Auto Ack";
    public static final String DEFAULT_EXCLUDE_NOTIFY = "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";
    public static final String DEFAULT_ALARM_STATES;

    public static final int ALARM_STATE_COUNT = 32;

    // we need to initialize our char mask
    static {
        String tmp = "";
        for (int i = 0; i < ALARM_STATE_COUNT; i++)
            tmp += '\u0001'; // Do not have nulls!! This is the Second char in a ASCII list, int value of 1
        DEFAULT_ALARM_STATES = tmp;
    }
    
    private Integer pointID = null;
    private String alarmStates = DEFAULT_ALARM_STATES;
    private String excludeNotifyStates = DEFAULT_EXCLUDE_NOTIFY;
    private String notifyOnAcknowledge = "N";
    private Integer notificationGroupID = new Integer(PointAlarming.NONE_NOTIFICATIONID);

    public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

    public static final String SETTER_COLUMNS[] = { "ALARMSTATES", "EXCLUDENOTIFYSTATES", "NOTIFYONACKNOWLEDGE",
        "NOTIFICATIONGROUPID" };

    public final static String TABLE_NAME = "PointAlarming";

    public PointAlarming() {
        super();
    }

    public PointAlarming(Integer pointID, String alarmStates, String excludeNotifyStates, String notifyOnAcknowledge,
            Integer notificationGroupID) {
        super();
        setPointID(pointID);
        setAlarmStates(alarmStates);
        setExcludeNotifyStates(excludeNotifyStates);
        setNotifyOnAcknowledge(notifyOnAcknowledge);
        setNotificationGroupID(notificationGroupID);
    }

    public void add() throws SQLException {
        Object addValues[] =
            { getPointID(), getAlarmStates(), getExcludeNotifyStates(), getNotifyOnAcknowledge(), getNotificationGroupID() };

        add(TABLE_NAME, addValues);
    }

    public void delete() throws SQLException {

        delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID());
    }

    public String getAlarmStates() {
        return alarmStates;
    }

    public String getExcludeNotifyStates() {
        return excludeNotifyStates;
    }

    public Integer getNotificationGroupID() {
        return notificationGroupID;
    }

    public String getNotifyOnAcknowledge() {
        return notifyOnAcknowledge;
    }

    public void setNotifyOnAck(boolean notify) {

        if (notify) {
            if (isNotifyOnClear())
                setNotifyOnAcknowledge("B");
            else
                setNotifyOnAcknowledge("A");
        } else {
            if (isNotifyOnClear())
                setNotifyOnAcknowledge("C");
            else
                setNotifyOnAcknowledge("N");
        }
    }

    public void setNotifyOnClear(boolean notify) {

        if (notify) {
            if (isNotifyOnAck())
                setNotifyOnAcknowledge("B");
            else
                setNotifyOnAcknowledge("C");
        } else {
            if (isNotifyOnAck())
                setNotifyOnAcknowledge("A");
            else
                setNotifyOnAcknowledge("N");
        }
    }

    /**
     * Possible values: A, C, B, N
     * A: notify on ACK only
     * C: notify on CLEAR only
     * B: notify on ACK or CLEAR (both)
     * N: notify on none
     */
    public boolean isNotifyOnClear() {
        return getNotifyOnAcknowledge().charAt(0) == 'C' || getNotifyOnAcknowledge().charAt(0) == 'B';
    }

    public boolean isNotifyOnAck() {
        return getNotifyOnAcknowledge().charAt(0) == 'A' || getNotifyOnAcknowledge().charAt(0) == 'B';
    }

    /**
     * Returns the single char representation for the ExcludeNotify state.
     */
    public static char getExcludeNotifyChar(String notify) {
        if (EXCLUDE_NOTIFY_VALUE_STRING.equals(notify))
            return 'E';
        else if (AUTO_ACK_VALUE_STRING.equals(notify))
            return 'A';
        else if (BOTH_OPTIONS_VALUE_STRING.equals(notify))
            return 'B';
        else
            return 'N';
    }

    /**
     * Returns the string representation for the ExcludeNotify char state.
     */
    public static String getExcludeNotifyString(char val) {
        if (val == 'E' || val == 'Y')
            return EXCLUDE_NOTIFY_VALUE_STRING;
        else if (val == 'A')
            return AUTO_ACK_VALUE_STRING;
        else if (val == 'B')
            return BOTH_OPTIONS_VALUE_STRING;
        else
            return NONE_VALUE_STRING;
    }

    public Integer getPointID() {
        return pointID;
    }

    public void retrieve() throws SQLException {
        Object constraintValues[] = { getPointID() };

        Object results[] = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);

        if (results.length == SETTER_COLUMNS.length) {
            setAlarmStates((String) results[0]);
            setExcludeNotifyStates((String) results[1]);
            setNotifyOnAcknowledge((String) results[2]);
            setNotificationGroupID((Integer) results[3]);

        }
    }

    public void setAlarmStates(String newAlarmStates) {
        alarmStates = newAlarmStates;
    }

    public void setExcludeNotifyStates(String newExcludeNotifyStates) {
        excludeNotifyStates = newExcludeNotifyStates;
    }

    public void setNotificationGroupID(Integer newNotificationGroupID) {
        notificationGroupID = newNotificationGroupID;
    }

    public void setNotifyOnAcknowledge(String newNotifyOnAcknowledge) {
        notifyOnAcknowledge = newNotifyOnAcknowledge;
    }

    public void setPointID(Integer newValue) {
        this.pointID = newValue;
    }

    public void update() throws SQLException {
        Object setValues[] =
            { getAlarmStates(), getExcludeNotifyStates(), getNotifyOnAcknowledge(), getNotificationGroupID() };

        Object constraintValues[] = { getPointID() };

        update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
    }
}
