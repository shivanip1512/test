package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface CalcHistoricalRole {
	public static final int ROLEID = ApplicationRoleDefs.CALC_HISTORICAL_ROLEID;
	
	public static final int INTERVAL = ApplicationRoleDefs.CALC_HISTORICAL_PROPERTYID_BASE;
	public static final int BASELINE_CALCTIME = ApplicationRoleDefs.CALC_HISTORICAL_PROPERTYID_BASE - 1;
 	public static final int DAYS_PREVIOUS_TO_COLLECT = ApplicationRoleDefs.CALC_HISTORICAL_PROPERTYID_BASE - 2;
// 	public static final int LOG_LEVEL = ApplicationRoleDefs.CALC_HISTORICAL_PROPERTYID_BASE - 3;
}
