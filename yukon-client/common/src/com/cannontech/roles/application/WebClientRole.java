package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface WebClientRole {
	public static final int ROLEID = ApplicationRoleDefs.WEB_CLIENT_ROLEID;
	
	public static final int HOME_URL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE;
	public static final int LOG_LEVEL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 1;

	public static final int STYLE_SHEET = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 2;
	public static final int NAV_BULLET_SELECTED = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 3;	//Bullet.gif
	public static final int NAV_BULLET = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 4;	//Bullet2.gif
	
	public static final int HEADER_LOGO = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 5;
}
