package com.cannontech.roles;

/**
 * @author snebben
 */
public interface AnalysisRoleDefs extends RoleDefs {
	
	public static final int REPORTING_ROLEID = ANALYSIS_ROLEID_BASE;
	public static final int TRENDING_ROLEID = ANALYSIS_ROLEID_BASE - 1;
		
	static final int REPORTING_PROPERTYID_BASE = ANALYSIS_PROPERTYID_BASE;
	static final int TRENDING_PROPERTYID_BASE = ANALYSIS_PROPERTYID_BASE - 100;
}
