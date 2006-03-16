package com.cannontech.roles;

/**
 * @author snebben
 */
public interface NotificationsRoleDefs extends RoleDefs {
	
	public static final int IVR_ROLEID = NOTIFICATIONS_ROLEID_BASE;
	public static final int CONFIGURATION_ROLEID = NOTIFICATIONS_ROLEID_BASE - 1;
		
	static final int IVR_PROPERTYID_BASE = NOTIFICATIONS_PROPERTYID_BASE;
	static final int CONFIGURATION_PROPERTYID_BASE = NOTIFICATIONS_PROPERTYID_BASE - 100;
}
