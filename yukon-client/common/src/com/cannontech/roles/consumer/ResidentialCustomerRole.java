package com.cannontech.roles.consumer;

import com.cannontech.roles.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface ResidentialCustomerRole {
	public static final int ROLEID = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_ROLEID;
	
	public static final int CONSUMER_INFO_NOT_IMPLEMENTED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE;
	public static final int CONSUMER_INFO_ACCOUNT_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 1;
	public static final int CONSUMER_INFO_METERING_USAGE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 2;
	public static final int CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 3;
	public static final int CONSUMER_INFO_PROGRAMS_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 4;
	public static final int CONSUMER_INFO_PROGRAMS_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 5;
	public static final int CONSUMER_INFO_HARDWARES_THERMOSTAT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 6;
	public static final int CONSUMER_INFO_QUESTIONS_UTIL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 7;
	public static final int CONSUMER_INFO_QUESTIONS_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 8;
	public static final int CONSUMER_INFO_ADMIN_CHANGE_LOGIN = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 9;

	public static final int NOTIFICATION_ON_GENERAL_PAGE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 30;
	public static final int HIDE_OPT_OUT_BOX = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 31;
	public static final int CUSTOMIZED_FAQ_LINK= ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 32;
	public static final int CUSTOMIZED_UTIL_EMAIL_LINK = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 33;
	public static final int DISABLE_PROGRAM_SIGNUP = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 34;
	
	public static final int WEB_LINK_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 40;
	public static final int WEB_LINK_UTIL_EMAIL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 41;
	public static final int WEB_LINK_LOG_OFF = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 42;
	public static final int WEB_TEXT_CONTROL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 45;
	public static final int WEB_TEXT_CONTROLLED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 46;
	public static final int WEB_TEXT_CONTROLLING = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 47;
	public static final int WEB_TEXT_OPT_OUT_NOUN = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 48;
	public static final int WEB_TEXT_OPT_OUT_VERB = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 49;
	public static final int WEB_TEXT_OPT_OUT_PAST = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 50;
	public static final int WEB_TEXT_ODDS_FOR_CONTROL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 51;
	public static final int WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 52;
	public static final int WEB_LABEL_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 65;
	public static final int WEB_LABEL_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 66;
	public static final int WEB_LABEL_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 67;
	public static final int WEB_LABEL_THERM_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 68;
	public static final int WEB_LABEL_THERM_MANUAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 69;
	public static final int WEB_TITLE_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 75;
	public static final int WEB_TITLE_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 76;
	public static final int WEB_TITLE_PROGRAM_CTRL_HIST = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 77;
	public static final int WEB_TITLE_PROGRAM_CTRL_SUM = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 78;
	public static final int WEB_TITLE_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 79;
	public static final int WEB_TITLE_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 80;
	public static final int WEB_TITLE_UTILITY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 81;
	public static final int WEB_TITLE_THERM_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 82;
	public static final int WEB_TITLE_THERM_MANUAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 83;
	public static final int WEB_DESC_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 90;
	public static final int WEB_DESC_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 91;

}
