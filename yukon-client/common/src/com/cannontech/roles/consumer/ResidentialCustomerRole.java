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
	
	public static final int WEB_LINK_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 50;
	public static final int WEB_LINK_UTIL_EMAIL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 51;
	public static final int WEB_TEXT_CONTROL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 52;
	public static final int WEB_TEXT_CONTROLLED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 53;
	public static final int WEB_TEXT_CONTROLLING = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 54;
	public static final int WEB_TEXT_RECOMMENDED_SETTINGS_BUTTON = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 55;
	public static final int WEB_TEXT_GENERAL_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 56;
	public static final int WEB_TEXT_CONTROL_HISTORY_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 57;
	public static final int WEB_TEXT_PROGRAM_CTRL_HIST_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 58;
	public static final int WEB_TEXT_PROGRAM_CTRL_SUM_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 59;
	public static final int WEB_TEXT_ENROLLMENT_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 60;
	public static final int WEB_TEXT_OPT_OUT_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 61;
	public static final int WEB_TEXT_UTILITY_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 62;
	public static final int WEB_TEXT_CHANGE_LOGIN_TITLE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 63;
	public static final int WEB_TEXT_CONTROL_HISTORY_LABEL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 64;
	public static final int WEB_TEXT_ENROLLMENT_LABEL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 65;
	public static final int WEB_TEXT_OPT_OUT_LABEL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 66;
	public static final int WEB_TEXT_GENERAL_DESC = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 67;
	public static final int WEB_TEXT_OPT_OUT_DESC = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 68;

}
