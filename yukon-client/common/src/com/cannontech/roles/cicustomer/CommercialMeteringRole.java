package com.cannontech.roles.cicustomer;

import com.cannontech.roles.CICustomerRoleDefs;

/**
 * @author aaron
 */
public interface CommercialMeteringRole {
	public static final int ROLEID = CICustomerRoleDefs.COMMERCIAL_METERING_ROLEID;
	
	public static final int TRENDING_DISCLAIMER = CICustomerRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE;
	public static final int GET_DATA_NOW_ENABLED = CICustomerRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 1;
	public static final int MINIMUM_SCAN_FREQUENCY = CICustomerRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 2;
	public static final int MAXIMUM_DAILY_SCANS = CICustomerRoleDefs.COMMERCIAL_METERING_PROPERTYID_BASE - 3;
}