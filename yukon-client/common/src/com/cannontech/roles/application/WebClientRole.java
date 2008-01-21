package com.cannontech.roles.application;

import com.cannontech.roles.ApplicationRoleDefs;

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
    // -9 was moved to EnergyCompanyRole
	public static final int POPUP_APPEAR_STYLE = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 10;   // If the popups are on click or on mouseover
	public static final int INBOUND_VOICE_HOME_URL = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 11;
	public static final int JAVA_WEB_START_LAUNCHER_ENABLED = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 12;
	public static final int SUPPRESS_ERROR_PAGE_DETAILS = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 14;
	public static final int DATA_UPDATER_DELAY_MS = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 15;
    public static final int STD_PAGE_STYLE_SHEET = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 16;  // Standard Page CSS
    public static final int THEME_NAME = ApplicationRoleDefs.WEB_CLIENT_PROPERTYID_BASE - 17;  // Theme Name
}
