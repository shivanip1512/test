package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface BillingRole {
	public static final int ROLEID = ApplicationRoleDefs.BILLING_ROLEID;
	
	public static final int WIZ_ACTIVATE = ApplicationRoleDefs.BILLING_PROPERTYID_BASE;
	public static final int INPUT_FILE = ApplicationRoleDefs.BILLING_PROPERTYID_BASE - 1;
//	public static final int LOG_LEVEL = ApplicationRoleDefs.BILLING_PROPERTYID_BASE - 2;
}
