package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface WebGraphRole {
	public static final int ROLEID = ApplicationRoleDefs.WEB_GRAPH_ROLEID;
	
	public static final int HOME_DIRECTORY = ApplicationRoleDefs.WEB_GRAPH_PROPERTYID_BASE;
	public static final int RUN_INTERVAL = ApplicationRoleDefs.WEB_GRAPH_PROPERTYID_BASE - 1;
//	public static final int LOG_LEVEL = ApplicationRoleDefs.WEB_GRAPH_PROPERTYID_BASE - 2;
}
