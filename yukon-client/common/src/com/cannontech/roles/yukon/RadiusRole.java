package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface RadiusRole {
	public static final int ROLEID = YukonRoleDefs.RADIUS_ROLEID;
	
	public static final int RADIUS_SERVER_ADDRESS = YukonRoleDefs.RADIUS_PROPERTYID_BASE;
	public static final int RADIUS_AUTH_PORT = YukonRoleDefs.RADIUS_PROPERTYID_BASE - 1;
	public static final int RADIUS_ACCT_PORT = YukonRoleDefs.RADIUS_PROPERTYID_BASE - 2;
	public static final int RADIUS_SECRET_KEY = YukonRoleDefs.RADIUS_PROPERTYID_BASE - 3;
	public static final int RADIUS_AUTH_METHOD = YukonRoleDefs.RADIUS_PROPERTYID_BASE - 4;
}
