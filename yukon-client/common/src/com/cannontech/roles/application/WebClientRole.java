package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface WebClientRole {
	public static final int ROLEID = ApplicationRoleDefs.WEB_CLIENT_ROLEID;
	
	public static final int HOME_URL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE;
	public static final int LOG_LEVEL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 1;
}
