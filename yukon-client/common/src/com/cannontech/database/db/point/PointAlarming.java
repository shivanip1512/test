package com.cannontech.database.db.point;

import static com.google.common.base.Preconditions.checkArgument;

import java.sql.SQLException;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.db.DBPersistent;
import com.google.common.collect.ImmutableMap;

public class PointAlarming extends DBPersistent {
    
    public static final int NONE_NOTIFICATIONID = 1;
    
    public static enum AlarmNotificationTypes implements DisplayableEnum, DatabaseRepresentationSource{
        NONE("(none)"),
        EXCLUDE_NOTIFY("Exclude Notify"),
        AUTO_ACK("Auto Ack"),
        BOTH_OPTIONS("Exclude Notify & Auto Ack");

        private static final Logger log = YukonLogManager.getLogger(AlarmNotificationTypes.class);
        private static final String baseKey = "yukon.common.point.alarmNotificationType.";
        private final static ImmutableMap<String, AlarmNotificationTypes> lookupByNotificationType;
        
        static {
            try {
                ImmutableMap.Builder<String, AlarmNotificationTypes> notificationTypeBuilder = ImmutableMap.builder();
                for (AlarmNotificationTypes type : values()) {
                    notificationTypeBuilder.put(type.dbString, type);
                }
                lookupByNotificationType = notificationTypeBuilder.build();
            } catch (IllegalArgumentException e) {
                log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
                throw e;
            }
        }
        
        private String dbString;
        
        private AlarmNotificationTypes(String dbString) {
            this.dbString = dbString;
        }

        @Override
        public Object getDatabaseRepresentation() {
            return dbString;
        }

        @Override
        public String getFormatKey() {
            if (this == NONE) {
                return "yukon.common.none.choice";
            }
            return baseKey + name();
        }

        public String getDbString() {
            return dbString;
        }

        public static AlarmNotificationTypes getAnalogControlTypeValue(String value) {
            AlarmNotificationTypes alarmNotificationType = lookupByNotificationType.get(value);
            checkArgument(alarmNotificationType != null, alarmNotificationType);
            return alarmNotificationType;
        } 
    }
    
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
        if (AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString().equals(notify))
            return 'E';
        else if (AlarmNotificationTypes.AUTO_ACK.getDbString().equals(notify))
            return 'A';
        else if (AlarmNotificationTypes.BOTH_OPTIONS.getDbString().equals(notify))
            return 'B';
        else
            return 'N';
    }

    /**
     * Returns the string representation for the ExcludeNotify char state.
     */
    public static String getExcludeNotifyString(char val) {
        if (val == 'E' || val == 'Y')
            return AlarmNotificationTypes.EXCLUDE_NOTIFY.getDbString();
        else if (val == 'A')
            return AlarmNotificationTypes.AUTO_ACK.getDbString();
        else if (val == 'B')
            return AlarmNotificationTypes.BOTH_OPTIONS.getDbString();
        else
            return AlarmNotificationTypes.NONE.getDbString();
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
