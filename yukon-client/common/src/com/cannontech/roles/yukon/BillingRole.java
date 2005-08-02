package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface BillingRole {
	public static final int ROLEID = YukonRoleDefs.BILLING_ROLEID;
	
	public static final int WIZ_ACTIVATE = YukonRoleDefs.BILLING_PROPERTYID_BASE;
	//Not implemented yet, there is one format that takes an input file, it's location is this property
	public static final int INPUT_FILE = YukonRoleDefs.BILLING_PROPERTYID_BASE - 1;
	//Label for the header
	public static final int HEADER_LABEL = YukonRoleDefs.BILLING_PROPERTYID_BASE - 2;
	//String for default billing file format.  Use string found in billingFileFormats table.
	public static final int DEFAULT_BILLING_FORMAT = YukonRoleDefs.BILLING_PROPERTYID_BASE - 3;
	//int value for demand days query back from billing end date
	public static final int DEMAND_DAYS_PREVIOUS = YukonRoleDefs.BILLING_PROPERTYID_BASE - 4;
	//int value for energy days query back from billing end date
	public static final int ENERGY_DAYS_PREVIOUS = YukonRoleDefs.BILLING_PROPERTYID_BASE - 5;
	//boolean value for appending to existing file, not sure how this works when downloading from web page yet
	public static final int APPEND_TO_FILE = YukonRoleDefs.BILLING_PROPERTYID_BASE - 6;
	//boolean value for removing the multiplier from the point values
	public static final int REMOVE_MULTIPLIER = YukonRoleDefs.BILLING_PROPERTYID_BASE - 7;
	//CADP format takes a coop ID
	public static final int COOP_ID_CADP_ONLY = YukonRoleDefs.BILLING_PROPERTYID_BASE - 8;
	
//	public static final int LOG_LEVEL = ApplicationRoleDefs.BILLING_PROPERTYID_BASE - 2;
}
