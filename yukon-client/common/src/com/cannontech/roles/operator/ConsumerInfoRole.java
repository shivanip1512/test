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
	public static final int CONSUMER_INFO_PROGRAMS_OPT_OUT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 8;
	public static final int CONSUMER_INFO_APPLIANCES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 9;
	public static final int CONSUMER_INFO_APPLIANCES_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 10;
	public static final int CONSUMER_INFO_HARDWARES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 11;
	public static final int CONSUMER_INFO_HARDWARES_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 12;
	public static final int CONSUMER_INFO_HARDWARES_THERMOSTAT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 13;
	public static final int CONSUMER_INFO_WORK_ORDERS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 14;
	public static final int CONSUMER_INFO_ADMIN_CHANGE_LOGIN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 15;
	public static final int CONSUMER_INFO_ADMIN_FAQ = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 16;
	public static final int CONSUMER_INFO_THERMOSTATS_ALL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 17;
	public static final int CONSUMER_INFO_METERING_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 18;
	
	public static final int SUPER_OPERATOR = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 50;
	public static final int NEW_ACCOUNT_WIZARD = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 51;
	public static final int IMPORT_CUSTOMER_ACCOUNT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 52;
	public static final int INVENTORY_CHECKING = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 53;
	public static final int AUTOMATIC_CONFIGURATION = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 54;
	public static final int ORDER_NUMBER_AUTO_GEN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 55;
	public static final int CALL_NUMBER_AUTO_GEN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 56;
	public static final int OPT_OUT_RULES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 57;
	
	public static final int WEB_LINK_FAQ = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2;
	public static final int WEB_LINK_THERM_INSTRUCTIONS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 1;
	public static final int WEB_TEXT_CONTROL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 10;
	public static final int WEB_TEXT_OPT_OUT_NOUN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 13;
	public static final int WEB_TEXT_OPT_OUT_VERB = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 14;
	public static final int WEB_TEXT_OPT_OUT_PAST = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 15;
	public static final int WEB_TEXT_REENABLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 16;
	public static final int WEB_TEXT_ODDS_FOR_CONTROL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 19;
	public static final int WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 20;
	public static final int WEB_LABEL_CONTROL_HISTORY = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 30;
	public static final int WEB_LABEL_ENROLLMENT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 31;
	public static final int WEB_LABEL_OPT_OUT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 32;
	public static final int WEB_LABEL_THERM_SCHED = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 33;
	public static final int WEB_LABEL_THERM_MANUAL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 34;
	public static final int WEB_TITLE_CONTROL_HISTORY = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 50;
	public static final int WEB_TITLE_PROGRAM_CTRL_HIST = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 51;
	public static final int WEB_TITLE_PROGRAM_CTRL_SUM = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 52;
	public static final int WEB_TITLE_ENROLLMENT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 53;
	public static final int WEB_TITLE_OPT_OUT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 54;
	public static final int WEB_TITLE_THERM_SCHED = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 55;
	public static final int WEB_TITLE_THERM_MANUAL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 56;
 	public static final int WEB_DESC_OPT_OUT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 70;
	
}
