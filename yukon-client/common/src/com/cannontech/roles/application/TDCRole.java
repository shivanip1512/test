package com.cannontech.roles.application;

import com.cannontech.roles.*;

/**
 * TDC Role Definition
 * @author aaron
 */
public interface TDCRole {
	public static final int ROLEID = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_ROLEID;
	
	public static final int LOADCONTROL_EDIT = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE;
	public static final int MACS_EDIT = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 1;
	public static final int TDC_EXPRESS = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 2;
	public static final int TDC_MAX_ROWS = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 3;
	public static final int TDC_RIGHTS = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE -4;
	public static final int CAP_CONTROL_INTERFACE = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 5;
	public static final int CBC_CREATION_NAME = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 6;
	public static final int TDC_ALARM_COUNT = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 7;
	public static final int DECIMAL_PLACES = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 8;
	public static final int PFACTOR_DECIMAL_PLACES = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 9;
	
//	public static final int LOG_LEVEL = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 10;
		
	public static final int LC_REDUCTION_COL = ApplicationRoleDefs.TABULAR_DISPLAY_CONSOLE_PROPERTYID_BASE - 11;
}
