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
	public static final int CONSUMER_INFO_THERMOSTATS_ALL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 10;

	public static final int NOTIFICATION_ON_GENERAL_PAGE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 50;
	public static final int HIDE_OPT_OUT_BOX = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 51;
	public static final int AUTOMATIC_CONFIGURATION = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 52;
	public static final int DISABLE_PROGRAM_SIGNUP = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 54;
	public static final int OPT_OUT_RULES = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 55;
	
	public static final int WEB_LINK_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 100;
	public static final int WEB_LINK_UTIL_EMAIL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 101;
	public static final int WEB_LINK_THERM_INSTRUCTIONS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 102;
	public static final int WEB_TEXT_CONTROL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 110;
	public static final int WEB_TEXT_CONTROLLED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 111;
	public static final int WEB_TEXT_CONTROLLING = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 112;
	public static final int WEB_TEXT_OPT_OUT_NOUN = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 113;
	public static final int WEB_TEXT_OPT_OUT_VERB = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 114;
	public static final int WEB_TEXT_OPT_OUT_PAST = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 115;
	public static final int WEB_TEXT_ODDS_FOR_CONTROL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 116;
	public static final int WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 117;
	public static final int WEB_LABEL_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 130;
	public static final int WEB_LABEL_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 131;
	public static final int WEB_LABEL_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 132;
	public static final int WEB_LABEL_THERM_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 133;
	public static final int WEB_LABEL_THERM_MANUAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 134;
	public static final int WEB_TITLE_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 150;
	public static final int WEB_TITLE_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 151;
	public static final int WEB_TITLE_PROGRAM_CTRL_HIST = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 152;
	public static final int WEB_TITLE_PROGRAM_CTRL_SUM = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 153;
	public static final int WEB_TITLE_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 154;
	public static final int WEB_TITLE_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 155;
	public static final int WEB_TITLE_UTILITY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 156;
	public static final int WEB_TITLE_THERM_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 157;
	public static final int WEB_TITLE_THERM_MANUAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 158;
	public static final int WEB_DESC_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 170;
	public static final int WEB_DESC_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 171;
	public static final int WEB_DESC_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 172;
	public static final int WEB_DESC_UTILITY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 173;
	public static final int WEB_IMG_CORNER = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 180;
	public static final int WEB_IMG_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 181;

}
