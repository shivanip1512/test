package com.cannontech.roles.operator;

import com.cannontech.roles.*;

/**
 * @author aaron
 */
public interface ConsumerInfoRole {
	public static final int ROLEID = OperatorRoleDefs.CONSUMER_INFO_ROLEID;
	
	public static final int CONSUMER_INFO_NOT_IMPLEMENTED = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE;
	public static final int CONSUMER_INFO_ACCOUNT_GENERAL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 1;
	public static final int CONSUMER_INFO_ACCOUNT_RESIDENCE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 2;
	public static final int CONSUMER_INFO_ACCOUNT_CALL_TRACKING = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 3;
	public static final int CONSUMER_INFO_METERING_INTERVAL_DATA = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 4;
	public static final int CONSUMER_INFO_METERING_USAGE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 5;
	public static final int CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 6;
	public static final int CONSUMER_INFO_PROGRAMS_ENROLLMENT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 7;
	public static final int CONSUMER_INFO_PROGRAMS_OPTOUT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 8;
	public static final int CONSUMER_INFO_APPLIANCES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 9;
	public static final int CONSUMER_INFO_APPLIANCES_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 10;
	public static final int CONSUMER_INFO_HARDWARES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 11;
	public static final int CONSUMER_INFO_HARDWARES_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 12;
	public static final int CONSUMER_INFO_HARDWARES_THERMOSTAT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 13;
	public static final int CONSUMER_INFO_WORK_ORDERS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 14;
	public static final int CONSUMER_INFO_ADMIN_CHANGE_LOGIN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 15;
	public static final int CONSUMER_INFO_ADMIN_FAQ = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 16;
	
	public static final int SUPER_OPERATOR = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 30;
	public static final int NEW_ACCOUNT_WIZARD = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 31;
	public static final int CUSTOMIZED_FAQ_LINK= OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 32;
	
	public static final int WEB_LINK_FAQ = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 50;
	public static final int WEB_TEXT_CONTROL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 51;
	public static final int WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 52;
	public static final int WEB_TEXT_CONTROL_HISTORY_TITLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 53;
	public static final int WEB_TEXT_PROGRAM_CTRL_HIST_TITLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 54;
	public static final int WEB_TEXT_PROGRAM_CTRL_SUM_TITLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 55;
	public static final int WEB_TEXT_ENROLLMENT_TITLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 56;
	public static final int WEB_TEXT_OPT_OUT_TITLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 57;
	public static final int WEB_TEXT_CHANGE_LOGIN_TITLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 58;
	public static final int WEB_TEXT_CONTROL_HISTORY_LABEL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 59;
	public static final int WEB_TEXT_ENROLLMENT_LABEL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 60;
	public static final int WEB_TEXT_OPT_OUT_LABEL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 61;
 	public static final int WEB_TEXT_OPT_OUT_DESC = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 62;
	
}
