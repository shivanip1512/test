package com.cannontech.roles.yukon;

import com.cannontech.roles.EnergyCompanyRoleDefs;

public interface EnergyCompanyRole {
    
    /* Lowercase for legacy reasons */
    public enum MeteringType {
        yukon,
        stars
    }
    
	public static final int ROLEID = EnergyCompanyRoleDefs.ENERGY_COMPANY_ROLEID;
	
	public static final int ADMIN_EMAIL_ADDRESS = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE;
	public static final int OPTOUT_NOTIFICATION_RECIPIENTS = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 1;
	public static final int DEFAULT_TIME_ZONE = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 2;
	public static final int TRACK_HARDWARE_ADDRESSING = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 7;
	public static final int SINGLE_ENERGY_COMPANY = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 8;
	// SA protocol on/off toggle, etc.
    public static final int OPTIONAL_PRODUCT_DEV = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 9;
    public static final int DEFAULT_TEMPERATURE_UNIT = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 10;
    public static final int METER_MCT_BASE_DESIGNATION = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 11;
    public static final int APPLICABLE_POINT_TYPE_KEY = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 12;
	public static final int INHERIT_PARENT_APP_CATS = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 14;
	public static final int AUTO_CREATE_LOGIN_FOR_ADDITIONAL_CONTACTS = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 15;
    public static final int ACCOUNT_NUMBER_LENGTH = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 16;
    public static final int ROTATION_DIGIT_LENGTH = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 17;
    public static final int SERIAL_NUMBER_VALIDATION = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 18;
    public static final int AUTOMATIC_CONFIGURATION = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 19;
    public static final int ALLOW_DESIGNATION_CODES = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 20;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_ALL = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 21;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_WEEKEND = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 22;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_WEEKDAY_SATURDAY_SUNDAY = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 23;
    public static final int ALLOW_THERMOSTAT_SCHEDULE_7_DAY = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 24;
    public static final int BROADCAST_OPT_OUT_CANCEL_SPID = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 25;
    public static final int ALTERNATE_PROGRAM_ENROLLMENT = EnergyCompanyRoleDefs.ENERGY_COMPANY_PROPERTYID_BASE - 26;
}