package com.cannontech.roles.yukon;

import com.cannontech.roles.YukonRoleDefs;

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
    public static final int DEFAULT_AUTH_TYPE = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 7;
	
    public static final int LDAP_DN = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 8;
    public static final int LDAP_USER_SUFFIX = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 9;
    public static final int LDAP_USER_PREFIX = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 10;
    public static final int LDAP_SERVER_ADDRESS = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 11;
    public static final int LDAP_SERVER_PORT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 12;
    public static final int LDAP_SERVER_TIMEOUT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 13;
    
    public static final int AD_SERVER_ADDRESS = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 14;
    public static final int AD_SERVER_PORT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 15;
    public static final int AD_SERVER_TIMEOUT = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 16;
    public static final int AD_NTDOMAIN = YukonRoleDefs.AUTHENTICATION_PROPERTYID_BASE - 17;
}
