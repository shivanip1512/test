package com.cannontech.roles;

/**
 * @author aaron
 */
public interface YukonRoleDefs extends RoleDefs {
	public static final int SYSTEM_ROLEID = YUKON_ROLEID_BASE;
	public static final int ENERGY_COMPANY_ROLEID = YUKON_ROLEID_BASE - 1;
	public static final int LOGGING_ROLEID = YUKON_ROLEID_BASE - 2;
	public static final int AUTHENTICATION_ROLEID = YUKON_ROLEID_BASE - 3;
    public static final int VOICE_SERVER_ROLEID = YUKON_ROLEID_BASE - 4;

	public static final int SYSTEM_PROPERTYID_BASE = YUKON_PROPERTYID_BASE;
	public static final int ENERGY_COMPANY_PROPERTYID_BASE = YUKON_PROPERTYID_BASE - 100;
	public static final int LOGGING_PROPERTYID_BASE = YUKON_PROPERTYID_BASE - 200;
	public static final int AUTHENTICATION_PROPERTYID_BASE = YUKON_PROPERTYID_BASE - 300;
    public static final int VOICE_PROPERTYID_BASE = YUKON_PROPERTYID_BASE - 400;
}
