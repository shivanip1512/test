package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface CommercialMeteringRole {
	public static final int ROLEID = OperatorRoleDefs.COMMERCIAL_METERING_ROLEID;
	
	public static final int TRENDING_DISCLAIMER = OperatorRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE;	

    public static final int BILLING_ENABLED = OperatorRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 1; // -20201
    public static final int TRENDING_ENABLED = OperatorRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 2; // -20202	
    public static final int IMPORTER_ENABLED = OperatorRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 3;	// -20203
}
