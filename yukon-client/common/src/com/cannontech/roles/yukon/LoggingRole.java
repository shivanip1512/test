package com.cannontech.roles.yukon;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface LoggingRole 
{
	public static final int ROLEID = YukonRoleDefs.LOGGING_ROLEID;
	
	public static final int DBEDITOR_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE;
	public static final int DATABASE_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 1;
	public static final int TDC_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 2;
	public static final int COMMANDER_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 3;
	public static final int BILLING_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 4;
	public static final int CALCHIST_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 5;
	public static final int CAPCONTROL_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 6;
	public static final int ESUB_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 7;
	public static final int EXPORT_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 8;
	public static final int LOADCONTROL_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 9;
	public static final int MACS_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 10;
	public static final int NOTIFICATION_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 11;
	public static final int REPORTING_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 12;
	public static final int TRENDING_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 13;
	public static final int STARS_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 14;
	public static final int GENERAL_LEVEL = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 15;
	public static final int LOG_TO_FILE = YukonRoleDefs.LOGGING_PROPERTYID_BASE - 16;

}
