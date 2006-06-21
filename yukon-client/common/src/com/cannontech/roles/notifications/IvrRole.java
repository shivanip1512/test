package com.cannontech.roles.notifications;

import com.cannontech.roles.NotificationsRoleDefs;
import com.cannontech.roles.YukonRoleDefs;

/**
 * @author aaron
 */
public interface IvrRole {
	public static final int ROLEID = NotificationsRoleDefs.IVR_ROLEID;

	//property that originated from a different role
	public static final int VOICE_APP = YukonRoleDefs.VOICE_PROPERTYID_BASE;


	public static final int NUMBER_OF_CHANNELS = NotificationsRoleDefs.NOTIFICATIONS_PROPERTYID_BASE - 1;
	public static final int INTRO_TEXT = NotificationsRoleDefs.NOTIFICATIONS_PROPERTYID_BASE - 2;


}
