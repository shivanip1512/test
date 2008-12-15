package com.cannontech.roles.consumer;

import com.cannontech.roles.ConsumerRoleDefs;

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
	public static final int CONSUMER_INFO_ADMIN_CHANGE_LOGIN_PASSWORD = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 11;
    
	
	public static final int HIDE_OPT_OUT_BOX = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 51;
	public static final int AUTOMATIC_CONFIGURATION = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 52;
	public static final int OPT_OUT_RULES = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 55;
	
	public static final int WEB_LINK_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 100;
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
	public static final int WEB_LABEL_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 135;
	public static final int WEB_LABEL_CONTACT_US = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 136;
	public static final int WEB_LABEL_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 137;
	public static final int WEB_LABEL_CHANGE_LOGIN = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 138;
	public static final int WEB_LABEL_THERM_SAVED_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 139;
	public static final int WEB_TITLE_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 150;
	public static final int WEB_TITLE_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 151;
	public static final int WEB_TITLE_PROGRAM_CTRL_HIST = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 152;
	public static final int WEB_TITLE_PROGRAM_CTRL_SUM = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 153;
	public static final int WEB_TITLE_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 154;
	public static final int WEB_TITLE_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 155;
	public static final int WEB_TITLE_UTILITY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 156;
	public static final int WEB_TITLE_THERM_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 157;
	public static final int WEB_TITLE_THERM_MANUAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 158;
	public static final int WEB_TITLE_THERM_SAVED_SCHED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 159;
	public static final int WEB_DESC_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 170;
	public static final int WEB_DESC_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 171;
	public static final int WEB_DESC_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 172;
	public static final int WEB_DESC_UTILITY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 173;
	public static final int WEB_IMG_CORNER = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 180;
	public static final int WEB_IMG_GENERAL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 181;
	public static final int WEB_HEADING_ACCOUNT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 190;
	public static final int WEB_HEADING_THERMOSTAT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 191;
	public static final int WEB_HEADING_METERING = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 192;
	public static final int WEB_HEADING_PROGRAMS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 193;
	public static final int WEB_HEADING_TRENDING = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 194;
	public static final int WEB_HEADING_QUESTIONS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 195;
	public static final int WEB_HEADING_ADMINISTRATION = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 196;
	public static final int CONTACTS_ACCESS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 197;
    public static final int OPT_OUT_TODAY_ONLY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 198;
    public static final int SIGN_OUT_ENABLED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 199;
    public static final int CREATE_LOGIN_FOR_ACCOUNT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 200;
    public static final int OPT_OUT_DEVICE_SELECTION = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 201;
    public static final int ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 202;
    public static final int ENROLLMENT_PER_DEVICE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 203;

}
