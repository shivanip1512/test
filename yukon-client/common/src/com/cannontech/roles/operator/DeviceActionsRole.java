package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface DeviceActionsRole {
    public static final int ROLEID = OperatorRoleDefs.DEVICE_ACTIONS_ROLEID;

    /**Controls access to Bulk Operation to Import Devices. */
    public static final int BULK_IMPORT_OPERATION = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE; // -21300
    /**Controls access to Bulk Operation to Update Devices. */
    public static final int BULK_UPDATE_OPERATION = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 1; // -21301
    /**Controls editing the actual Device Group (Add/Remove Group, update group name)*/
    public static final int DEVICE_GROUP_EDIT = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 2; // -21302
    /**Controls modifying the "contents" of the group (Add to/Remove from Group, etc.)*/
    public static final int DEVICE_GROUP_MODIFY = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 3; // -21303
    /**Controls access to Group Commander */
    public static final int GROUP_COMMANDER = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 4; // -21304
    /**Controls access to Mass Change collection action.  This is inclusive of ALL possible mass change types. */
    public static final int MASS_CHANGE = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 5; // -21305
    /**Controls access to Locate Route collection action. */
    public static final int LOCATE_ROUTE = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 6; // -21306
    /**Controls access to Mass Delete collection action. */
    public static final int MASS_DELETE = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 7; // -21307
    /**Controls access to Add Points collection action. */
    public static final int ADD_REMOVE_POINTS = OperatorRoleDefs.DEVICE_ACTIONS_PROPERTYID_BASE - 8; // -21308
}
