/*
 * Created on Nov 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ImportProblem;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.CreateApplianceAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.UpdateApplianceAction;
import com.cannontech.stars.web.action.UpdateCustAccountAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLoginAction;
import com.cannontech.stars.web.action.UpdateResidenceInfoAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ACType;
import com.cannontech.stars.xml.serialize.AirConditioner;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.BinSize;
import com.cannontech.stars.xml.serialize.BlowerEnergySource;
import com.cannontech.stars.xml.serialize.BlowerHeatSource;
import com.cannontech.stars.xml.serialize.BlowerHorsePower;
import com.cannontech.stars.xml.serialize.ConstructionMaterial;
import com.cannontech.stars.xml.serialize.ContactNotification;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.DecadeBuilt;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.DryerType;
import com.cannontech.stars.xml.serialize.DualFuel;
import com.cannontech.stars.xml.serialize.EnergySource;
import com.cannontech.stars.xml.serialize.GeneralCondition;
import com.cannontech.stars.xml.serialize.Generator;
import com.cannontech.stars.xml.serialize.GrainDryer;
import com.cannontech.stars.xml.serialize.HeatPump;
import com.cannontech.stars.xml.serialize.HorsePower;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.InsulationDepth;
import com.cannontech.stars.xml.serialize.Irrigation;
import com.cannontech.stars.xml.serialize.IrrigationType;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.MainCoolingSystem;
import com.cannontech.stars.xml.serialize.MainFuelType;
import com.cannontech.stars.xml.serialize.MainHeatingSystem;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.MeterLocation;
import com.cannontech.stars.xml.serialize.MeterVoltage;
import com.cannontech.stars.xml.serialize.NumberOfGallons;
import com.cannontech.stars.xml.serialize.NumberOfOccupants;
import com.cannontech.stars.xml.serialize.OwnershipType;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.PumpSize;
import com.cannontech.stars.xml.serialize.PumpType;
import com.cannontech.stars.xml.serialize.ResidenceType;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.SecondaryEnergySource;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.SoilType;
import com.cannontech.stars.xml.serialize.SquareFeet;
import com.cannontech.stars.xml.serialize.StandbySource;
import com.cannontech.stars.xml.serialize.StarsApp;
import com.cannontech.stars.xml.serialize.StarsCreateAppliance;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsCustAccount;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsUpdateAppliance;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUpdateResidenceInformation;
import com.cannontech.stars.xml.serialize.StorageHeat;
import com.cannontech.stars.xml.serialize.StorageType;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.serialize.SwitchOverType;
import com.cannontech.stars.xml.serialize.Tonnage;
import com.cannontech.stars.xml.serialize.TransferSwitchManufacturer;
import com.cannontech.stars.xml.serialize.TransferSwitchType;
import com.cannontech.stars.xml.serialize.Voltage;
import com.cannontech.stars.xml.serialize.WaterHeater;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;

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
	
	// Customer account fields
	public static int acct_idx = 0;
	public static final int IDX_LINE_NUM = acct_idx++;
	public static final int IDX_ACCOUNT_ID = acct_idx++;
	public static final int IDX_ACCOUNT_NO = acct_idx++;
	public static final int IDX_CUSTOMER_TYPE = acct_idx++;
	public static final int IDX_COMPANY_NAME = acct_idx++;
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

	public static String[] prepareFields(int numFields) {
		String[] fields = new String[ numFields ];
		for (int i = 0; i < numFields; i++)
			fields[i] = "";
		
		return fields;
	}
	
	private static void setStarsCustAccount(StarsCustAccount account, String[] fields, LiteStarsEnergyCompany energyCompany, ImportProblem problem) {
	    account.setAccountNumber( fields[IDX_ACCOUNT_NO] );
	    account.setIsCommercial( fields[IDX_CUSTOMER_TYPE].equalsIgnoreCase("COM") );
	    account.setCompany( fields[IDX_COMPANY_NAME] );
	    account.setAccountNotes( fields[IDX_ACCOUNT_NOTES] );
	    account.setPropertyNumber( fields[IDX_MAP_NO] );
	    account.setPropertyNotes( fields[IDX_PROP_NOTES] );
	
	    StreetAddress propAddr = new StreetAddress();
	    propAddr.setStreetAddr1( fields[IDX_STREET_ADDR1] );
	    propAddr.setStreetAddr2( fields[IDX_STREET_ADDR2] );
	    propAddr.setCity( fields[IDX_CITY] );
	    propAddr.setState( fields[IDX_STATE] );
	    propAddr.setZip( fields[IDX_ZIP_CODE] );
	    propAddr.setCounty( fields[IDX_COUNTY] );
	    account.setStreetAddress( propAddr );
	
		Substation starsSub = new Substation();
		starsSub.setEntryID( CtiUtilities.NONE_ID );
		if (fields[IDX_SUBSTATION].length() > 0) {
			try {
				// If importing old STARS data, this field is substation id
				starsSub.setEntryID( Integer.parseInt(fields[IDX_SUBSTATION]) );
			}
			catch (NumberFormatException e) {
				// Otherwise, this is the substation name inside ""
				String subName = fields[IDX_SUBSTATION].substring( 1, fields[IDX_SUBSTATION].length()-1 );
				YukonSelectionList subList = energyCompany.getYukonSelectionList( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
				
				for (int i = 0; i < subList.getYukonListEntries().size(); i++) {
					YukonListEntry sub = (YukonListEntry) subList.getYukonListEntries().get(i);
					if (sub.getEntryText().equalsIgnoreCase( subName )) {
						starsSub.setEntryID( sub.getEntryID() );
						break;
					}
				}
			}
		}
		
	    StarsSiteInformation siteInfo = new StarsSiteInformation();
	    siteInfo.setSubstation( starsSub );
	    siteInfo.setFeeder( fields[IDX_FEEDER] );
	    siteInfo.setPole( fields[IDX_POLE] );
	    siteInfo.setTransformerSize( fields[IDX_TRFM_SIZE] );
	    siteInfo.setServiceVoltage( fields[IDX_SERV_VOLT] );
	    account.setStarsSiteInformation( siteInfo );
	    
	    BillingAddress billAddr = new BillingAddress();
	    billAddr.setStreetAddr1( "" );
	    billAddr.setStreetAddr2( "" );
	    billAddr.setCity( "" );
	    billAddr.setState( "" );
	    billAddr.setZip( "" );
	    billAddr.setCounty( "" );
	    account.setBillingAddress( billAddr );
	    
	    PrimaryContact primContact = new PrimaryContact();
	    primContact.setLastName( fields[IDX_LAST_NAME] );
	    primContact.setFirstName( fields[IDX_FIRST_NAME] );
	    
	    try {
	    	ContactNotification homePhone = ServletUtils.createContactNotification(
					ServletUtils.formatPhoneNumber(fields[IDX_HOME_PHONE]), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE );
			if (homePhone != null) primContact.addContactNotification( homePhone );
	    }
	    catch (WebClientException e) {
			if (problem != null) problem.appendProblem( e.getMessage() );
	    }
	    
	    try {
			ContactNotification workPhone = ServletUtils.createContactNotification(
					ServletUtils.formatPhoneNumber(fields[IDX_WORK_PHONE]), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE );
			if (workPhone != null) primContact.addContactNotification( workPhone );
	    }
	    catch (WebClientException e) {
			if (problem != null) problem.appendProblem( e.getMessage() );
	    }
	    
	    ContactNotification email = ServletUtils.createContactNotification(
				fields[IDX_EMAIL], YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
	    email.setDisabled( true );
	    primContact.addContactNotification( email );
	    
	    account.setPrimaryContact( primContact );
	}
	
	private static StarsUpdateLogin createStarsUpdateLogin(String[] fields, LiteStarsEnergyCompany energyCompany) {
		StarsUpdateLogin login = new StarsUpdateLogin();
		login.setUsername( fields[IDX_USERNAME] );
		login.setPassword( fields[IDX_PASSWORD] );
		
		LiteYukonGroup[] custGroups = energyCompany.getResidentialCustomerGroups();
		if (custGroups != null && custGroups.length > 0) {
			if (fields[IDX_LOGIN_GROUP].length() > 0) {
				for (int i = 0; i < custGroups.length; i++) {
					if (custGroups[i].getGroupName().equalsIgnoreCase( fields[IDX_LOGIN_GROUP] )) {
						login.setGroupID( custGroups[i].getGroupID() );
						break;
					}
				}
			}
			else {
				login.setGroupID( custGroups[0].getGroupID() );
			}
		}
		
		return login;
	}
	
	public static LiteStarsCustAccountInformation newCustomerAccount(String[] fields, StarsYukonUser user,
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint, ImportProblem problem) throws Exception
	{
		// Build the request message
		StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
		
		StarsCustomerAccount account = new StarsCustomerAccount();
		setStarsCustAccount( account, fields, energyCompany, problem );
		newAccount.setStarsCustomerAccount( account );
		
		if (fields[IDX_USERNAME].trim().length() > 0)
			newAccount.setStarsUpdateLogin( createStarsUpdateLogin(fields, energyCompany) );
		
		return NewCustAccountAction.newCustomerAccount(newAccount, user, energyCompany, checkConstraint);
	}
	
	public static void updateCustomerAccount(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany,
		ImportProblem problem) throws Exception
	{
	    StarsUpdateCustomerAccount updateAccount = new StarsUpdateCustomerAccount();
	    setStarsCustAccount( updateAccount, fields, energyCompany, problem );
	    
	    UpdateCustAccountAction.updateCustomerAccount( updateAccount, liteAcctInfo, energyCompany );
	    
		int loginID = ContactFuncs.getContact( liteAcctInfo.getCustomer().getPrimaryContactID() ).getLoginID();
		if (loginID == UserUtils.USER_STARS_DEFAULT_ID && fields[IDX_USERNAME].trim().length() > 0) {
			StarsUpdateLogin login = createStarsUpdateLogin( fields, energyCompany );
			UpdateLoginAction.updateLogin( login, liteAcctInfo, energyCompany );
		}
		
		// Delete the stars object from cache, so the user will see a fresh new copy
		energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
	}
	
	private static int getDeviceTypeID(LiteStarsEnergyCompany energyCompany, String deviceType) {
		try {
			// If importing old STARS data, the string is the device type id
			return Integer.parseInt( deviceType );
		}
		catch (NumberFormatException e) {
			YukonSelectionList devTypeList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE );
			for (int i = 0; i < devTypeList.getYukonListEntries().size(); i++) {
				YukonListEntry entry = (YukonListEntry) devTypeList.getYukonListEntries().get(i);
				if (entry.getEntryText().equalsIgnoreCase( deviceType ))
					return entry.getEntryID();
			}
		}
		
		return -1;
	}
	
	private static void setStarsInventory(StarsInv inv, String[] fields, LiteStarsEnergyCompany energyCompany, ImportProblem problem) {
		if (fields[IDX_ALT_TRACK_NO].length() > 0)
			inv.setAltTrackingNumber( fields[IDX_ALT_TRACK_NO] );
		if (fields[IDX_INV_NOTES].length() > 0)
			inv.setNotes( fields[IDX_INV_NOTES] );
		
		StarsUtils.starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
		
		if (fields[IDX_RECEIVE_DATE].length() > 0) {
			try {
				inv.setReceiveDate( StarsUtils.starsDateFormat.parse(fields[IDX_RECEIVE_DATE]) );
			}
			catch (java.text.ParseException e) {}
			
			if (inv.getReceiveDate() == null && problem != null)
				problem.appendProblem( "Invalid receive date format '" + fields[IDX_RECEIVE_DATE] + "'" );
		}
		
		if (fields[IDX_INSTALL_DATE].length() > 0) {
			inv.setInstallDate( ServletUtil.parseDateStringLiberally(
					fields[IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone()) );
			
			if (inv.getInstallDate() == null) {
				try {
					inv.setInstallDate( StarsUtils.starsDateFormat.parse(fields[IDX_INSTALL_DATE]) );
				}
				catch (java.text.ParseException e) {}
			}
			
			if (inv.getInstallDate() == null && problem != null)
				problem.appendProblem( "Invalid install date format '" + fields[IDX_INSTALL_DATE] + "'" );
		}
		
		if (fields[IDX_REMOVE_DATE].length() > 0) {
			inv.setRemoveDate( ServletUtil.parseDateStringLiberally(
					fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
			
			if (inv.getRemoveDate() == null && problem != null)
				problem.appendProblem( "Invalid remove date format '" + fields[IDX_REMOVE_DATE] + "'" );
		}
		
		if (fields[IDX_DEVICE_VOLTAGE].length() > 0) {
			// If importing old STARS data, this field is the device voltage id
			Voltage volt = new Voltage();
			volt.setEntryID( Integer.parseInt(fields[IDX_DEVICE_VOLTAGE]) );
			inv.setVoltage( volt );
		}
		
		if (fields[IDX_DEVICE_STATUS].length() > 0) {
			// If importing old STARS data, this field is the device staus id
			DeviceStatus status = new DeviceStatus();
			status.setEntryID( Integer.parseInt(fields[IDX_DEVICE_STATUS]) );
			inv.setDeviceStatus( status );
		}
		else
			inv.setDeviceStatus( null );
		
		if (fields[IDX_SERVICE_COMPANY].length() > 0) {
			InstallationCompany company = new InstallationCompany();
			company.setEntryID( CtiUtilities.NONE_ID );
			
			try {
				// If importing old STARS data, this field is the service company id
				company.setEntryID( Integer.parseInt(fields[IDX_SERVICE_COMPANY]) );
			}
			catch (NumberFormatException e) {
				// Otherwise, this is the service company name inside ""
				String companyName = fields[IDX_SERVICE_COMPANY].substring( 1, fields[IDX_SERVICE_COMPANY].length()-1 );
				ArrayList companies = energyCompany.getAllServiceCompanies();
				
				for (int i = 0; i < companies.size(); i++) {
					LiteServiceCompany entry = (LiteServiceCompany) companies.get(i);
					if (entry.getCompanyName().equalsIgnoreCase( companyName )) {
						company.setEntryID( entry.getCompanyID() );
						break;
					}
				}
			}
			
			inv.setInstallationCompany( company );
		}
		
		boolean isLMHardware = false;
		if (fields[IDX_DEVICE_TYPE].length() > 0) {
			DeviceType devType = new DeviceType();
			devType.setEntryID( Integer.parseInt(fields[IDX_DEVICE_TYPE]) );
			inv.setDeviceType( devType );
			
			int categoryID = ECUtils.getInventoryCategoryID(devType.getEntryID(), energyCompany);
			isLMHardware = ECUtils.isLMHardware(categoryID);
		}
		
		if (isLMHardware) {
			LMHardware hw = new LMHardware();
			hw.setManufacturerSerialNumber( fields[IDX_SERIAL_NO] );
			inv.setLMHardware( hw );
		}
		else {
			// Now we only have MCTs
			MCT mct = new MCT();
			mct.setDeviceName( fields[IDX_DEVICE_NAME] );
			inv.setMCT( mct );
			
			// If the device is not found in Yukon, use the label field to store the device name
			if (inv.getDeviceID() == 0)
				inv.setDeviceLabel( fields[IDX_DEVICE_NAME] );
		}
	}
	
	public static LiteInventoryBase insertLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint, ImportProblem problem) throws Exception
	{
		// Check inventory and build request message
		int devTypeID = getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new WebClientException("Invalid device type \"" + fields[IDX_DEVICE_TYPE] + "\"");
		
		StarsCreateLMHardware createHw = null;
		LiteInventoryBase liteInv = null;
		
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID, energyCompany );
		if (ECUtils.isLMHardware( categoryID )) {
			if (checkConstraint)
				liteInv = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
		}
		else {
			if (fields[IDX_DEVICE_NAME].equals(""))
				throw new WebClientException( ImportProblem.NO_DEVICE_NAME );
			liteInv = energyCompany.searchForDevice( categoryID, fields[IDX_DEVICE_NAME] );
			if (liteInv == null)
				throw new WebClientException( ImportProblem.DEVICE_NAME_NOT_FOUND );
		}
		
		if (liteInv != null) {
			if (liteInv.getAccountID() > 0)
				throw new WebClientException( "Hardware already exists and assigned to an account" );
			
			createHw = new StarsCreateLMHardware();
			StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
			createHw.setRemoveDate( new java.util.Date(0) );
			createHw.setInstallationNotes( "" );
		}
		else {
			createHw = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
		}
		
		createHw.setInstallDate( new java.util.Date(0) );
		fields[IDX_DEVICE_TYPE] = String.valueOf( devTypeID );
		setStarsInventory( createHw, fields, energyCompany, problem );
	    
		liteInv = CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
		
		if (liteAcctInfo != null)
			energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
	    
		return liteInv;
	}
	
	public static LiteInventoryBase updateLMHardware(String[] fields, LiteInventoryBase liteInv,
		LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, ImportProblem problem) throws Exception
	{
		// Check inventory and build request message
		int devTypeID = getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new WebClientException("Invalid device type '" + fields[IDX_DEVICE_TYPE] + "'");
		
		StarsUpdateLMHardware updateHw = new StarsUpdateLMHardware();
		StarsLiteFactory.setStarsInv( updateHw, liteInv, energyCompany );
		updateHw.setInstallDate( new java.util.Date() );
		updateHw.setRemoveDate( new java.util.Date(0) );
		updateHw.setInstallationNotes( "" );
		
		fields[IDX_DEVICE_TYPE] = String.valueOf( devTypeID );
		setStarsInventory( updateHw, fields, energyCompany, problem );
		
		UpdateLMHardwareAction.updateInventory( updateHw, liteInv, energyCompany );
		
		if (liteAcctInfo != null)
			energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		
		return energyCompany.getInventory( liteInv.getInventoryID(), true );
	}
	
	public static void removeLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, ImportProblem problem) throws Exception
	{
		// Check if the hardware to be deleted exists
		LiteStarsLMHardware liteHw = null;
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			LiteStarsLMHardware hardware = (LiteStarsLMHardware) energyCompany.getInventory(invID, true);
			
			if (hardware.getManufacturerSerialNumber().equals( fields[IDX_SERIAL_NO] )) {
				liteHw = hardware;
				break;
			}
		}
		
		if (liteHw == null)
			throw new WebClientException("Hardware with serial #" + fields[IDX_SERIAL_NO] + " doesn't exist");
		
		StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
		deleteHw.setInventoryID( liteHw.getInventoryID() );
		deleteHw.setDeleteFromInventory( false );
		if (fields[IDX_REMOVE_DATE].length() > 0) {
			deleteHw.setRemoveDate( ServletUtil.parseDateStringLiberally(
					fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
			
			if (deleteHw.getRemoveDate() == null && problem != null)
				problem.appendProblem( "Invalid remove date format '" + fields[IDX_REMOVE_DATE] + "'" );
		}
		
		DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany );
		energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
	}
	
	/**
	 * @param programs Array of (ProgramID, ApplianceCategoryID, GroupID, LoadNumber).
	 * The ApplianceCategoryID, GroupID, and LoadNumber are optional, set them to -1 if you don't want to privide the value.
	 * @param liteInv The hardware the programs are attached to
	 */
	public static void programSignUp(int[][] programs, LiteStarsCustAccountInformation liteAcctInfo,
		LiteInventoryBase liteInv, LiteStarsEnergyCompany energyCompany) throws Exception
	{
		// Build request message
		StarsSULMPrograms suPrograms = new StarsSULMPrograms();
		for (int i = 0; i < programs.length; i++) {
			SULMProgram suProg = new SULMProgram();
			suProg.setProgramID( programs[i][0] );
			if (programs[i][1] != -1)
				suProg.setApplianceCategoryID( programs[i][1] );
			if (programs[i][2] != -1)
				suProg.setAddressingGroupID( programs[i][2] );
			if (programs[i][3] != -1)
				suProg.setLoadNumber( programs[i][3] );
			suPrograms.addSULMProgram( suProg );
		}
		
		StarsProgramSignUp progSignUp = new StarsProgramSignUp();
		progSignUp.setStarsSULMPrograms( suPrograms );
	    
	    ProgramSignUpAction.updateProgramEnrollment( progSignUp, liteAcctInfo, liteInv, energyCompany );
		energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
	}
	
	private static int getApplianceCategoryID(LiteStarsEnergyCompany energyCompany, String appType) {
		ArrayList appCats = energyCompany.getAllApplianceCategories();
		for (int i = 0; i < appCats.size(); i++) {
			LiteApplianceCategory appCat = (LiteApplianceCategory) appCats.get(i);
			if (appCat.getDescription().equalsIgnoreCase( appType ))
				return appCat.getApplianceCategoryID();
		}
		
		return -1;
	}
	
	private static void setStarsAppliance(StarsApp app, String[] fields, LiteStarsEnergyCompany energyCompany) {
		if (fields[IDX_APP_DESC].length() > 0)	// Importing old STARS data
			app.setApplianceCategoryID( Integer.parseInt(fields[IDX_APP_DESC]) );
		else
			app.setApplianceCategoryID( getApplianceCategoryID(energyCompany, fields[IDX_APP_TYPE]) );
		
		app.setNotes( fields[IDX_APP_NOTES] );
		app.setModelNumber( "" );
		app.setEfficiencyRating( -1 );
		
		if (fields[IDX_YEAR_MADE].length() > 0) {
			try {
				app.setYearManufactured( Integer.parseInt(fields[IDX_YEAR_MADE]) );
			}
			catch (NumberFormatException e) {}
		}
		
		if (fields[IDX_APP_KW].length() > 0) {
			int kwCap = (int) Double.parseDouble(fields[IDX_APP_KW]);
			if (kwCap >= 0) app.setKWCapacity( kwCap );
		}
		
		Location location = new Location();
		location.setEntryID( CtiUtilities.NONE_ID );
		app.setLocation( location );
		
		Manufacturer mfc = new Manufacturer();
		if (fields[IDX_MANUFACTURER].length() > 0)
			mfc.setEntryID( Integer.parseInt(fields[IDX_MANUFACTURER]) );
		else
			mfc.setEntryID( CtiUtilities.NONE_ID );
		app.setManufacturer( mfc );
		
		if (fields[IDX_APP_CAT_DEF_ID].equals("")) return;
		int catDefID = Integer.parseInt( fields[IDX_APP_CAT_DEF_ID] );
		
		if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
			AirConditioner ac = new AirConditioner();
			
			Tonnage tonnage = new Tonnage();
			tonnage.setEntryID( Integer.parseInt(fields[IDX_AC_TONNAGE]) );
			ac.setTonnage( tonnage );
			
			ACType type = new ACType();
			type.setEntryID( 0 );
			ac.setACType( type );
			
			app.setAirConditioner( ac );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
			WaterHeater wh = new WaterHeater();
			
			NumberOfGallons numGal = new NumberOfGallons();
			numGal.setEntryID( Integer.parseInt(fields[IDX_WH_NUM_GALLONS]) );
			wh.setNumberOfGallons( numGal );
			
			EnergySource es = new EnergySource();
			es.setEntryID( Integer.parseInt(fields[IDX_WH_ENERGY_SRC]) );
			wh.setEnergySource( es );
			
			if (fields[IDX_WH_NUM_ELEMENTS].length() > 0) {
				int numElmt = Integer.parseInt( fields[IDX_WH_NUM_ELEMENTS] );
				if (numElmt >= 0) wh.setNumberOfElements( numElmt );
			}
			
			app.setWaterHeater( wh );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
			Generator gen = new Generator();
			
			TransferSwitchType tsType = new TransferSwitchType();
			tsType.setEntryID( Integer.parseInt(fields[IDX_GEN_TRAN_SWITCH_TYPE]) );
			gen.setTransferSwitchType( tsType );
			
			TransferSwitchManufacturer tsMfc = new TransferSwitchManufacturer();
			tsMfc.setEntryID( Integer.parseInt(fields[IDX_GEN_TRAN_SWITCH_MFC]) );
			gen.setTransferSwitchManufacturer( tsMfc );
			
			if (fields[IDX_GEN_CAPACITY].length() > 0) {
				int peakKW = Integer.parseInt( fields[IDX_GEN_CAPACITY] );
				if (peakKW >= 0) gen.setPeakKWCapacity( peakKW );
			}
			
			if (fields[IDX_GEN_FUEL_CAP].length() > 0) {
				int fuelCap = Integer.parseInt( fields[IDX_GEN_FUEL_CAP] );
				if (fuelCap >= 0) gen.setFuelCapGallons( fuelCap );
			}
			
			if (fields[IDX_GEN_START_DELAY].length() > 0) {
				int startDelay = Integer.parseInt( fields[IDX_GEN_START_DELAY] );
				if (startDelay >= 0) gen.setStartDelaySeconds( startDelay );
			}
			
			app.setGenerator( gen );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
			Irrigation irr = new Irrigation();
			
			IrrigationType type = new IrrigationType();
			type.setEntryID( Integer.parseInt(fields[IDX_IRR_TYPE]) );
			irr.setIrrigationType( type );
			
			HorsePower horsePwr = new HorsePower();
			horsePwr.setEntryID( Integer.parseInt(fields[IDX_IRR_HORSE_POWER]) );
			irr.setHorsePower( horsePwr );
			
			EnergySource energySrc = new EnergySource();
			energySrc.setEntryID( Integer.parseInt(fields[IDX_IRR_ENERGY_SRC]) );
			irr.setEnergySource( energySrc );
			
			SoilType soilType = new SoilType();
			soilType.setEntryID( Integer.parseInt(fields[IDX_IRR_SOIL_TYPE]) );
			irr.setSoilType( soilType );
			
			MeterLocation meterLoc = new MeterLocation();
			meterLoc.setEntryID( Integer.parseInt(fields[IDX_IRR_METER_LOC]) );
			irr.setMeterLocation( meterLoc );
			
			MeterVoltage meterVolt = new MeterVoltage();
			meterVolt.setEntryID( Integer.parseInt(fields[IDX_IRR_METER_VOLT]) );
			irr.setMeterVoltage( meterVolt );
			
			app.setIrrigation( irr );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
			GrainDryer gd = new GrainDryer();
			
			DryerType type = new DryerType();
			type.setEntryID( Integer.parseInt(fields[IDX_GDRY_TYPE]) );
			gd.setDryerType( type );
			
			BinSize binSize = new BinSize();
			binSize.setEntryID( Integer.parseInt(fields[IDX_GDRY_BIN_SIZE]) );
			gd.setBinSize( binSize );
			
			BlowerEnergySource energySrc = new BlowerEnergySource();
			energySrc.setEntryID( Integer.parseInt(fields[IDX_GDRY_ENERGY_SRC]) );
			gd.setBlowerEnergySource( energySrc );
			
			BlowerHorsePower horsePwr = new BlowerHorsePower();
			horsePwr.setEntryID( Integer.parseInt(fields[IDX_GDRY_HORSE_POWER]) );
			gd.setBlowerHorsePower( horsePwr );
			
			BlowerHeatSource heatSrc = new BlowerHeatSource();
			heatSrc.setEntryID( Integer.parseInt(fields[IDX_GDRY_HEAT_SRC]) );
			gd.setBlowerHeatSource( heatSrc );
			
			app.setGrainDryer( gd );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
			HeatPump hp = new HeatPump();
			
			PumpType type = new PumpType();
			type.setEntryID( Integer.parseInt(fields[IDX_HP_TYPE]) );
			hp.setPumpType( type );
			
			PumpSize size = new PumpSize();
			type.setEntryID( Integer.parseInt(fields[IDX_HP_SIZE]) );
			hp.setPumpSize( size );
			
			StandbySource standbySrc = new StandbySource();
			standbySrc.setEntryID( Integer.parseInt(fields[IDX_HP_STANDBY_SRC]) );
			hp.setStandbySource( standbySrc );
			
			if (fields[IDX_HP_RESTART_DELAY].length() > 0) {
				int restartDelay = Integer.parseInt( fields[IDX_HP_RESTART_DELAY] );
				if (restartDelay >= 0) hp.setRestartDelaySeconds( restartDelay );
			}
			
			app.setHeatPump( hp );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
			StorageHeat sh = new StorageHeat();
			
			StorageType type = new StorageType();
			type.setEntryID( Integer.parseInt(fields[IDX_SH_TYPE]) );
			sh.setStorageType( type );
			
			if (fields[IDX_SH_CAPACITY].length() > 0) {
				int peakKW = Integer.parseInt( fields[IDX_SH_CAPACITY] );
				if (peakKW >= 0) sh.setPeakKWCapacity( peakKW );
			}
			
			if (fields[IDX_SH_RECHARGE_TIME].length() > 0) {
				int rechargeTime = Integer.parseInt( fields[IDX_SH_RECHARGE_TIME] );
				if (rechargeTime >= 0) sh.setHoursToRecharge( rechargeTime );
			}
			
			app.setStorageHeat( sh );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
			DualFuel df = new DualFuel();
			
			SwitchOverType soType = new SwitchOverType();
			soType.setEntryID( Integer.parseInt(fields[IDX_DF_SWITCH_OVER_TYPE]) );
			df.setSwitchOverType( soType );
			
			if (fields[IDX_DF_2ND_CAPACITY].length() > 0) {
				int kwCap2 = Integer.parseInt( fields[IDX_DF_2ND_CAPACITY] );
				if (kwCap2 >= 0) df.setSecondaryKWCapacity( kwCap2 );
			}
			
			SecondaryEnergySource energySrc = new SecondaryEnergySource();
			energySrc.setEntryID( Integer.parseInt(fields[IDX_DF_2ND_ENERGY_SRC]) );
			df.setSecondaryEnergySource( energySrc );
			
			app.setDualFuel( df );
		}
	}
	
	public static void newAppliance(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		StarsCreateAppliance newApp = new StarsCreateAppliance();
		setStarsAppliance( newApp, fields, energyCompany );
		
		CreateApplianceAction.createAppliance( newApp, liteAcctInfo, energyCompany );
		energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
	}
	
	public static void updateAppliance(String[] fields, int appID, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws Exception
	{
		LiteStarsAppliance liteApp = null;
		for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
			LiteStarsAppliance lApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
			if (lApp.getApplianceID() == appID) {
				liteApp = lApp;
				break;
			}
		}
		
		if (liteApp != null) {
			StarsUpdateAppliance updateApp = new StarsUpdateAppliance();
			updateApp.setApplianceID( appID );
			setStarsAppliance( updateApp, fields, energyCompany );
			
			UpdateApplianceAction.updateAppliance( updateApp, liteAcctInfo );
			energyCompany.deleteStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		}
		else
			newAppliance( fields, liteAcctInfo, energyCompany );
	}
	
	private static String formatTimeString(String str) {
		StringBuffer strBuf = new StringBuffer( str.toUpperCase() );
		strBuf.reverse();
		
		while (strBuf.length() > 0) {
			if (Character.isDigit(strBuf.charAt(0)) || strBuf.charAt(0) == 'M')
				break;
			
			if (strBuf.charAt(0) == 'A' || strBuf.charAt(0) == 'P') {
				strBuf.insert(0, 'M');
				break;
			}
			
			strBuf.deleteCharAt(0);
		}
		
		return strBuf.reverse().toString();
	}
	
	public static void newServiceRequest(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint, ImportProblem problem) throws Exception
	{
		StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();
		createOrder.setOrderNumber( fields[IDX_ORDER_NO] );
		createOrder.setDescription( fields[IDX_ORDER_DESC] );
		createOrder.setActionTaken( fields[IDX_ACTION_TAKEN] );
		createOrder.setOrderedBy( "" );
		
		if (liteAcctInfo != null)
			createOrder.setAccountID( liteAcctInfo.getAccountID() );
		else
			createOrder.setAccountID( CtiUtilities.NONE_ID );
		
		StarsUtils.starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
		
		if (fields[IDX_DATE_REPORTED].length() > 0) {
			try {
				createOrder.setDateReported( StarsUtils.starsDateFormat.parse(fields[IDX_DATE_REPORTED]) );
			}
			catch (java.text.ParseException e) {}
			
			if (createOrder.getDateReported() == null && problem != null)
				problem.appendProblem( "Invalid report date format '" + fields[IDX_DATE_REPORTED] + "'" );
		}
		
		if (fields[IDX_DATE_COMPLETED].length() > 0) {
			try {
				createOrder.setDateCompleted( StarsUtils.starsDateFormat.parse(fields[IDX_DATE_COMPLETED]) );
			}
			catch (java.text.ParseException e) {}
			
			if (createOrder.getDateCompleted() == null && problem != null)
				problem.appendProblem( "Invalid complete date format '" + fields[IDX_DATE_COMPLETED] + "'" );
		}
		
		if (fields[IDX_DATE_SCHEDULED].length() > 0) {
			try {
				Date datePart = StarsUtils.starsDateFormat.parse( fields[IDX_DATE_SCHEDULED] );
				Date timePart = ServletUtils.parseTime(
						formatTimeString(fields[IDX_TIME_SCHEDULED]), energyCompany.getDefaultTimeZone() );
				
				if (timePart != null) {
					Calendar dateCal = Calendar.getInstance();
					dateCal.setTime( datePart );
					Calendar timeCal = Calendar.getInstance();
					timeCal.setTime( timePart );
					dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
					dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
					dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
					createOrder.setDateScheduled( dateCal.getTime() );
				}
				else
					createOrder.setDateScheduled( datePart );
			}
			catch (java.text.ParseException e) {}
			
			if (createOrder.getDateScheduled() == null && problem != null)
				problem.appendProblem( "Invalid schedule date format '" + fields[IDX_TIME_SCHEDULED] + "'" );
		}
		
		ServiceType type = new ServiceType();
		type.setEntryID( Integer.parseInt(fields[IDX_ORDER_TYPE]) );
		createOrder.setServiceType( type );
		
		CurrentState state = new CurrentState();
		state.setEntryID( Integer.parseInt(fields[IDX_ORDER_STATUS]) );
		createOrder.setCurrentState( state );
		
		ServiceCompany company = new ServiceCompany();
		company.setEntryID( Integer.parseInt(fields[IDX_ORDER_CONTRACTOR]) );
		createOrder.setServiceCompany( company );
	    
		CreateServiceRequestAction.createServiceRequest(createOrder, liteAcctInfo, energyCompany, checkConstraint);
	}
	
	public static void newResidenceInfo(String[] fields, LiteStarsCustAccountInformation liteAcctInfo)
		throws Exception
	{
		StarsUpdateResidenceInformation updateResInfo = new StarsUpdateResidenceInformation();
		updateResInfo.setNotes( fields[IDX_RES_NOTES] );
		
		ResidenceType resType = new ResidenceType();
		resType.setEntryID( Integer.parseInt(fields[IDX_RES_TYPE]) );
		updateResInfo.setResidenceType( resType );
		
		ConstructionMaterial constrType = new ConstructionMaterial();
		constrType.setEntryID( Integer.parseInt(fields[IDX_CONSTRUCTION_TYPE]) );
		updateResInfo.setConstructionMaterial( constrType );
		
		DecadeBuilt decade = new DecadeBuilt();
		decade.setEntryID( Integer.parseInt(fields[IDX_DECADE_BUILT]) );
		updateResInfo.setDecadeBuilt( decade );
		
		SquareFeet sqrFeet = new SquareFeet();
		sqrFeet.setEntryID( Integer.parseInt(fields[IDX_SQUARE_FEET]) );
		updateResInfo.setSquareFeet( sqrFeet );
		
		InsulationDepth insulDepth = new InsulationDepth();
		insulDepth.setEntryID( Integer.parseInt(fields[IDX_INSULATION_DEPTH]) );
		updateResInfo.setInsulationDepth( insulDepth );
		
		GeneralCondition genCond = new GeneralCondition();
		genCond.setEntryID( Integer.parseInt(fields[IDX_GENERAL_COND]) );
		updateResInfo.setGeneralCondition( genCond );
		
		MainCoolingSystem coolSys = new MainCoolingSystem();
		coolSys.setEntryID( Integer.parseInt(fields[IDX_MAIN_COOLING_SYS]) );
		updateResInfo.setMainCoolingSystem( coolSys );
		
		MainHeatingSystem heatSys = new MainHeatingSystem();
		heatSys.setEntryID( Integer.parseInt(fields[IDX_MAIN_HEATING_SYS]) );
		updateResInfo.setMainHeatingSystem( heatSys );
		
		NumberOfOccupants numOccpt = new NumberOfOccupants();
		numOccpt.setEntryID( Integer.parseInt(fields[IDX_NUM_OCCUPANTS]) );
		updateResInfo.setNumberOfOccupants( numOccpt );
		
		OwnershipType ownType = new OwnershipType();
		ownType.setEntryID( Integer.parseInt(fields[IDX_OWNERSHIP_TYPE]) );
		updateResInfo.setOwnershipType( ownType );
		
		MainFuelType fuelType = new MainFuelType();
		fuelType.setEntryID( Integer.parseInt(fields[IDX_MAIN_FUEL_TYPE]) );
		updateResInfo.setMainFuelType( fuelType );
		
		UpdateResidenceInfoAction.updateResidenceInformation( updateResInfo, liteAcctInfo );
	}
}
