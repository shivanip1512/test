package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface WebClientRole {
	public static final int ROLEID = ApplicationRoleDefs.WEB_CLIENT_ROLEID;
	
	public static final int HOME_URL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE;
//	public static final int LOG_LEVEL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 1;

	public static final int STYLE_SHEET = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 2;
	public static final int NAV_BULLET_SELECTED = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 3;	//Bullet.gif
	public static final int NAV_BULLET_EXPAND = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 4;	//BulletExpand.gif
	public static final int HEADER_LOGO = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 5;
	
	public static final int LOG_IN_URL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 6;
	
	public static final int NAV_CONNECTOR_BOTTOM = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 7;	// BottomConnector.gif
	public static final int NAV_CONNECTOR_MIDDLE = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 8;	// MidConnector.gif
	public static final int STD_PAGE_STYLE_SHEET = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 9;	// Standard Page CSS
	public static final int POPUP_APPEAR_STYLE = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 10;   // If the popups are on click or on mouseover
}
