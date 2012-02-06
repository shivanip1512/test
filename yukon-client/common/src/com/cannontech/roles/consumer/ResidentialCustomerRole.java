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

	public static final int CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 3;
	public static final int CONSUMER_INFO_PROGRAMS_ENROLLMENT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 4;
	public static final int CONSUMER_INFO_PROGRAMS_OPT_OUT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 5;
	public static final int CONSUMER_INFO_HARDWARES_THERMOSTAT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 6;
	public static final int CONSUMER_INFO_QUESTIONS_UTIL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 7;
	public static final int CONSUMER_INFO_QUESTIONS_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 8;
	public static final int CONSUMER_INFO_CHANGE_LOGIN_USERNAME = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 9;
	public static final int CONSUMER_INFO_THERMOSTATS_ALL = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 10;
	public static final int CONSUMER_INFO_CHANGE_LOGIN_PASSWORD = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 11;
    
	
	public static final int HIDE_OPT_OUT_BOX = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 51;
	public static final int OPT_OUT_PERIOD = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 55;
	public static final int OPT_OUT_LIMITS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 56;
	
	public static final int WEB_LINK_FAQ = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 100;
	public static final int WEB_LINK_THERM_INSTRUCTIONS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 102;

	public static final int CONTACTS_ACCESS = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 197;
    
	public static final int OPT_OUT_TODAY_ONLY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 198;
	
    public static final int SIGN_OUT_ENABLED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 199;
    public static final int CREATE_LOGIN_FOR_ACCOUNT = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 200;
    public static final int OPT_OUT_ALL_DEVICES = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 201;
    public static final int ENROLLMENT_MULTIPLE_PROGRAMS_PER_CATEGORY = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 202;
    public static final int ENROLLMENT_PER_DEVICE = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 203;
    
    public static final int AUTO_THERMOSTAT_MODE_ENABLED = ConsumerRoleDefs.RESIDENTIAL_CUSTOMER_PROPERTYID_BASE - 300;
}
