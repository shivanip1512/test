/*
 * Created on Apr 13, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.activity;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ActivityLogActions
{
	/* Login/out Actions Strings to log */
	public static final String LOGIN_WEB_ACTIVITY_ACTION = "LOG IN (WEB)";
	public static final String LOGIN_CLIENT_ACTIVITY_ACTION = "LOG IN (CLIENT)";
	public static final String LOGOUT_ACTIVITY_LOG = "LOG OUT";
	public static final String LOGIN_FAILED_ACTIVITY_LOG = "LOGIN FAILED";
	
	public static final String MANUAL_LMPROGRAM_START_ACTION = "Manual LMProgram Start";
	public static final String MANUAL_LMPROGRAM_STOP_ACTION = "Manual LMProgram Stop";
	
	public static final String PROGRAM_OPT_OUT_ACTION = "Program Opt Out";
	public static final String PROGRAM_REENABLE_ACTION = "Program Reenable";
	public static final String PROGRAM_CANCEL_SCHEDULED_ACTION = "Program Cancel Scheduled";
	public static final String PROGRAM_ENROLLMENT_ACTION = "Program Enrollment";
	
	public static final String MANUAL_MACS_SCHEDULE_START_ACTION = "Manual MACS Schedule Start";
	public static final String MANUAL_MACS_SCHEDULE_STOP_ACTION = "Manual MACS Schedule Stop";
	
	public static final String HARDWARE_CONFIGURATION_ACTION = "Hardware Configuration";
	public static final String HARDWARE_SAVE_TO_BATCH_ACTION = "Hardware Save To Batch";
	public static final String HARDWARE_SAVE_CONFIG_ONLY_ACTION = "Hardware Save Config Only";
	public static final String HARDWARE_ENABLE_ACTION = "Hardware Enable";
	public static final String HARDWARE_DISABLE_ACTION = "Hardware Disable";
	public static final String HARDWARE_SEND_BATCH_CONFIG_ACTION = "Hardware Send Batch Config";
	
	public static final String INVENTORY_ADD_RANGE = "Inventory Add Range";
	public static final String INVENTORY_UPDATE_RANGE = "Inventory Update Range";
	public static final String INVENTORY_CONFIG_RANGE = "Inventory Config Range";
	public static final String INVENTORY_DELETE_RANGE = "Inventory Delete Range";
	
	public static final String THERMOSTAT_MANUAL_ACTION = "Thermostat Manual";
	public static final String THERMOSTAT_SCHEDULE_ACTION = "Thermostat Schedule";
	
	public static final String SCAN_DATA_NOW_ACTION = "Scan Meter Data Now";
}
