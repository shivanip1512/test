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
public interface AuthenticationRole {
	
	//Valid string values for AUTHENTICATION_MODE
	public static final String YUKON_AUTH_STRING = "Yukon";
	public static final String RADIUS_AUTH_STRING = "Radius";
	
	public static final int ROLEID = YukonRoleDefs.AUTHENTICATION_ROLEID;
	
	public static final int SERVER_ADDRESS = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE;
	public static final int AUTH_PORT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 1;
	public static final int ACCT_PORT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 2;
	public static final int SECRET_KEY = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 3;
	public static final int AUTH_METHOD = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 4;
	public static final int AUTHENTICATION_MODE = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 5;
	public static final int AUTH_TIMEOUT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 6;
	
}
