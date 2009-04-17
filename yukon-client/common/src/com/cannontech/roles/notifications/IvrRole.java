package com.cannontech.roles.notifications;

import com.cannontech.roles.NotificationsRoleDefs;

/**
 * @author aaron
 */
public interface IvrRole {
	public static final int ROLEID = NotificationsRoleDefs.IVR_ROLEID;

	public static final int NUMBER_OF_CHANNELS = NotificationsRoleDefs.NOTIFICATIONS_PROPERTYID_BASE - 1;
	public static final int IVR_URL_DIALER_TEMPLATE = NotificationsRoleDefs.NOTIFICATIONS_PROPERTYID_BASE - 4;
	public static final int IVR_URL_DIALER_SUCCESS_MATCHER = NotificationsRoleDefs.NOTIFICATIONS_PROPERTYID_BASE - 5;




}
