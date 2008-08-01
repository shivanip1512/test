package com.cannontech.roles.application;

import com.cannontech.roles.ApplicationRoleDefs;

/**
 * @author snebben
 */
public interface ReportingRole {
	public static final int ROLEID = ApplicationRoleDefs.REPORTING_ROLEID;

	//Label for the header
	public static final int HEADER_LABEL = ApplicationRoleDefs.ANALYSIS_PROPERTYID_BASE;
 	
	public static final int DOWNLOAD_REPORTS_ENABLE = ApplicationRoleDefs.ANALYSIS_PROPERTYID_BASE - 1;
	//A default file name when downloads are done
	public static final int DOWNLOAD_DEFAULT_FILENAME = ApplicationRoleDefs.ANALYSIS_PROPERTYID_BASE - 2;

	//Access to groups of reports.  SEE RoleTypes to create actual report type group
	public static final int ADMIN_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 3;
	public static final int AMR_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 4;
	public static final int STATISTICAL_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 5;
	public static final int LOAD_MANAGEMENT_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 6;
	public static final int CAP_CONTROL_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 7;
	public static final int DATABASE_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 8;
	public static final int STARS_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 9;
	public static final int OTHER_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 10;
	public static final int SETTLEMENT_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 11;
	public static final int CI_CURTAILMENT_REPORTS_GROUP = ApplicationRoleDefs.REPORTING_PROPERTYID_BASE - 23;
}
