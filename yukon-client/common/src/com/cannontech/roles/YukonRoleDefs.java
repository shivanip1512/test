package com.cannontech.roles;

/**
 * @author aaron
 */
public interface YukonRoleDefs extends RoleDefs {
	public static final int SYSTEM_ROLEID = YUKON_ROLEID_BASE;
	public static final int ENERGY_COMPANY_ROLDID = YUKON_ROLEID_BASE - 1;
	public static final int LOGGING_ROLDID = YUKON_ROLEID_BASE - 2;
	
	public static final int SYSTEM_PROPERTYID_BASE = YUKON_PROPERTYID_BASE;
	public static final int ENERGY_COMPANY_PROPERTYID_BASE = YUKON_PROPERTYID_BASE - 100;
	public static final int LOGGING_PROPERTYID_BASE = YUKON_PROPERTYID_BASE - 200;
}
