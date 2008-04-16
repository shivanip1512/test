package com.cannontech.roles.application;

import com.cannontech.roles.ApplicationRoleDefs;

/**
 * @author aaron
 */
public interface TrendingRole {
	public static final int ROLEID = ApplicationRoleDefs.TRENDING_ROLEID;
	
	public static final int GRAPH_EDIT_GRAPHDEFINITION = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE;
	public static final int TRENDING_DISCLAIMER = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 2;

	public static final int SCAN_NOW_ENABLED = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 3;
	public static final int MINIMUM_SCAN_FREQUENCY = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 5;
	public static final int MAXIMUM_DAILY_SCANS = ApplicationRoleDefs.TRENDING_PROPERTYID_BASE - 6;
}
