package com.cannontech.roles.cicustomer;

import com.cannontech.roles.CICustomerRoleDefs;

/**
 * @author aaron
 */
public interface CommercialMeteringRole {
	public static final int ROLEID = CICustomerRoleDefs.COMMERCIAL_METERING_ROLEID;
	
	public static final int TRENDING_DISCLAIMER = CICustomerRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE;
	public static final int TRENDING_GET_DATA_NOW_BUTTON = CICustomerRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 1;
}
