package com.cannontech.core.dao;

import com.cannontech.database.db.DBPersistent;

public interface DBDeletionDao {
    
    //types of delete
    public static final int POINT_TYPE = 1;
    public static final int NOTIF_GROUP_TYPE = 2;
    public static final int STATEGROUP_TYPE = 3;
    public static final int PORT_TYPE = 4;
    public static final int DEVICE_TYPE = 5;
    public static final int PAO_TYPE    = 6;
    public static final int CONTACT_TYPE = 7;
    public static final int CUSTOMER_TYPE = 8;
    public static final int LOGIN_TYPE = 9;
    public static final int HOLIDAY_SCHEDULE = 10;
    public static final int LOGIN_GRP_TYPE = 11;
    public static final int BASELINE_TYPE = 12;
    public static final int CONFIG_TYPE = 13;
    public static final int TAG_TYPE = 14;
    public static final int LMPROG_CONSTR_TYPE = 15;
    public static final int SEASON_SCHEDULE = 16;
    public static final int TOU_TYPE = 17;
    public static final int ROUTE_TYPE = 18;
    public static final int DEVICE_CONFIGURATION = 19;
    public static final int DEVICE_CONFIGURATION_CATEGORY = 20;
    public static final int USER_GROUP_TYPE = 21;

    //the return types of each possible delete
    public static final byte STATUS_ALLOW = 1;
    public static final byte STATUS_CONFIRM = 2;
    public static final byte STATUS_DISALLOW = 3;
    
    public DBDeleteResult getDeleteInfo(DBPersistent toDelete,
            final String nodeName);

    /**
     * Accepts the result of the getDeleteInfo() method.
     */
    public byte deletionAttempted(final DBDeleteResult dbRes)
            throws java.sql.SQLException;

}