package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface MeteringRole {
	public static final int ROLEID = OperatorRoleDefs.METERING_ROLEID;
	
    public static final int IMPORTER_ENABLED = OperatorRoleDefs.METERING_PROPERTYID_BASE - 3;	// -20203
}
