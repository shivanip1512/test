package com.cannontech.roles.yukon;

import com.cannontech.roles.YukonRoleDefs;

public interface EnergyCompanyRole {
    
    /* Lowercase for legacy reasons */
    public enum MeteringType {
        yukon,
        stars
    }
    
	public static final int ROLEID = YukonRoleDefs.ENERGY_COMPANY_ROLEID;
	
	public static final int ADMIN_EMAIL_ADDRESS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE;
	public static final int OPTOUT_NOTIFICATION_RECIPIENTS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 1;
	public static final int DEFAULT_TIME_ZONE = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 2;
	public static final int TRACK_HARDWARE_ADDRESSING = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 7;
	public static final int SINGLE_ENERGY_COMPANY = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 8;
	// SA protocol on/off toggle, etc.
    public static final int OPTIONAL_PRODUCT_DEV = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 9;
    public static final int DEFAULT_TEMPERATURE_UNIT = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 10;
    public static final int METER_MCT_BASE_DESIGNATION = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 11;
    public static final int APPLICABLE_POINT_TYPE_KEY = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 12;
	public static final int INHERIT_PARENT_APP_CATS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 14;
	public static final int AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 15;
    public static final int ACCOUNT_NUMBER_LENGTH = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 16;
    public static final int ROTATION_DIGIT_LENGTH = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 17;
    public static final int SERIAL_NUMBER_VALIDATION = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 18;
    public static final int AUTOMATIC_CONFIGURATION = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 19;
    public static final int ALLOW_DESIGNATION_CODES = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 20;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_ALL = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 21;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 22;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 23;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_7_DAY = YukonRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 24;
}