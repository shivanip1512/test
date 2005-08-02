package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 * Role for actual user access.
 */
public interface BillingRole {
	public static final int ROLEID = ApplicationRoleDefs.BILLING_ROLEID;
	
	/* The label used for the header title */
	public static final int HEADER_LABEL = ApplicationRoleDefs.BILLING_PROPERTYID_BASE;
}
