package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface MeteringRole {
    public static final int ROLEID = OperatorRoleDefs.METERING_ROLEID;

    public static final int IMPORTER_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 3; // -20203

    /** Controls ability to collect profile data (in the past) from the meter. */
    public static final int PROFILE_COLLECTION = OperatorRoleDefs.METERING_PROPERTYID_BASE - 6; // -20206
    /** Controls Move In/Out auto archiving (versus manual (or none?)*/
    public static final int MOVE_IN_MOVE_OUT_AUTO_ARCHIVING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 7; // -20207
    /** Controls access to process a move in/out */
    public static final int MOVE_IN_MOVE_OUT = OperatorRoleDefs.METERING_PROPERTYID_BASE - 8; // -20208
    /** Controls ability to collect profile data (now) by scanning the meter. */
    public static final int PROFILE_COLLECTION_SCANNING = OperatorRoleDefs.METERING_PROPERTYID_BASE - 9; // -20209
    /** Controls access to process a Hight bill complaint */
    public static final int HIGH_BILL_COMPLAINT = OperatorRoleDefs.METERING_PROPERTYID_BASE - 10; // -20210
}
