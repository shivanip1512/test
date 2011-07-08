package com.cannontech.roles.operator;

import com.cannontech.roles.OperatorRoleDefs;

/**
 * @author aaron
 */
public interface ConsumerInfoRole {
	public static final int ROLEID = OperatorRoleDefs.CONSUMER_INFO_ROLEID;
	
	public static final int CONSUMER_INFO_ACCOUNT_RESIDENCE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 2;
	public static final int CONSUMER_INFO_ACCOUNT_CALL_TRACKING = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 3;
	public static final int CONSUMER_INFO_METERING_INTERVAL_DATA = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 4;
	public static final int CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 6;
	public static final int CONSUMER_INFO_PROGRAMS_ENROLLMENT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 7;
	public static final int CONSUMER_INFO_PROGRAMS_OPT_OUT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 8;
	public static final int CONSUMER_INFO_APPLIANCES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 9;
	public static final int CONSUMER_INFO_APPLIANCES_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 10;
	public static final int CONSUMER_INFO_HARDWARES = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 11;
	public static final int CONSUMER_INFO_HARDWARES_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 12;
	public static final int CONSUMER_INFO_HARDWARES_THERMOSTAT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 13;
	public static final int CONSUMER_INFO_WORK_ORDERS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 14;
	public static final int CONSUMER_INFO_ADMIN_CHANGE_LOGIN_USERNAME = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 15;
	public static final int CONSUMER_INFO_ADMIN_FAQ = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 16;
	public static final int CONSUMER_INFO_THERMOSTATS_ALL = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 17;
	public static final int CONSUMER_INFO_METERING_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 18;
    public static final int CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 19;
    public static final int CONSUMER_INFO_WS_LM_DATA_ACCESS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 20;
    public static final int CONSUMER_INFO_WS_LM_CONTROL_ACCESS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 21;
	
	public static final int NEW_ACCOUNT_WIZARD = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 51;
	public static final int IMPORT_CUSTOMER_ACCOUNT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 52;
	public static final int INVENTORY_CHECKING = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 53;
//    moved to energy company role.
//	public static final int AUTOMATIC_CONFIGURATION = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 54;
	public static final int ORDER_NUMBER_AUTO_GEN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 55;
	public static final int CALL_NUMBER_AUTO_GEN = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 56;
	public static final int OPT_OUT_PERIOD = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 57;
    public static final int DISABLE_SWITCH_SENDING = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 58;
    public static final int METER_SWITCH_ASSIGNMENT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 59;
    public static final int CREATE_LOGIN_FOR_ACCOUNT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 60;
//    moved to energy company role
//    public static final int ACCOUNT_NUMBER_LENGTH = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 61;
//    public static final int ROTATION_DIGIT_LENGTH = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 62;
    public static final int ALLOW_ACCOUNT_EDITING = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 63;
    public static final int ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 64;
    public static final int ACCOUNT_SEARCH = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 65;
    public static final int SURVEY_EDIT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 66;
    public static final int OPT_OUT_SURVEY_EDIT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE - 67;

	public static final int WEB_LINK_FAQ = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2;
	public static final int WEB_LINK_THERM_INSTRUCTIONS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 1;
    public static final int INVENTORY_CHECKING_CREATE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 93;
    public static final int OPT_OUT_TODAY_ONLY = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 94;
    public static final int OPT_OUT_ADMIN_STATUS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 95;
    public static final int OPT_OUT_ADMIN_CHANGE_ENABLE = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 96;
    public static final int OPT_OUT_ADMIN_CANCEL_CURRENT = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 97;
    public static final int OPT_OUT_ADMIN_CHANGE_COUNTS = OperatorRoleDefs.CONSUMER_INFO_PROPERTYID_BASE2 - 98;
}
