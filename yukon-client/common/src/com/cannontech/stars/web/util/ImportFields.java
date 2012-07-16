package com.cannontech.stars.web.util;

public class ImportFields {

	// Customer account fields
	private static int acct_idx = 0;
	public static final int IDX_LINE_NUM = acct_idx++;
	public static final int IDX_ACCOUNT_ID = acct_idx++;
	public static final int IDX_ACCOUNT_NO = acct_idx++;
	public static final int IDX_CUSTOMER_TYPE = acct_idx++;
	public static final int IDX_COMPANY_NAME = acct_idx++;
    public static final int IDX_IVR_PIN = acct_idx++;
    public static final int IDX_IVR_USERNAME = acct_idx++;
	public static final int IDX_CUSTOMER_NUMBER = acct_idx++;
	public static final int IDX_LAST_NAME = acct_idx++;
	public static final int IDX_FIRST_NAME = acct_idx++;
	public static final int IDX_HOME_PHONE = acct_idx++;
	public static final int IDX_WORK_PHONE = acct_idx++;
	public static final int IDX_WORK_PHONE_EXT = acct_idx++;
	public static final int IDX_EMAIL = acct_idx++;
	public static final int IDX_ACCOUNT_NOTES = acct_idx++;
	public static final int IDX_STREET_ADDR1 = acct_idx++;
	public static final int IDX_STREET_ADDR2 = acct_idx++;
	public static final int IDX_CITY = acct_idx++;
	public static final int IDX_STATE = acct_idx++;
	public static final int IDX_ZIP_CODE = acct_idx++;
	public static final int IDX_COUNTY = acct_idx++;
	public static final int IDX_MAP_NO = acct_idx++;
	public static final int IDX_PROP_NOTES = acct_idx++;
	public static final int IDX_USERNAME = acct_idx++;
	public static final int IDX_PASSWORD = acct_idx++;
	public static final int IDX_LOGIN_GROUP = acct_idx++;
	public static final int IDX_RATE_SCHEDULE = acct_idx++;
	public static final int IDX_SUBSTATION = acct_idx++;
	public static final int IDX_FEEDER = acct_idx++;
	public static final int IDX_POLE = acct_idx++;
	public static final int IDX_TRFM_SIZE = acct_idx++;
	public static final int IDX_SERV_VOLT = acct_idx++;
	public static final int IDX_ACCOUNT_ACTION = acct_idx++;
	public static final int NUM_ACCOUNT_FIELDS = acct_idx;
	
	// Inventory fields
	// IDX_LINE_NUM = 0
	// IDX_ACCOUNT_ID = 1
	private static int inv_idx = 2;
	public static final int IDX_INV_ID = inv_idx++;
	public static final int IDX_SERIAL_NO = inv_idx++;
	public static final int IDX_DEVICE_TYPE = inv_idx++;
	public static final int IDX_RECEIVE_DATE = inv_idx++;
	public static final int IDX_INSTALL_DATE = inv_idx++;
	public static final int IDX_REMOVE_DATE = inv_idx++;
	public static final int IDX_SERVICE_COMPANY = inv_idx++;
	public static final int IDX_ALT_TRACK_NO = inv_idx++;
	public static final int IDX_DEVICE_VOLTAGE = inv_idx++;
	public static final int IDX_DEVICE_STATUS = inv_idx++;
	public static final int IDX_INV_NOTES = inv_idx++;
	public static final int IDX_DEVICE_NAME = inv_idx++;
	public static final int IDX_PROGRAM_NAME = inv_idx++;
	public static final int IDX_ADDR_GROUP = inv_idx++;
	public static final int IDX_HARDWARE_ACTION = inv_idx++;
	public static final int IDX_R1_GROUP = inv_idx++;
	public static final int IDX_R2_GROUP = inv_idx++;
	public static final int IDX_R3_GROUP = inv_idx++;
	public static final int IDX_R1_STATUS = inv_idx++;
	public static final int IDX_R2_STATUS = inv_idx++;
	public static final int IDX_R3_STATUS = inv_idx++;
    public static final int IDX_OPTION_PARAMS = inv_idx++;
    public static final int IDX_DEVICE_LABEL = inv_idx++;
    public static final int IDX_DEVICE_MANUFACTURER = inv_idx++;
    public static final int IDX_DEVICE_MODEL = inv_idx++;
    public static final int NUM_INV_FIELDS = inv_idx;
	
	// Appliance fields
	// IDX_LINE_NUM = 0
	// IDX_ACCOUNT_ID = 1
	// IDX_INV_ID = 2
	private static int app_idx = 3;
	public static final int IDX_APP_ID = app_idx++;
	public static final int IDX_RELAY_NUM = app_idx++;
	public static final int IDX_APP_DESC = app_idx++;
	public static final int IDX_APP_TYPE = app_idx++;
	public static final int IDX_APP_NOTES = app_idx++;
	public static final int IDX_MANUFACTURER = app_idx++;
	public static final int IDX_AVAIL_FOR_CTRL = app_idx++;
	public static final int IDX_YEAR_MADE = app_idx++;
	public static final int IDX_APP_KW = app_idx++;
	public static final int IDX_AC_TONNAGE = app_idx++;
	public static final int IDX_WH_NUM_GALLONS = app_idx++;
	public static final int IDX_WH_NUM_ELEMENTS = app_idx++;
	public static final int IDX_WH_ENERGY_SRC = app_idx++;
	public static final int IDX_GEN_TRAN_SWITCH_TYPE = app_idx++;
	public static final int IDX_GEN_TRAN_SWITCH_MFC = app_idx++;
	public static final int IDX_GEN_CAPACITY = app_idx++;
	public static final int IDX_GEN_FUEL_CAP = app_idx++;
	public static final int IDX_GEN_START_DELAY = app_idx++;
	public static final int IDX_IRR_TYPE = app_idx++;
	public static final int IDX_IRR_HORSE_POWER = app_idx++;
	public static final int IDX_IRR_ENERGY_SRC = app_idx++;
	public static final int IDX_IRR_SOIL_TYPE = app_idx++;
	public static final int IDX_IRR_METER_LOC = app_idx++;
	public static final int IDX_IRR_METER_VOLT = app_idx++;
	public static final int IDX_GDRY_TYPE = app_idx++;
	public static final int IDX_GDRY_BIN_SIZE = app_idx++;
	public static final int IDX_GDRY_ENERGY_SRC = app_idx++;
	public static final int IDX_GDRY_HORSE_POWER = app_idx++;
	public static final int IDX_GDRY_HEAT_SRC = app_idx++;
	public static final int IDX_HP_TYPE = app_idx++;
	public static final int IDX_HP_SIZE = app_idx++;
	public static final int IDX_HP_STANDBY_SRC = app_idx++;
	public static final int IDX_HP_RESTART_DELAY = app_idx++;
	public static final int IDX_SH_TYPE = app_idx++;
	public static final int IDX_SH_CAPACITY = app_idx++;
	public static final int IDX_SH_RECHARGE_TIME = app_idx++;
	public static final int IDX_DF_SWITCH_OVER_TYPE = app_idx++;
	public static final int IDX_DF_2ND_CAPACITY = app_idx++;
	public static final int IDX_DF_2ND_ENERGY_SRC = app_idx++;
	public static final int IDX_APP_CAT_DEF_ID = app_idx++;
	public static final int NUM_APP_FIELDS = app_idx;
	
}