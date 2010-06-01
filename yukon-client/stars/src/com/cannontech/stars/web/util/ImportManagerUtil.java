/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;


/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportManagerUtil {

	public static final String PREPROCESSED_DATA = "PREPROCESSED_DATA";
	public static final String UNASSIGNED_LISTS = "UNASSIGNED_LISTS";
	public static final String USER_LABELS = "USER_LABELS";
	public static final String DEVICE_TYPES = "DEVICE_TYPES";
	public static final String WORK_ORDER_STATUS = "WORK_ORDER_STATUS";
	
	public static final String CUSTOMER_FILE_PATH = "CUSTOMER_FILE_PATH";
	public static final String CUSTOMER_ACCOUNT_MAP = "CUSTOMER_ACCOUNT_MAP";
	public static final String HW_CONFIG_APP_MAP = "HW_CONFIG_APP_MAP";
    public static final String SEASONAL_LOAD_OPTION = "SEASONAL LOAD";
	
	// Customer account fields
	public static int acct_idx = 0;
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
	public static int inv_idx = 2;
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
    public static final int NUM_INV_FIELDS = inv_idx;
	
	// Appliance fields
	// IDX_LINE_NUM = 0
	// IDX_ACCOUNT_ID = 1
	// IDX_INV_ID = 2
	public static int app_idx = 3;
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
	
	// Work order fields
	// IDX_LINE_NUM = 0
	// IDX_ACCOUNT_ID = 1
	// IDX_INV_ID = 2
	public static int order_idx = 3;
	public static final int IDX_ORDER_NO = order_idx++;
	public static final int IDX_ORDER_TYPE = order_idx++;
	public static final int IDX_ORDER_STATUS = order_idx++;
	public static final int IDX_ORDER_CONTRACTOR = order_idx++;
	public static final int IDX_DATE_REPORTED = order_idx++;
	public static final int IDX_DATE_SCHEDULED = order_idx++;
	public static final int IDX_TIME_SCHEDULED = order_idx++;
	public static final int IDX_DATE_COMPLETED = order_idx++;
	public static final int IDX_ORDER_DESC = order_idx++;
	public static final int IDX_ACTION_TAKEN = order_idx++;
	public static final int NUM_ORDER_FIELDS = order_idx;
	
	// Customer residence fields
	// IDX_LINE_NUM = 0
	// IDX_ACCOUNT_ID = 1
	public static int res_idx = 2;
	public static final int IDX_RES_TYPE = res_idx++;
	public static final int IDX_CONSTRUCTION_TYPE = res_idx++;
	public static final int IDX_DECADE_BUILT = res_idx++;
	public static final int IDX_SQUARE_FEET = res_idx++;
	public static final int IDX_INSULATION_DEPTH = res_idx++;
	public static final int IDX_GENERAL_COND = res_idx++;
	public static final int IDX_MAIN_COOLING_SYS = res_idx++;
	public static final int IDX_MAIN_HEATING_SYS = res_idx++;
	public static final int IDX_NUM_OCCUPANTS = res_idx++;
	public static final int IDX_OWNERSHIP_TYPE = res_idx++;
	public static final int IDX_MAIN_FUEL_TYPE = res_idx++;
	public static final int IDX_RES_NOTES = res_idx++;
	public static final int NUM_RES_FIELDS = res_idx;
	
	// Table of list names, list labels, and old STARS list names
	public static final String[][] LIST_NAMES = {
		{"Substation", "Substation", "[Substations]"},
		{"DeviceType", "Device Type", ""},
		{"DeviceVoltage", "Device Voltage", "[Device Voltage]"},
		{"ServiceCompany", "Service Company", "[Contractor List 1]"},
		{"DeviceStatus", "Device Status", ""},
		{"LoadGroup", "Load Group", ""},
		{"LoadType", "Load Type", "[Load Type Description]"},
		{"Manufacturer", "Manufacturer", "[Manufacturers]"},
		{"ACTonnage", "AC Tonnage", "[Air Conditioner Tonage]"},
		{"WHNumberOfGallons", "WH Size", "[Water Heater Size]"},
		{"WHEnergySource", "WH Energy Source", "[Water Heater Energy Src]"},
		{"GENTransferSwitchType", "GEN Trans Switch Type", "[Trans Switch Type]"},
		{"GENTransferSwitchMfg", "GEN Trans Switch Manufacturer", "[Trans Switch Manu]"},
		{"IrrigationType", "IRR Type", "[Irrigation Type]"},
		{"IRREnergySource", "IRR Energy Source", "[Irrigation Energy Src]"},
		{"IRRHorsePower", "IRR Horse Power", "[Irrigation Horsepower]"},
		{"IRRMeterVoltage", "IRR Meter Voltage", "[Irrigation Meter Voltage]"},
		{"IRRMeterLocation", "IRR Meter Source", "[Irrigation Meter Src]"},
		{"IRRSoilType", "IRR Soil Type", "[Irrigation Soil Type]"},
		{"GrainDryerType", "GDry Type", "[Grain Dryer Type]"},
		{"GDEnergySource", "GDry Energy Source", "[Grain Dryer Blower Energy Src]"},
		{"GDHorsePower", "GDry Horse Power", "[Grain Dryer Blower Horsepower]"},
		{"GDHeatSource", "GDry Heat Source", "[Grain Dryer Blower Heat Src]"},
		{"GDBinSize", "GDry Bin Size", "[Grain Dryer Bin Size]"},
		{"HeatPumpType", "HP Type", "[Heat Pump Type]"},
		{"HeatPumpSize", "HP Size", "[Heat Pump Size]"},
		{"HPStandbySource", "HP Standby Heat", "[Heat Pump Standby Heat]"},
		{"StorageHeatType", "SH Type", "[Storage Heat Type]"},
		{"DFSwitchOverType", "DF Switch Over", ""},
		{"DFSecondarySource", "DF Secondary Src", "[Dual Fuel Secondary Src]"},
		{"ServiceStatus", "Service Status", ""},
		{"ServiceType", "Service Type", "[Work Order Type]"},
		{"ResidenceType", "Residence Type", "[Home Type]"},
		{"ConstructionMaterial", "Construction Material", "[Construction Type]"},
		{"DecadeBuilt", "Decade Built", "[Year Built]"},
		{"SquareFeet", "Square Feet", "[Square Footage]"},
		{"InsulationDepth", "Insulation Depth", "[Insulation Value]"},
		{"GeneralCondition", "General Condition", "[Caulk Condition]"},
		{"CoolingSystem", "Main Cooling System", "[Main Cooling System]"},
		{"HeatingSystem", "Main Heating System", "[Main Heating System]"},
		{"NumberOfOccupants", "Number of Occupants", ""},
		{"OwnershipType", "Ownership Type", ""},
		{"FuelType", "Main Fuel Type", "[Fuel Type]"},
		{"WHLocation", "", "[Water Heater Location]"},
		{"RateSchedule", "", "[Customer Rate Schedule]"}

	};
	
	// Tables of index of lists(in LIST_NAMES) and index of fields(in corresponding field definition above)
	public static final int[][] SERVINFO_LIST_FIELDS = {
		{0, IDX_SUBSTATION},
	};
	public static final int[][] INV_LIST_FIELDS = {
		{1, IDX_DEVICE_TYPE},
		{2, IDX_DEVICE_VOLTAGE},
		{3, IDX_SERVICE_COMPANY},
	};
	public static final int[][] RECV_LIST_FIELDS = {
		{4, IDX_DEVICE_STATUS},
		{5, IDX_R1_GROUP},
		{5, IDX_R2_GROUP},
		{5, IDX_R3_GROUP},
	};
	public static final int[][] APP_LIST_FIELDS = {
		{6, IDX_APP_DESC},
		{7, IDX_MANUFACTURER},
	};
	public static final int[][] AC_LIST_FIELDS = {
		{8, IDX_AC_TONNAGE},
	};
	public static final int[][] WH_LIST_FIELDS = {
		{9, IDX_WH_NUM_GALLONS},
		{10, IDX_WH_ENERGY_SRC},
	};
	public static final int[][] GEN_LIST_FIELDS = {
		{11, IDX_GEN_TRAN_SWITCH_TYPE},
		{12, IDX_GEN_TRAN_SWITCH_MFC},
	};
	public static final int[][] IRR_LIST_FIELDS = {
		{13, IDX_IRR_TYPE},
		{14, IDX_IRR_ENERGY_SRC},
		{15, IDX_IRR_HORSE_POWER},
		{16, IDX_IRR_METER_VOLT},
		{17, IDX_IRR_METER_LOC},
		{18, IDX_IRR_SOIL_TYPE},
	};
	public static final int[][] GDRY_LIST_FIELDS = {
		{19, IDX_GDRY_TYPE},
		{20, IDX_GDRY_ENERGY_SRC},
		{21, IDX_GDRY_HORSE_POWER},
		{22, IDX_GDRY_HEAT_SRC},
		{23, IDX_GDRY_BIN_SIZE},
	};
	public static final int[][] HP_LIST_FIELDS = {
		{24, IDX_HP_TYPE},
		{25, IDX_HP_SIZE},
		{26, IDX_HP_STANDBY_SRC},
	};
	public static final int[][] SH_LIST_FIELDS = {
		{27, IDX_SH_TYPE},
	};
	public static final int[][] DF_LIST_FIELDS = {
		{28, IDX_DF_SWITCH_OVER_TYPE},
		{29, IDX_DF_2ND_ENERGY_SRC},
	};
	public static final int[][] ORDER_LIST_FIELDS = {
		{30, IDX_ORDER_STATUS},
		{31, IDX_ORDER_TYPE},
		{3, IDX_ORDER_CONTRACTOR},
	};
	public static final int[][] RES_LIST_FIELDS = {
		{32, IDX_RES_TYPE},
		{33, IDX_CONSTRUCTION_TYPE},
		{34, IDX_DECADE_BUILT},
		{35, IDX_SQUARE_FEET},
		{36, IDX_INSULATION_DEPTH},
		{37, IDX_GENERAL_COND},
		{38, IDX_MAIN_COOLING_SYS},
		{39, IDX_MAIN_HEATING_SYS},
		{40, IDX_NUM_OCCUPANTS},
		{41, IDX_OWNERSHIP_TYPE},
		{42, IDX_MAIN_FUEL_TYPE},
	};
	
	// User label names
	public static final String LABEL_SI_CHAR1 = "SICharLabel1";
	public static final String LABEL_SI_CHAR2 = "SICharLabel2";
	public static final String LABEL_SI_DROPBOX1 = "SIDropBoxLabel1";
	public static final String LABEL_SI_DROPBOX2 = "SIDropBoxLabel2";
	public static final String LABEL_SI_CHECKBOX1 = "SICheckBoxLabel1";
	public static final String LABEL_SI_CHECKBOX2 = "SICheckBoxLabel2";
	public static final String LABEL_SI_NUMERIC1 = "SINumericLabel1";
	public static final String LABEL_SI_NUMERIC2 = "SINumericLabel2";
	public static final String LABEL_DI_CHAR1 = "DICharLabel1";
	public static final String LABEL_DI_DROPBOX1 = "DIDropBoxLabel1";
	public static final String LABEL_DC_CHAR1 = "DCCharLabel1";
	public static final String LABEL_DC_DROPBOX1 = "DCDropBoxLabel1";
	public static final String LABEL_DC_NUMERIC1 = "DCNumericLabel1";
	public static final String LABEL_PHONE1 = "Phone1Label";
	public static final String LABEL_PHONE2 = "Phone2Label";
	public static final String LABEL_PHONE3 = "Phone3Label";
	public static final String LABEL_CONTRACTOR1 = "ContractorLabel1";
	public static final String LABEL_CONTRACTOR2 = "ContractorLabel2";
	public static final String LABEL_LI_CHAR1 = "LICharLabel1";
	public static final String LABEL_LI_DROPBOX1 = "LIDropBoxLabel1";
	public static final String LABEL_LI_DROPBOX2 = "LIDropBoxLabel2";
	public static final String LABEL_LI_DROPBOX3 = "LIDropBoxLabel3";
	public static final String LABEL_LI_NUMERIC1 = "LINumericLabel1";
	public static final String LABEL_LI_NUMERIC2 = "LINumericLabel2";
	public static final String LABEL_LI_CHECKBOX1 = "LICheckBoxLabel1";
	public static final String LABEL_LI_CHECKBOX2 = "LICheckBoxLabel2";
	public static final String WO_STATUS_OPEN = "OPEN";
	public static final String WO_STATUS_CLOSED = "CLOSED";
	public static final String WO_STATUS_SCHEDULED = "SCHEDULED";
	public static final String WO_STATUS_WAITING = "WAITING";
	public static final String HARDWARE_ACTION_INSERT = "INSERT";
	public static final String HARDWARE_ACTION_UPDATE = "UPDATE";
	public static final String HARDWARE_ACTION_REMOVE = "REMOVE";
	
}