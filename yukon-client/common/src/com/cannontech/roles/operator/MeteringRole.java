package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface MeteringRole {
    public static final int ROLEID = OperatorRoleDefs.METERING_ROLEID;

    public static final int IMPORTER_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 3; // -20203

    public static final int TOU_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 4; // -20204
    public static final int DEVICE_GROUP_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 5; // -20205
    public static final int PROFILE_REQUEST_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 6; // -20206

}
