package com.cannontech.roles.amr;

import com.cannontech.roles.*;

/**
 * @author snebben
 */
public interface BillingRole {
	public static final int ROLEID = AMRRoleDefs.BILLING_ROLEID;
	
	//Label for the header
	public static final int HEADER_LABEL = AMRRoleDefs.BILLING_PROPERTYID_BASE;
	//String for default billing file format.  Use string found in billingFileFormats table.
	public static final int DEFAULT_BILLING_FORMAT = AMRRoleDefs.BILLING_PROPERTYID_BASE - 1;
	//int value for demand days query back from billing end date
	public static final int DEMAND_DAYS_PREVIOUS = AMRRoleDefs.BILLING_PROPERTYID_BASE - 2;
	//int value for energy days query back from billing end date
	public static final int ENERGY_DAYS_PREVIOUS = AMRRoleDefs.BILLING_PROPERTYID_BASE - 3;
	//boolean value for appending to existing file, not sure how this works when downloading from web page yet
	public static final int APPEND_TO_FILE = AMRRoleDefs.BILLING_PROPERTYID_BASE - 4;
	//boolean value for removing the multiplier from the point values
	public static final int REMOVE_MULTIPLIER = AMRRoleDefs.BILLING_PROPERTYID_BASE - 5;
	
	//Not implemented yet, there is one format that takes an input file, it's location is this property
	public static final int INPUT_FILE_LOCATION = AMRRoleDefs.BILLING_PROPERTYID_BASE - 6;
	//CADP format takes a coop ID
	public static final int COOP_ID_CADP_ONLY = AMRRoleDefs.BILLING_PROPERTYID_BASE - 7;
}
