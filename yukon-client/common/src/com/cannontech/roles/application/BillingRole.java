package com.cannontech.roles.application;

import com.cannontech.roles.ApplicationRoleDefs;

/**
 * @author aaron
 * Role for actual user access.
 */
public interface BillingRole {
	public static final int ROLEID = ApplicationRoleDefs.BILLING_ROLEID;
	
	/** Controls access to Dynamic Billing File Setup */
	public static final int DYNAMIC_BILLING_FILE_SETUP = ApplicationRoleDefs.BILLING_PROPERTYID_BASE;
}
