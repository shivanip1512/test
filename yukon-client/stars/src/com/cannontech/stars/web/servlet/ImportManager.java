/*
 * Created on Feb 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ProgressChecker;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.ImportCustAccountsTask;
import com.cannontech.stars.util.task.ImportStarsDataTask;
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
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.DecadeBuilt;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.DeviceType;
import com.cannontech.stars.xml.serialize.DryerType;
import com.cannontech.stars.xml.serialize.DualFuel;
import com.cannontech.stars.xml.serialize.Email;
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
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
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
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;

interface ImportFileParser {
	String[] populateFields(String line) throws Exception;
}

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportManager extends HttpServlet {
    
	public static final String PREPROCESSED_DATA = "PREPROCESSED_DATA";
	public static final String UNASSIGNED_LISTS = "UNASSIGNED_LISTS";
	
	// Table of list names and list labels
	public static final String[][] LIST_NAMES = {
		{"Substation", "Substation"},
		{"DeviceType", "Device Type"},
		{"DeviceVoltage", "Device Voltage"},
		{"ServiceCompany", "Service Company"},
		{"DeviceStatus", "Device Status"},
		{"LoadGroup", "Load Group"},
		{"ApplianceType", "Appliance Type"},
		{"Manufacturer", "Manufacturer"},
		{"ACTonnage", "AC Tonnage"},
		{"WHNumberOfGallons", "WH Size"},
		{"WHEnergySource", "WH Energy Source"},
		{"GENTransferSwitchType", "GEN Trans Switch Type"},
		{"GENTransferSwitchMfg", "GEN Trans Switch Manufacturer"},
		{"IrrigationType", "IRR Type"},
		{"IRREnergySource", "IRR Energy Source"},
		{"IRRHorsePower", "IRR Horse Power"},
		{"IRRMeterVoltage", "IRR Meter Voltage"},
		{"IRRMeterLocation", "IRR Location Source"},
		{"IRRSoilType", "IRR Soil Type"},
		{"GrainDryerType", "GDry Type"},
		{"GDEnergySource", "GDry Energy Source"},
		{"GDHorsePower", "GDry Horse Power"},
		{"GDHeatSource", "GDry Heat Source"},
		{"GDBinSize", "GDry Bin Size"},
		{"HeatPumpType", "HP Type"},
		{"HeatPumpSize", "HP Size"},
		{"HPStandbySource", "HP Standby Heat"},
		{"StorageHeatType", "SH Type"},
		{"DFSwitchOverType", "DF Secondary Src"},
		{"DFSecondarySource", "DF Switch Over"},
		{"ServiceStatus", "Service Status"},
		{"ServiceType", "Service Type"},
		{"ResidenceType", "Residence Type"},
		{"ConstructionMaterial", "Construction Material"},
		{"DecadeBuilt", "Decade Built"},
		{"SquareFeet", "Square Feet"},
		{"InsulationDepth", "Insulation Depth"},
		{"GeneralCondition", "General Condition"},
		{"CoolingSystem", "Main Cooling System"},
		{"HeatingSystem", "Main Heating System"},
		{"NumberOfOccupants", "Number of Occupants"},
		{"OwnershipType", "Ownership Type"},
		{"FuelType", "Main Fuel Type"},
	};
	
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
	public static final int IDX_SUBSTATION = acct_idx++;
	public static final int IDX_FEEDER = acct_idx++;
	public static final int IDX_POLE = acct_idx++;
	public static final int IDX_TRFM_SIZE = acct_idx++;
	public static final int IDX_SERV_VOLT = acct_idx++;
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
	public static final int IDX_APP_TYPE = app_idx++;
	public static final int IDX_APP_NOTES = app_idx++;
	public static final int IDX_MANUFACTURER = app_idx++;
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
		{6, IDX_APP_TYPE},
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
    
	public static final String HARDWARE_ACTION_INSERT = "INSERT";
	public static final String HARDWARE_ACTION_UPDATE = "UPDATE";
	public static final String HARDWARE_ACTION_REMOVE = "REMOVE";
    
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
    
	private static StreamTokenizer prepareStreamTokenzier(String line) {
		StreamTokenizer st = new StreamTokenizer( new StringReader(line) );
		st.resetSyntax();
		st.ordinaryChar( ',' );
		st.quoteChar( '"' );
		st.wordChars( 'a', 'z' );
		st.wordChars( 'A', 'Z' );
		st.wordChars( '0', '9' );
		st.wordChars( ' ', ' ' );
		st.wordChars( '-', '-' );
		st.wordChars( '/', '/' );
		st.wordChars( '.', '.' );
		st.wordChars( '#', '#' );
		st.wordChars( '@', '@' );
		st.wordChars( '(', ')' );
		st.wordChars( '!', '!' );
		st.wordChars( '%', '%' );
		st.wordChars( ':', ':' );
		
		return st;
	}
    
	private static String[] prepareFields(int numFields, StreamTokenizer st, boolean parseLineNo)
	throws java.io.IOException {
		String[] fields = new String[ numFields ];
		for (int i = 0; i < numFields; i++)
			fields[i] = "";
		
		if (parseLineNo) {
			st.nextToken();
			if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LINE_NUM] = st.sval;
			if (st.ttype != ',') st.nextToken();
		}
		
		return fields;
	}
    
	private static final Hashtable parsers = new Hashtable();
	static {
		parsers.put("Idaho", new ImportFileParser() {
			/** Idaho Power Customer Information
			 * COL_NUM:	COL_NAME
			 * 1		----
			 * 2		LAST_NAME
			 * 3		FIRST_NAME
			 * 4		----
			 * 5,6		STREET_ADDR1
			 * 7		CITY
			 * 8		STATE
			 * 9		ZIP_CODE
			 * 10		HOME_PHONE
			 * 11		ACCOUNT_NO
			 * 12		SERIAL_NO(THERMOSTAT)
			 * 13		INSTALL_DATE
			 * 14		SERVICE_COMPANY
			 */
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_STREET_ADDR1] += " " + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = st.sval;
				
				fields[IDX_USERNAME] = fields[IDX_FIRST_NAME].substring(0,1).toLowerCase()
									 + fields[IDX_LAST_NAME].toLowerCase();
				fields[IDX_PASSWORD] = fields[NUM_ACCOUNT_FIELDS + IDX_SERIAL_NO];
				
				fields[NUM_ACCOUNT_FIELDS + IDX_DEVICE_TYPE] = "Thermostat";
				
				if (fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY].equalsIgnoreCase("Western"))
					fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = "Western Heating";
				else if (fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY].equalsIgnoreCase("Ridgeway"))
					fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = "Ridgeway Industrial";
				else if (fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY].equalsIgnoreCase("Access"))
					fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = "Access Heating";
				
				return fields;
			}
		});
    	
		parsers.put("Portland", new ImportFileParser() {
			/** Portland General Customer Information
			 * COL_NUM:	COL_NAME
			 * 1		ACCOUNT_NO
			 * 2		COUNTY
			 * 3		----
			 * 4		LAST_NAME
			 * 5		FIRST_NAME
			 * 6		HOME_PHONE
			 * 7,8		WORK_PHONE
			 * 9		STREET_ADDR1
			 * 10		CITY
			 * 11		STATE
			 * 12		ZIP_CODE
			 * 13		EMAIL
			 * 14		----
			 * 15		SERVICE_COMPANY
			 * 16		----
			 * 17		INSTALL_DATE
			 * 18		SERIAL_NO(LCR)
			 * 19		ADDR_GROUP
			 * 20		----
			 */
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_COUNTY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE_EXT] = "(ext." + st.sval + ")";
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_ADDR_GROUP] = st.sval;
				if (st.ttype != ',') st.nextToken();
				
				fields[IDX_USERNAME] = fields[IDX_EMAIL];
				fields[IDX_PASSWORD] = fields[NUM_ACCOUNT_FIELDS + IDX_SERIAL_NO];
				
				fields[NUM_ACCOUNT_FIELDS + IDX_DEVICE_TYPE] = "LCR-5000";
				fields[NUM_ACCOUNT_FIELDS + IDX_ADDR_GROUP] = "PGE RIWH Group " + fields[NUM_ACCOUNT_FIELDS + IDX_ADDR_GROUP];
				
				return fields;
			}
		});
    	
		/** San Antonio Customer Information
		 * COL_NUM:	COL_NAME
		 * 1		ACCOUNT_NO
		 * 2		LAST_NAME
		 * 3		FIRST_NAME
		 * 4		HOME_PHONE
		 * 5		WORK_PHONE
		 * 6		EMAIL
		 * 7		STREET_ADDR1
		 * 8		STREET_ADDR2
		 * 9		CITY
		 * 10		STATE
		 * 11		ZIP_CODE
		 * 12		MAP_NO
		 * 13		SERIAL_NO(THERMOSTAT)
		 * 14		INSTALL_DATE
		 * 15		REMOVE_DATE
		 * ----		(Additional required fields)
		 * 16		USERNAME
		 * 17		PASSWORD
		 * 18		HARDWARE_ACTION
		 */
		parsers.put("San Antonio", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_EMAIL] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STREET_ADDR2] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_MAP_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_REMOVE_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_USERNAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_PASSWORD] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[NUM_ACCOUNT_FIELDS + IDX_HARDWARE_ACTION] = st.sval;
				
				fields[NUM_ACCOUNT_FIELDS + IDX_DEVICE_TYPE] = "Thermostat";
				
				return fields;
			}
		});
    	
		/** Old STARS customer table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)	#used accross old STARS tables
		 * 2,3		ACCOUNT_NO
		 * 4		CUSTOMER_TYPE
		 * 5		LAST_NAME
		 * 6		FIRST_NAME
		 * 7		----
		 * 8		COMPANY_NAME
		 * 9		MAP_NO
		 * 10		STREET_ADDR1
		 * 11		STREET_ADDR2
		 * 12		CITY
		 * 13		STATE
		 * 14		ZIP_CODE
		 * 15		HOME_PHONE
		 * 16		WORK_PHONE
		 * 17		----
		 * 18		ACCOUNT_NOTES
		 */
		parsers.put("STARS Customer", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_NO] += "-" + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CUSTOMER_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_LAST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_FIRST_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_COMPANY_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_MAP_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_STREET_ADDR1] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_STREET_ADDR2] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_CITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_STATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ZIP_CODE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_HOME_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WORK_PHONE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_ACCOUNT_NOTES] = st.sval;
				
				return fields;
			}
		});
    	
		/** Old STARS service info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		PROP_NOTES
		 * 3		SUBSTATION
		 * 4		FEEDER
		 * 5		POLE
		 * 6		TRFM_SIZE
		 * 7		SERV_VOLT
		 * 8,9		----
		 * 10-18	PROP_NOTES
		 */
		parsers.put("STARS ServiceInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_PROP_NOTES] += "MeterNumber:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SUBSTATION] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_FEEDER] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_POLE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_TRFM_SIZE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SERV_VOLT] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				for (int i = 10; i <= 18; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_PROP_NOTES] += st.sval + LINE_SEPARATOR;
				}
				
				return fields;
			}
		});
    	
		/** Old STARS inventory table
		 * COL_NUM:	COL_NAME
		 * 1		(INV_ID)
		 * 2		SERIAL_NO
		 * 3		(ACCOUNT_ID)
		 * 4		ALT_TRACK_NO
		 * 5		DEVICE_NAME
		 * 6-8		INV_NOTES		
		 * 9		DEVICE_TYPE
		 * 10-12	----
		 * 13		DEVICE_VOLT
		 * 14		RECEIVE_DATE
		 * 15		----
		 * 16		SERVICE_COMPANY
		 * 17-19	INV_NOTES
		 */
		parsers.put("STARS Inventory", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_INV_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_SERIAL_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_ALT_TRACK_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DEVICE_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_INV_NOTES] += "MapNumber:" + st.sval + LINE_SEPARATOR;
				for (int i = 7; i <= 8; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_INV_NOTES] += st.sval + LINE_SEPARATOR;
				}
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DEVICE_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				for (int i = 10; i <= 12; i++) {
					st.nextToken();
					if (st.ttype != ',') st.nextToken();
				}
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DEVICE_VOLTAGE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_RECEIVE_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SERVICE_COMPANY] = st.sval;
				for (int i = 17; i <= 19; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_INV_NOTES] += st.sval + LINE_SEPARATOR;
				}
				
				return fields;
			}
		});
		
		/** Old STARS receiver table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(INV_ID)
		 * 3		INSTALL_DATE
		 * 4-10		INV_NOTES
		 * 11		DEVICE_STATUS
		 * 12,15,18	R1_GROUP,R2_GROUP,R3_GROUP
		 * 13,16,19	INV_NOTES
		 * 14,17,20	R1_STATUS,R2_STATUS,R3_STATUS
		 */
		parsers.put("STARS Receiver", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_INV_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INSTALL_DATE] = st.sval;
				for (int i = 4; i <= 6; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_INV_NOTES] += st.sval + LINE_SEPARATOR;
				}
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_INV_NOTES] += "Technician:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_INV_NOTES] += "Location:" + st.sval + LINE_SEPARATOR;
				for (int i = 9; i <= 10; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_INV_NOTES] += st.sval + LINE_SEPARATOR;
				}
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DEVICE_STATUS] = st.sval;
				for (int i = 0; i < 3; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
						fields[IDX_R1_GROUP + i] = st.sval.substring( "RX-GROUP:".length() );
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_INV_NOTES] += st.sval + LINE_SEPARATOR;
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
						fields[IDX_R1_STATUS + i] = st.sval.substring( "RX-STATUS:".length() );
				}
				
				return fields;
			}
		});
		
		/** Old STARS meter table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(INV_ID)
		 * 3		DEVICE_NAME
		 * 4		INV_NOTES
		 * 5		INSTALL_DATE
		 * 6		INV_NOTES		
		 */
		parsers.put("STARS Meter", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_INV_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DEVICE_NAME] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_INV_NOTES] += "Technician:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INSTALL_DATE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_INV_NOTES] += st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS load info table
		 * COL_NUM:	COL_NAME
		 * 1		APP_ID
		 * 2		(ACCOUNT_ID)
		 * 3		(INV_ID)
		 * 4		RELAY_NUM
		 * 5		APP_NOTES
		 * 6		APP_TYPE
		 * 7		APP_NOTES
		 * 8		MANUFACTURER
		 * 9-12		APP_NOTES
		 * 13		YEAR_MADE
		 * 14-21	APP_NOTES
		 */
		parsers.put("STARS LoadInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_RELAY_NUM] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_APP_NOTES] += st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_APP_NOTES] += "EquipCode:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_MANUFACTURER] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_APP_NOTES] += "Contractor1:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_APP_NOTES] += "Contractor2:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_APP_NOTES] += "WarrantyInfo:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "AvailForCtrl:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_YEAR_MADE] = st.sval;
				for (int i = 14; i <= 21; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
						fields[IDX_APP_NOTES] += st.sval + LINE_SEPARATOR;
				}
				
				return fields;
			}
		});
		
		/** Old STARS work order table
		 * COL_NUM:	COL_NAME
		 * 1		ORDER_NO
		 * 2		(ACCOUNT_ID)
		 * 3		(INV_ID)
		 * 4-8		----
		 * 9		DATE_REPORTED
		 * 10		DATE_COMPLETED
		 * 11		ORDER_STATUS
		 * 12		ORDER_TYPE
		 * 13		ORDER_DESC
		 * 14		ACTION_TAKEN
		 * 15		TIME_SCHEDULED
		 * 16		DATE_SCHEDULED
		 * 17-19	ORDER_NOTES
		 * 20		SERVICE_COMPANY
		 */
		parsers.put("STARS WorkOrder", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_ORDER_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ORDER_NO] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_INV_ID] = st.sval;
				for (int i = 4; i <=8; i++) {
					if (st.ttype != ',') st.nextToken();
					st.nextToken();
				}
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DATE_REPORTED] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DATE_COMPLETED] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ORDER_STATUS] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_ORDER_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_ORDER_DESC] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_ACTION_TAKEN] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) {
					fields[IDX_TIME_SCHEDULED] = st.sval;
					fields[IDX_ORDER_DESC] += "<br>Time Scheduled: " + fields[IDX_TIME_SCHEDULED];
				} 
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_DATE_SCHEDULED] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_ACTION_TAKEN] += "<br>" + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_ACTION_TAKEN] += "<br>Overtime Hours: " + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"' && st.sval.length() > 0)
					fields[IDX_ACTION_TAKEN] += "<br>Technician: " + st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_ORDER_CONTRACTOR] = st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS residence info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		OWNERSHIP_TYPE
		 * 3		RES_TYPE
		 * 4		CONSTRUCTION_TYPE
		 * 5		DECADE_BUILT
		 * 6		SQUARE_FEET
		 * 7		NUM_OCCUPANTS
		 * 8		GENERAL_COND
		 * 9		INSULATION_DEPTH
		 * 10		MAIN_FUEL_TYPE
		 * 11		RES_NOTES
		 * 12		MAIN_COOLING_SYS
		 * 13,14	RES_NOTES
		 * 15		MAIN_HEATING_SYS
		 * 16-21	RES_NOTES
		 */
		parsers.put("STARS ResInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_RES_FIELDS, st, true);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_OWNERSHIP_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_RES_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_CONSTRUCTION_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DECADE_BUILT] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SQUARE_FEET] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_NUM_OCCUPANTS] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GENERAL_COND] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_INSULATION_DEPTH] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_MAIN_FUEL_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "SetBackThermostat:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_MAIN_COOLING_SYS] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "CoolingSystemYearInstalled:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "CoolingSystemEfficiency:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_MAIN_HEATING_SYS] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "HeatingSystemYearInstalled:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "HeatingSystemEfficiency:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "EnergyAudit:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "HeatLoss:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "HeatGain:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_RES_NOTES] += "EnergyManagementParticipant:" + st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS AC info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4		APP_NOTES
		 * 5		AC_TONNAGE
		 * 6,7		NOTES
		 */
		parsers.put("STARS ACInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_AC_TONNAGE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += "BTU_Hour:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS WH info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4		APP_NOTES
		 * 5		WH_NUM_GALLONS
		 * 6		WH_NUM_ELEMENTS
		 * 7		WH_ENERGY_SRC
		 * 8		WH_LOCATION
		 * 8		APP_NOTES
		 */
		parsers.put("STARS WHInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_WH_NUM_GALLONS] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_WH_NUM_ELEMENTS] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_WH_ENERGY_SRC] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += "Location:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS generator info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_NOTES
		 * 4		GEN_FUEL_CAP
		 * 5		GEN_START_DELAY
		 * 6		GEN_CAPACITY
		 * 7		GEN_TRAN_SWITCH_MFC
		 * 8		GEN_TRAN_SWITCH_TYPE
		 */
		parsers.put("STARS GenInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += "StandbyKW:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GEN_FUEL_CAP] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GEN_START_DELAY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GEN_CAPACITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GEN_TRAN_SWITCH_MFC] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GEN_TRAN_SWITCH_TYPE] = st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS irrigation info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4		APP_NOTES
		 * 5		IRR_TYPE
		 * 6		IRR_ENERGY_SRC
		 * 7		IRR_HORSE_POWER
		 * 8		IRR_METER_VOLT
		 * 9		IRR_METER_LOC
		 * 10		IRR_SOIL_TYPE
		 */
		parsers.put("STARS IrrInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_IRR_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_IRR_ENERGY_SRC] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_IRR_HORSE_POWER] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_IRR_METER_VOLT] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_IRR_METER_LOC] += st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_IRR_SOIL_TYPE] = st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS grain dryer info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4		APP_NOTES
		 * 5		GDRY_TYPE
		 * 6		GDRY_ENERGY_SRC
		 * 7		GDRY_HORSE_POWER
		 * 8		GDRY_HEAT_SRC
		 * 9		GDRY_BIN_SIZE
		 */
		parsers.put("STARS GDryInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GDRY_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GDRY_ENERGY_SRC] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GDRY_HORSE_POWER] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GDRY_HEAT_SRC] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_GDRY_BIN_SIZE] = st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS heat pump info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4		APP_NOTES
		 * 5		HP_TYPE
		 * 6		HP_SIZE
		 * 7		HP_STANDBY_SRC
		 * 8		HP_RESTART_DELAY
		 * 9		APP_NOTES
		 */
		parsers.put("STARS HPInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_HP_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_HP_SIZE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_HP_STANDBY_SRC] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_HP_RESTART_DELAY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS storage heat info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4		APP_NOTES
		 * 5		SH_TYPE
		 * 6		SH_CAPACITY
		 * 7		SH_RECHARGE_TIME
		 * 8,9		APP_NOTES
		 */
		parsers.put("STARS SHInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SH_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SH_CAPACITY] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_SH_RECHARGE_TIME] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "ContractHours:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS dual fuel info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4,5		APP_NOTES
		 * 6		DF_2ND_ENERGY_SRC
		 * 7		DF_2ND_CAPACITY
		 * 8		DF_SWITCH_OVER_TYPE
		 * 9		APP_NOTES
		 */
		parsers.put("STARS DFInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += "PrimarySize:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DF_2ND_ENERGY_SRC] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DF_2ND_CAPACITY] = st.sval;
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_DF_SWITCH_OVER_TYPE] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += st.sval;
				
				return fields;
			}
		});
		
		/** Old STARS general info table
		 * COL_NUM:	COL_NAME
		 * 1		(ACCOUNT_ID)
		 * 2		(APP_ID)
		 * 3		APP_KW
		 * 4,5		APP_NOTES
		 */
		parsers.put("STARS GenlInfo", new ImportFileParser() {
			public String[] populateFields(String line) throws Exception {
				StreamTokenizer st = prepareStreamTokenzier( line );
				String[] fields = prepareFields(NUM_APP_FIELDS, st, false);
				
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_ACCOUNT_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_ID] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD) fields[IDX_APP_KW] = st.sval;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD)
					fields[IDX_APP_NOTES] += "Rebate:" + st.sval + LINE_SEPARATOR;
				if (st.ttype != ',') st.nextToken();
				st.nextToken();
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"')
					fields[IDX_APP_NOTES] += st.sval;
				
				return fields;
			}
		});
	}
    
	private String referer = null;
	private String redirect = null;
	

	private static void setStarsCustAccount(StarsCustAccount account, String[] fields, LiteStarsEnergyCompany energyCompany) {
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
		if (fields[IDX_SUBSTATION].length() > 0) {
			// If importing old STARS data, this field is substation id
			starsSub.setEntryID( Integer.parseInt(fields[IDX_SUBSTATION]) );
		}
		else {
			YukonSelectionList subList = energyCompany.getYukonSelectionList( com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION );
			if (subList.getYukonListEntries().size() == 0)
				starsSub.setEntryID( CtiUtilities.NONE_ID );
			else
				starsSub.setEntryID( ((YukonListEntry)subList.getYukonListEntries().get(0)).getEntryID() );
		}
		
	    StarsSiteInformation siteInfo = new StarsSiteInformation();
	    siteInfo.setSubstation( starsSub );
	    siteInfo.setFeeder( fields[IDX_FEEDER] );
	    siteInfo.setPole( fields[IDX_POLE] );
	    siteInfo.setTransformerSize( fields[IDX_TRFM_SIZE] );
	    siteInfo.setServiceVoltage( fields[IDX_SERV_VOLT] );
	    account.setStarsSiteInformation( siteInfo );
	    
	    BillingAddress billAddr = new BillingAddress();
	    billAddr.setStreetAddr1( fields[IDX_STREET_ADDR1] );
	    billAddr.setStreetAddr2( fields[IDX_STREET_ADDR2] );
	    billAddr.setCity( fields[IDX_CITY] );
	    billAddr.setState( fields[IDX_STATE] );
	    billAddr.setZip( fields[IDX_ZIP_CODE] );
	    billAddr.setCounty( fields[IDX_COUNTY] );
	    account.setBillingAddress( billAddr );
	    
	    PrimaryContact primContact = new PrimaryContact();
	    primContact.setLastName( fields[IDX_LAST_NAME] );
	    primContact.setFirstName( fields[IDX_FIRST_NAME] );
	    primContact.setHomePhone( ServletUtils.formatPhoneNumber(fields[IDX_HOME_PHONE]) );
	    primContact.setWorkPhone( ServletUtils.formatPhoneNumber(fields[IDX_WORK_PHONE]) + fields[IDX_WORK_PHONE_EXT] );
	    
	    Email email = new Email();
	    email.setNotification( fields[IDX_EMAIL] );
	    email.setEnabled( false );
	    primContact.setEmail( email );
	    account.setPrimaryContact( primContact );
	}

	public static LiteStarsCustAccountInformation newCustomerAccount(String[] fields, StarsYukonUser user, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		// Build the request message
		StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
		
		StarsCustomerAccount account = new StarsCustomerAccount();
		ImportManager.setStarsCustAccount( account, fields, energyCompany );
		newAccount.setStarsCustomerAccount( account );
		
		if (fields[IDX_USERNAME].trim().length() > 0) {
			StarsUpdateLogin login = new StarsUpdateLogin();
			login.setUsername( fields[IDX_USERNAME] );
			login.setPassword( fields[IDX_PASSWORD] );
			newAccount.setStarsUpdateLogin( login );
		}
		
		return NewCustAccountAction.newCustomerAccount( newAccount, user, energyCompany );
	}

	public static void updateCustomerAccount(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
		throws Exception
	{
	    StarsUpdateCustomerAccount updateAccount = new StarsUpdateCustomerAccount();
	    ImportManager.setStarsCustAccount( updateAccount, fields, energyCompany );
	    
	    UpdateCustAccountAction.updateCustomerAccount( updateAccount, liteAcctInfo, energyCompany, conn );
	}

	public static void updateLogin(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		LiteContact liteContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
		if (liteContact.getLoginID() == UserUtils.USER_YUKON_ID
			&& fields[IDX_USERNAME].trim().length() == 0)
			return;
			
	    StarsUpdateLogin updateLogin = new StarsUpdateLogin();
	    updateLogin.setUsername( fields[IDX_USERNAME] );
	    updateLogin.setPassword( fields[IDX_PASSWORD] );
	    
	    StarsOperation operation = new StarsOperation();
	    operation.setStarsUpdateLogin( updateLogin );
	    SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage( operation );
	    
	    UpdateLoginAction.updateLogin( updateLogin, liteAcctInfo, energyCompany );
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

	private static void setStarsInventory(StarsInv inv, String[] fields, LiteStarsEnergyCompany energyCompany) {
		if (fields[IDX_ALT_TRACK_NO].length() > 0)
			inv.setAltTrackingNumber( fields[IDX_ALT_TRACK_NO] );
		if (fields[IDX_INV_NOTES].length() > 0)
			inv.setNotes( fields[IDX_INV_NOTES] );
		
		ServerUtils.starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
		
		if (fields[IDX_RECEIVE_DATE].length() > 0) {
			try {
				inv.setReceiveDate( ServerUtils.starsDateFormat.parse(fields[IDX_RECEIVE_DATE]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_INSTALL_DATE].length() > 0) {
			inv.setInstallDate( ServletUtil.parseDateStringLiberally(
					fields[IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone()) );
			
			if (inv.getInstallDate() == null) {
				try {
					inv.setInstallDate( ServerUtils.starsDateFormat.parse(fields[IDX_INSTALL_DATE]) );
				}
				catch (java.text.ParseException e) {}
			}
		}
		
		if (fields[IDX_REMOVE_DATE].length() > 0) {
			inv.setRemoveDate( ServletUtil.parseDateStringLiberally(
					fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
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
			try {
				// If importing old STARS data, this field is the service company id
				int companyID = Integer.parseInt( fields[IDX_SERVICE_COMPANY] );
				InstallationCompany company = new InstallationCompany();
				company.setEntryID( companyID );
				inv.setInstallationCompany( company );
			}
			catch (NumberFormatException e) {
				// Otherwise, this field is the service company name
				ArrayList companies = energyCompany.getAllServiceCompanies();
				
				for (int i = 0; i < companies.size(); i++) {
					LiteServiceCompany entry = (LiteServiceCompany) companies.get(i);
					
					if (entry.getCompanyName().equalsIgnoreCase( fields[IDX_SERVICE_COMPANY] )) {
						InstallationCompany company = new InstallationCompany();
						company.setEntryID( entry.getCompanyID() );
						inv.setInstallationCompany( company );
						break;
					}
				}
			}
		}
		
		if (fields[IDX_DEVICE_TYPE].length() > 0) {
			DeviceType devType = new DeviceType();
			devType.setEntryID( Integer.parseInt(fields[IDX_DEVICE_TYPE]) );
			inv.setDeviceType( devType );
		}
		
		if (fields[IDX_DEVICE_NAME].equals("")) {
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
		LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws Exception
	{
		// Check inventory and build request message
		int devTypeID = ImportManager.getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new WebClientException("Invalid device type \"" + fields[IDX_DEVICE_TYPE] + "\"");
		
		StarsCreateLMHardware createHw = null;
		LiteInventoryBase liteInv = null;
		
		int categoryID = ECUtils.getInventoryCategoryID( devTypeID, energyCompany );
		if (ECUtils.isLMHardware( categoryID ))
			liteInv = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
		else if (fields[IDX_DEVICE_NAME].length() > 0)
			liteInv = energyCompany.searchForDevice( categoryID, fields[IDX_DEVICE_NAME] );
		
		if (liteInv != null) {
			if (liteInv.getAccountID() == liteAcctInfo.getAccountID())
				throw new WebClientException("Cannot insert duplicate hardware into the customer account");
			
			createHw = new StarsCreateLMHardware();
			StarsLiteFactory.setStarsInv( createHw, liteInv, energyCompany );
			createHw.setInstallDate( new java.util.Date() );
			createHw.setRemoveDate( new java.util.Date(0) );
			createHw.setInstallationNotes( "" );
		}
		else {
			createHw = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
		}
		
		fields[IDX_DEVICE_TYPE] = String.valueOf( devTypeID );
		ImportManager.setStarsInventory( createHw, fields, energyCompany );
	    
	    return CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany, conn );
	}

	public static void updateLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws Exception
	{
		if (liteAcctInfo.getInventories().size() != 1)
			throw new WebClientException( "More than one hardware in the account, cannot determine which one to be updated" );
		
		// Check inventory and build request message
		int devTypeID = ImportManager.getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new WebClientException("Invalid device type '" + fields[IDX_DEVICE_TYPE] + "'");
		
		StarsUpdateLMHardware updateHw = null;
		LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
		
		if (liteHw != null) {
			updateHw = new StarsUpdateLMHardware();
			StarsLiteFactory.setStarsInv( updateHw, liteHw, energyCompany );
			updateHw.setInstallDate( new java.util.Date() );
			updateHw.setRemoveDate( new java.util.Date(0) );
			updateHw.setInstallationNotes( "" );
		}
		else {
			updateHw = (StarsUpdateLMHardware) StarsFactory.newStarsInv(StarsUpdateLMHardware.class);
		}
		
		fields[IDX_DEVICE_TYPE] = String.valueOf( devTypeID );
		ImportManager.setStarsInventory( updateHw, fields, energyCompany );
		
		int invIDOld = ((Integer) liteAcctInfo.getInventories().get(0)).intValue();
		
		if (liteHw == null || liteHw.getInventoryID() != invIDOld) {
			// Update information doesn't match the old hardware, remove the old one
			StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
			deleteHw.setInventoryID( invIDOld );
			deleteHw.setDeleteFromInventory( false );
			if (fields[IDX_REMOVE_DATE].length() > 0) {
				deleteHw.setRemoveDate( ServletUtil.parseDateStringLiberally(
						fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
			}
			
			// Build up request message for adding new hardware and preserving old hardware configuration
			StarsCreateLMHardware createHw = (StarsCreateLMHardware)
					StarsFactory.newStarsInv( updateHw, StarsCreateLMHardware.class );
	        
			if (createHw.getLMHardware() != null) {
				for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
					if (liteApp.getInventoryID() == deleteHw.getInventoryID()) {
						StarsLMHardwareConfig starsConfig = new StarsLMHardwareConfig();
						starsConfig.setApplianceID( liteApp.getApplianceID() );
						starsConfig.setGroupID( liteApp.getAddressingGroupID() );
						createHw.getLMHardware().addStarsLMHardwareConfig( starsConfig );
					}
				}
			}
	        
			DeleteLMHardwareAction.removeInventory(deleteHw, liteAcctInfo, energyCompany, conn);
			CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany, conn );
		}
		else {
			UpdateLMHardwareAction.updateInventory(updateHw, liteHw, energyCompany, conn);
		}
	}

	public static void removeLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws Exception
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
		}
		
		DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany, conn );
	}

	public static void programSignUp(ArrayList programs, LiteStarsCustAccountInformation liteAcctInfo,
		Integer invID, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
		throws Exception
	{
		// Build request message
		StarsSULMPrograms suPrograms = new StarsSULMPrograms();
		for (int i = 0; i < programs.size(); i++) {
			int[] prog = (int[]) programs.get(i);
			
			SULMProgram suProg = new SULMProgram();
			suProg.setProgramID( prog[0] );
			suProg.setApplianceCategoryID( prog[1] );
			suProg.setAddressingGroupID( prog[2] );
			suPrograms.addSULMProgram( suProg );
		}
		
		StarsProgramSignUp progSignUp = new StarsProgramSignUp();
		progSignUp.setStarsSULMPrograms( suPrograms );
	    
	    ProgramSignUpAction.updateProgramEnrollment( progSignUp, liteAcctInfo, invID, energyCompany, conn );
	}

	private static void setStarsAppliance(StarsApp app, String[] fields, LiteStarsEnergyCompany energyCompany) {
		app.setApplianceCategoryID( Integer.parseInt(fields[IDX_APP_TYPE]) );
		app.setYearManufactured( fields[IDX_YEAR_MADE] );
		app.setNotes( fields[IDX_APP_NOTES] );
		app.setModelNumber( "" );
		app.setEfficiencyRating( -1 );
		
		if (fields[IDX_APP_KW].length() > 0) {
			int kwCap = (int) Double.parseDouble(fields[IDX_APP_KW]);
			if (kwCap >= 0) app.setKWCapacity( kwCap );
		}
		
		Location location = new Location();
		location.setEntryID( CtiUtilities.NONE_ID );
		app.setLocation( location );
		
		Manufacturer mfc = new Manufacturer();
		mfc.setEntryID( Integer.parseInt(fields[IDX_MANUFACTURER]) );
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
			
			int numElmt = Integer.parseInt(fields[IDX_WH_NUM_ELEMENTS]);
			if (numElmt >= 0) wh.setNumberOfElements( numElmt );
			
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
			
			int peakKW = Integer.parseInt(fields[IDX_GEN_CAPACITY]);
			if (peakKW >= 0) gen.setPeakKWCapacity( peakKW );
			
			int fuelCap = Integer.parseInt(fields[IDX_GEN_FUEL_CAP]);
			if (fuelCap >= 0) gen.setFuelCapGallons( fuelCap );
			
			int startDelay = Integer.parseInt(fields[IDX_GEN_START_DELAY]);
			if (startDelay >= 0) gen.setStartDelaySeconds( startDelay );
			
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
			
			int restartDelay = Integer.parseInt(fields[IDX_HP_RESTART_DELAY]);
			if (restartDelay >= 0) hp.setRestartDelaySeconds( restartDelay );
			
			app.setHeatPump( hp );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
			StorageHeat sh = new StorageHeat();
			
			StorageType type = new StorageType();
			type.setEntryID( Integer.parseInt(fields[IDX_SH_TYPE]) );
			sh.setStorageType( type );
			
			int peakKW = Integer.parseInt(fields[IDX_SH_CAPACITY]);
			if (peakKW >= 0) sh.setPeakKWCapacity( peakKW );
			
			int rechargeTime = Integer.parseInt(fields[IDX_SH_RECHARGE_TIME]);
			if (rechargeTime >= 0) sh.setHoursToRecharge( rechargeTime );
			
			app.setStorageHeat( sh );
		}
		else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
			DualFuel df = new DualFuel();
			
			SwitchOverType soType = new SwitchOverType();
			soType.setEntryID( Integer.parseInt(fields[IDX_DF_SWITCH_OVER_TYPE]) );
			df.setSwitchOverType( soType );
			
			int kwCap2 = Integer.parseInt(fields[IDX_DF_2ND_CAPACITY]);
			if (kwCap2 >= 0) df.setSecondaryKWCapacity( kwCap2 );
			
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
	}

	public static void updateAppliance(String[] fields, int appID, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws Exception
	{
		StarsUpdateAppliance updateApp = new StarsUpdateAppliance();
		updateApp.setApplianceID( appID );
		setStarsAppliance( updateApp, fields, energyCompany );
		
		UpdateApplianceAction.updateAppliance( updateApp, liteAcctInfo );
	}

	private static String formatTimeString(String str) {
		StringBuffer strBuf = new StringBuffer( str.toUpperCase() );
		strBuf.reverse();
		
		while (true) {
			if (Character.isDigit(strBuf.charAt(0)) ||
				strBuf.charAt(0) == 'A' ||
				strBuf.charAt(0) == 'P' ||
				strBuf.charAt(0) == 'M')
				break;
			strBuf.deleteCharAt(0);
		}
		
		if (strBuf.charAt(0) == 'A' || strBuf.charAt(0) == 'P')
			strBuf.insert(0, 'M');
		
		return strBuf.reverse().toString();
	}

	public static void newServiceRequest(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws Exception
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
		
		ServerUtils.starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
		
		if (fields[IDX_DATE_REPORTED].length() > 0) {
			try {
				createOrder.setDateReported( ServerUtils.starsDateFormat.parse(fields[IDX_DATE_REPORTED]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_DATE_COMPLETED].length() > 0) {
			try {
				createOrder.setDateCompleted( ServerUtils.starsDateFormat.parse(fields[IDX_DATE_COMPLETED]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_DATE_SCHEDULED].length() > 0) {
			try {
				Date datePart = ServerUtils.starsDateFormat.parse( fields[IDX_DATE_SCHEDULED] );
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
	    
		CreateServiceRequestAction.createServiceRequest( createOrder, liteAcctInfo, energyCompany );
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
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
			return;
		}
    	
		StarsYukonUser user = (StarsYukonUser)
				session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		
		boolean isMultiPart = false;
		List items = null;
		
		String action = req.getParameter( "action" );
		if (action == null) {
			try {
				DiskFileUpload upload = new DiskFileUpload();
				items = upload.parseRequest( req );
				action = ServerUtils.getFormField( items, "action" );
				isMultiPart = true;
			}
			catch (FileUploadException e) {
				e.printStackTrace();
			}
		}
		if (action == null) action = "";
    	
		referer = req.getHeader( "referer" );
		redirect = (isMultiPart)? ServerUtils.getFormField(items, ServletUtils.ATT_REDIRECT)
				: req.getParameter(ServletUtils.ATT_REDIRECT);
		if (redirect == null) redirect = referer;
		
		if (action.equalsIgnoreCase("PreprocessImportData"))
			preprocessImportData( user, items, session );
		else if (action.equalsIgnoreCase("ImportCustAccounts"))
			importCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase("PreprocessStarsData"))
			preprocessStarsData( user, req, session );
		else if (action.equalsIgnoreCase("AssignSelectionList"))
			assignSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("ImportStarsData"))
			importStarsData( user, req, session );
        
		resp.sendRedirect( redirect );
	}
	
	private void preprocessImportData(StarsYukonUser user, List items, HttpSession session) {
		try {
			String importID = ServerUtils.forceNotNone(
					AuthFuncs.getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT) );
			
			ImportFileParser parser = (ImportFileParser) parsers.get(importID);
			if (parser == null)
				throw new WebClientException("Invalid import ID: " + importID);
			
			File uploadFile = ServerUtils.getUploadFile( items, "ImportFile" );
			if (uploadFile == null)
				throw new WebClientException("Failed to upload the import file");
			
			ArrayList lines = ServerUtils.readFile( uploadFile, true );
			if (lines == null)
				throw new WebClientException("Failed to read file '" + uploadFile.getPath() + "'");
			
			ArrayList fieldsList = new ArrayList();
			String line = null;
			
			try {
				for (int i = 0; i < lines.size(); i++) {
					line = (String) lines.get(i);
					String[] fields = parser.populateFields( line );
					fieldsList.add( fields );
				}
			}
			catch (Exception e) {
				throw new WebClientException("Failed to parse the line '" + line + "'");
			}
			
			session.setAttribute(PREPROCESSED_DATA, fieldsList);
		}
		catch (WebClientException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
	}
	
	private void importCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		ArrayList fieldsList = (ArrayList) session.getAttribute(PREPROCESSED_DATA);
		
		// Process request parameters for program enrollment
		ArrayList programs = new ArrayList();
		String[] catIDs = req.getParameterValues( "CatID" );
		String[] progIDs = req.getParameterValues( "ProgID" );
		
		if (progIDs != null) {
			for (int i = 0; i < progIDs.length; i++) {
				if (progIDs[i].length() == 0) continue;
				
				int[] prog = new int[3];
				prog[0] = Integer.parseInt(progIDs[i]);
				prog[1] = Integer.parseInt(catIDs[i]);
				prog[2] = 0;
				programs.add( prog );
			}
		}
		
		ImportCustAccountsTask task = new ImportCustAccountsTask( user, fieldsList, programs );
		long id = ProgressChecker.addTask( task );
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}
	
	private void preprocessStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			File custFile = new File( req.getParameter("CustFile") );
			File servInfoFile = new File( req.getParameter("ServInfoFile") );
			File invFile = new File( req.getParameter("InvFile") );
			File recvrFile = new File( req.getParameter("RecvrFile") );
			File meterFile = new File( req.getParameter("MeterFile") );
			File loadInfoFile = new File( req.getParameter("LoadInfoFile") );
			File acInfoFile = new File( req.getParameter("ACInfoFile") );
			File whInfoFile = new File( req.getParameter("WHInfoFile") );
			File genInfoFile = new File( req.getParameter("GenInfoFile") );
			File irrInfoFile = new File( req.getParameter("IrrInfoFile") );
			File gdryInfoFile = new File( req.getParameter("GDryInfoFile") );
			File hpInfoFile = new File( req.getParameter("HPInfoFile") );
			File shInfoFile = new File( req.getParameter("SHInfoFile") );
			File dfInfoFile = new File( req.getParameter("DFInfoFile") );
			File genlInfoFile = new File( req.getParameter("GenlInfoFile") );
			File workOrderFile = new File( req.getParameter("WorkOrderFile") );
			File resInfoFile = new File( req.getParameter("ResInfoFile") );
			
			ArrayList custLines = ServerUtils.readFile( custFile, true );
			ArrayList servInfoLines = ServerUtils.readFile( servInfoFile );
			ArrayList invLines = ServerUtils.readFile( invFile, true );
			ArrayList recvrLines = ServerUtils.readFile( recvrFile );
			ArrayList meterLines = ServerUtils.readFile( meterFile );
			ArrayList loadInfoLines = ServerUtils.readFile( loadInfoFile, true );
			ArrayList acInfoLines = ServerUtils.readFile( acInfoFile );
			ArrayList whInfoLines = ServerUtils.readFile( whInfoFile );
			ArrayList genInfoLines = ServerUtils.readFile( genInfoFile );
			ArrayList irrInfoLines = ServerUtils.readFile( irrInfoFile );
			ArrayList gdryInfoLines = ServerUtils.readFile( gdryInfoFile );
			ArrayList hpInfoLines = ServerUtils.readFile( hpInfoFile );
			ArrayList shInfoLines = ServerUtils.readFile( shInfoFile );
			ArrayList dfInfoLines = ServerUtils.readFile( dfInfoFile );
			ArrayList genlInfoLines = ServerUtils.readFile( genlInfoFile );
			ArrayList workOrderLines = ServerUtils.readFile( workOrderFile, true );
			ArrayList resInfoLines = ServerUtils.readFile( resInfoFile, true );
			
			Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_DATA);
			if (preprocessedData == null)
				preprocessedData = new Hashtable();
			
			Hashtable acctIDFields = new Hashtable();	// Map from account id(Integer) to fields(String[])
			Hashtable invIDFields = new Hashtable();	// Map from inventory id(Integer) to fields(String[])
			Hashtable appIDFields = new Hashtable();	// Map from appliance id(Integer) to fields(String[])
			
			ArrayList acctFieldsList = new ArrayList();		// List of all account fields(String[])
			ArrayList invFieldsList = new ArrayList();		// List of all inventory fields(String[])
			ArrayList appFieldsList = new ArrayList();		// List of all appliance fields(String[])
			ArrayList orderFieldsList = new ArrayList();	// List of all work order fields(String[])
			ArrayList resFieldsList = new ArrayList();		// List of all residence fields(String[])
			
			// Sorted maps of import value(String) to id(Integer), filled in assignSelectionList()
			TreeMap[] valueIDMaps = new TreeMap[ LIST_NAMES.length ];
			int[] valueIDCnt = new int[ LIST_NAMES.length ];
			
			for (int i = 0; i < LIST_NAMES.length; i++) {
				valueIDMaps[i] = (TreeMap) preprocessedData.get( LIST_NAMES[i][0] );
				if (valueIDMaps[i] == null) valueIDMaps[i] = new TreeMap();
				valueIDCnt[i] = valueIDMaps[i].size();
			}
			
			Integer zero = new Integer(0);
			
			if (custLines != null) {
				if (custFile.getName().equals("customer.map")) {
					// customer.map file format: import_account_id,db_account_id
					// Notice: the parsed lines have line# inserted at the beginning
					Hashtable acctIDMap = new Hashtable();
					
					for (int i = 0; i < custLines.size(); i++) {
						String[] fields = ((String) custLines.get(i)).split(",");
						if (fields.length != 3)
							throw new WebClientException("Invalid format of file '" + custFile.getPath() + "'");
						acctIDMap.put( Integer.valueOf(fields[1]), Integer.valueOf(fields[2]) );
					}
					
					preprocessedData.put("CustomerAccountMap", acctIDMap);
				}
				else {
					ImportFileParser parser = (ImportFileParser) parsers.get("STARS Customer");
					
					for (int i = 0; i < custLines.size(); i++) {
						String[] fields = parser.populateFields( (String)custLines.get(i) );
						acctFieldsList.add( fields );
						acctIDFields.put( fields[IDX_ACCOUNT_ID], fields );
					}
				}
				
				preprocessedData.put("CustomerFilePath", custFile.getParent());
			}
			else {
				if (preprocessedData.get("CustomerAccountMap") == null)
					throw new WebClientException("No customer information found. If you have already imported the customer file, enter the path of the generated 'customer.map' file in the 'Customer File' field");
			}
			
			if (servInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS ServiceInfo");
				
				for (int i = 0; i < servInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)servInfoLines.get(i) );
					String[] custFields = (String[]) acctIDFields.get( fields[IDX_ACCOUNT_ID] );
					
					if (custFields != null) {
						for (int j = 0; j < custFields.length; j++)
							if (fields[j].length() > 0) custFields[j] = fields[j];
						
						for (int j = 0; j < SERVINFO_LIST_FIELDS.length; j++) {
							int listIdx = SERVINFO_LIST_FIELDS[j][0];
							int fieldIdx = SERVINFO_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (invLines != null) {
				if (invFile.getName().equals("hwconfig.map")) {
					// hwconfig.map file format: import_inv_id,relay1_db_app_id,relay2_db_app_id,relay3_db_app_id
					// Notice: the parsed lines have line# inserted at the beginning
					Hashtable appIDMap = new Hashtable();
					
					for (int i = 0; i < invLines.size(); i++) {
						String[] fields = ((String) invLines.get(i)).split(",");
						if (fields.length != 5)
							throw new WebClientException("Invalid format of file '" + invFile.getPath() + "'");
						
						Integer invID = Integer.valueOf( fields[1] );
						int[] appIDs = new int[3];
						for (int j = 0; j < 3; j++)
							appIDs[j] = Integer.parseInt( fields[j+2] );
						
						appIDMap.put( invID, appIDs );
					}
					
					preprocessedData.put("HwConfigAppMap", appIDMap);
				}
				else {
					ImportFileParser parser = (ImportFileParser) parsers.get("STARS Inventory");
					
					for (int i = 0; i < invLines.size(); i++) {
						String[] fields = parser.populateFields( (String)invLines.get(i) );
						if (fields[IDX_DEVICE_TYPE].equals("") || fields[IDX_DEVICE_TYPE].equals("-1"))
							continue;
						
						invFieldsList.add( fields );
						invIDFields.put( fields[IDX_INV_ID], fields );
						
						for (int j = 0; j < INV_LIST_FIELDS.length; j++) {
							int listIdx = INV_LIST_FIELDS[j][0];
							int fieldIdx = INV_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (recvrLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS Receiver");
				
				for (int i = 0; i < recvrLines.size(); i++) {
					String[] fields = parser.populateFields( (String)recvrLines.get(i) );
					String[] invFields = (String[]) invIDFields.get( fields[IDX_INV_ID] );
					
					if (invFields != null) {
						for (int j = 0; j < invFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_INV_NOTES && invFields[j].length() > 0)
									invFields[j] += fields[j];
								else
									invFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < RECV_LIST_FIELDS.length; j++) {
							int listIdx = RECV_LIST_FIELDS[j][0];
							int fieldIdx = RECV_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (meterLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS Meter");
				
				for (int i = 0; i < meterLines.size(); i++) {
					String[] fields = parser.populateFields( (String)meterLines.get(i) );
					String[] invFields = (String[]) invIDFields.get( fields[IDX_INV_ID] );
					
					if (invFields != null) {
						for (int j = 0; j < invFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_INV_NOTES && invFields[j].length() > 0)
									invFields[j] += fields[j];
								else
									invFields[j] = fields[j];
							}
						}
					}
				}
			}
			
			if (loadInfoLines != null) {
				if (invLines == null && preprocessedData.get("HwConfigAppMap") == null)
					throw new WebClientException("No hardware config information found. If you have already imported the inventory file, enter the path of the generated 'hwconfig.map' file in the 'Inventory File' field");
				
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS LoadInfo");
				
				for (int i = 0; i < loadInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)loadInfoLines.get(i) );
					appFieldsList.add( fields );
					appIDFields.put( fields[IDX_APP_ID], fields );
					
					for (int j = 0; j < APP_LIST_FIELDS.length; j++) {
						int listIdx = APP_LIST_FIELDS[j][0];
						int fieldIdx = APP_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
							valueIDMaps[listIdx].put( fields[fieldIdx], zero );
					}
				}
			}
			
			if (acInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS ACInfo");
				
				for (int i = 0; i < acInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)acInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < AC_LIST_FIELDS.length; j++) {
							int listIdx = AC_LIST_FIELDS[j][0];
							int fieldIdx = AC_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (whInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS WHInfo");
				
				for (int i = 0; i < whInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)whInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < WH_LIST_FIELDS.length; j++) {
							int listIdx = WH_LIST_FIELDS[j][0];
							int fieldIdx = WH_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (genInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS GenInfo");
				
				for (int i = 0; i < genInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)genInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < GEN_LIST_FIELDS.length; j++) {
							int listIdx = GEN_LIST_FIELDS[j][0];
							int fieldIdx = GEN_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (irrInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS IrrInfo");
				
				for (int i = 0; i < irrInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)irrInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < IRR_LIST_FIELDS.length; j++) {
							int listIdx = IRR_LIST_FIELDS[j][0];
							int fieldIdx = IRR_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (gdryInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS GDryInfo");
				
				for (int i = 0; i < gdryInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)gdryInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < GDRY_LIST_FIELDS.length; j++) {
							int listIdx = GDRY_LIST_FIELDS[j][0];
							int fieldIdx = GDRY_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (hpInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS HPInfo");
				
				for (int i = 0; i < hpInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)hpInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < HP_LIST_FIELDS.length; j++) {
							int listIdx = HP_LIST_FIELDS[j][0];
							int fieldIdx = HP_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (shInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS SHInfo");
				
				for (int i = 0; i < shInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)shInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < SH_LIST_FIELDS.length; j++) {
							int listIdx = SH_LIST_FIELDS[j][0];
							int fieldIdx = SH_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (dfInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS DFInfo");
				
				for (int i = 0; i < dfInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)dfInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
						
						for (int j = 0; j < DF_LIST_FIELDS.length; j++) {
							int listIdx = DF_LIST_FIELDS[j][0];
							int fieldIdx = DF_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
								valueIDMaps[listIdx].put( fields[fieldIdx], zero );
						}
					}
				}
			}
			
			if (genlInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS GenlInfo");
				
				for (int i = 0; i < genlInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)genlInfoLines.get(i) );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						appFields[IDX_APP_CAT_DEF_ID] = String.valueOf(YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT);
						for (int j = 0; j < appFields.length; j++) {
							if (fields[j].length() > 0) {
								if (j == IDX_APP_NOTES && appFields[j].length() > 0)
									appFields[j] += fields[j];
								else
									appFields[j] = fields[j];
							}
						}
					}
				}
			}
			
			if (workOrderLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS WorkOrder");
				
				for (int i = 0; i < workOrderLines.size(); i++) {
					String[] fields = parser.populateFields( (String)workOrderLines.get(i) );
					orderFieldsList.add( fields );
					
					for (int j = 0; j < ORDER_LIST_FIELDS.length; j++) {
						int listIdx = ORDER_LIST_FIELDS[j][0];
						int fieldIdx = ORDER_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
							valueIDMaps[listIdx].put( fields[fieldIdx], zero );
					}
				}
			}
			
			if (resInfoLines != null) {
				ImportFileParser parser = (ImportFileParser) parsers.get("STARS ResInfo");
				
				for (int i = 0; i < resInfoLines.size(); i++) {
					String[] fields = parser.populateFields( (String)resInfoLines.get(i) );
					resFieldsList.add( fields );
					
					for (int j = 0; j < RES_LIST_FIELDS.length; j++) {
						int listIdx = RES_LIST_FIELDS[j][0];
						int fieldIdx = RES_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] ))
							valueIDMaps[listIdx].put( fields[fieldIdx], zero );
					}
				}
			}
			
			preprocessedData.put( "CustomerAccount", acctFieldsList );
			preprocessedData.put( "Inventory", invFieldsList );
			preprocessedData.put( "Appliance", appFieldsList );
			preprocessedData.put( "WorkOrder", orderFieldsList );
			preprocessedData.put( "CustomerResidence", resFieldsList );
			for (int i = 0; i < LIST_NAMES.length; i++)
				preprocessedData.put( LIST_NAMES[i][0], valueIDMaps[i] );
			session.setAttribute(PREPROCESSED_DATA, preprocessedData);
			
			Hashtable unassignedLists = (Hashtable) session.getAttribute(UNASSIGNED_LISTS);
			if (unassignedLists == null) {
				unassignedLists = new Hashtable();
				session.setAttribute(UNASSIGNED_LISTS, unassignedLists);
			}
			
			Boolean bTrue = new Boolean(true);
			for (int i = 0; i < LIST_NAMES.length; i++) {
				if (valueIDMaps[i].size() > valueIDCnt[i])
					unassignedLists.put( LIST_NAMES[i][0], bTrue );
			}
		}
		catch (WebClientException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to preprocess old STARS data");
			redirect = referer;
		}
	}
	
	private void assignSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		StarsGetEnergyCompanySettingsResponse ecSettings =
				(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
		
		String listName = req.getParameter("ListName");
		String newList = req.getParameter("NewList");
		String[] values = req.getParameterValues("ImportValue");
		String[] enabled = req.getParameterValues("Enabled");
		String[] entryIDs = req.getParameterValues("EntryID");
		String[] entryTexts = req.getParameterValues("EntryText");
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_DATA);
		TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
		
		for (int i = 0; i < values.length; i++)
			valueIDMap.put( values[i], null );
		
		ArrayList assignedValues = new ArrayList();
		if (enabled != null) {
			for (int i = 0; i < enabled.length; i++) {
				int idx = Integer.parseInt( enabled[i] );
				assignedValues.add( values[idx] );
			}
		}
		
		if (entryIDs != null) {
			for (int i = 0; i < assignedValues.size(); i++)
				valueIDMap.put( assignedValues.get(i), Integer.valueOf(entryIDs[i]) );
		}
		else if (Boolean.valueOf(newList).booleanValue()) {
			java.sql.Connection conn = null;
			try {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				if (listName.equals("ServiceCompany")) {
					// First delete all existing service companies
					StarsAdmin.deleteAllServiceCompanies( energyCompany, conn );
					
					if (entryTexts != null) {
						for (int i = 0; i < entryTexts.length; i++) {
							com.cannontech.database.data.stars.report.ServiceCompany company =
									new com.cannontech.database.data.stars.report.ServiceCompany();
							com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
							
							companyDB.setCompanyName( entryTexts[i] );
							company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
							company.setDbConnection( conn );
							company.add();
							
							com.cannontech.database.data.customer.Contact contact =
									new com.cannontech.database.data.customer.Contact();
							contact.setCustomerContact( company.getPrimaryContact() );
							LiteContact liteContact = (LiteContact) StarsLiteFactory.createLite(contact);
							energyCompany.addContact( liteContact, null );
							
							LiteServiceCompany liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( companyDB );
							energyCompany.addServiceCompany( liteCompany );
							
							StarsServiceCompany starsCompany = new StarsServiceCompany();
							StarsLiteFactory.setStarsServiceCompany( starsCompany, liteCompany, energyCompany );
							ecSettings.getStarsServiceCompanies().addStarsServiceCompany( starsCompany );
							
							valueIDMap.put( assignedValues.get(i), companyDB.getCompanyID() );
						}
					}
				}
				else {
					YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
					
					Object[][] entryData = null;
					if (entryTexts != null) {
						entryData = new Object[ entryTexts.length ][];
						Integer zero = new Integer(0);
						for (int i = 0; i < entryTexts.length; i++) {
							entryData[i] = new Object[3];
							entryData[i][0] = zero;
							entryData[i][1] = entryTexts[i];
							entryData[i][2] = zero;
						}
					}
					
					StarsAdmin.updateYukonListEntries( cList, entryData, energyCompany, conn );
					
					StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
					selectionListTable.put( starsList.getListName(), starsList );
					
					for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
						int entryID = ((YukonListEntry)cList.getYukonListEntries().get(i)).getEntryID();
						valueIDMap.put( assignedValues.get(i), new Integer(entryID) );
					}
				}
			}
			catch (WebClientException e) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
				redirect = referer;
			}
			catch (java.sql.SQLException e) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to assign import values to selection list");
				redirect = referer;
			}
			finally {
				try {
					if (conn != null) conn.close();
				}
				catch (java.sql.SQLException e) {}
			}
		}
		
		// Change the unassigned flag to false
		Hashtable unassignedLists = (Hashtable) session.getAttribute(UNASSIGNED_LISTS);
		unassignedLists.put(listName, new Boolean(false));
	}
	
	private void importStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_DATA);
		ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
		ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
		ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
		ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
		ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
		
		TreeMap[] valueIDMaps = new TreeMap[ LIST_NAMES.length ];
		for (int i = 0; i < LIST_NAMES.length; i++)
			valueIDMaps[i] = (TreeMap) preprocessedData.get( LIST_NAMES[i][0] );
		
		// Replace import values with ids assigned to them
		for (int i = 0; i < acctFieldsList.size(); i++) {
			String[] fields = (String[]) acctFieldsList.get(i);
			
			for (int j = 0; j < SERVINFO_LIST_FIELDS.length; j++) {
				int listIdx = SERVINFO_LIST_FIELDS[j][0];
				int fieldIdx = SERVINFO_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		for (int i = 0; i < invFieldsList.size(); i++) {
			String[] fields = (String[]) invFieldsList.get(i);
			
			for (int j = 0; j < INV_LIST_FIELDS.length; j++) {
				int listIdx = INV_LIST_FIELDS[j][0];
				int fieldIdx = INV_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
			
			if (fields[IDX_DEVICE_NAME].equals("")) {
				// This is a receiver
				for (int j = 0; j < RECV_LIST_FIELDS.length; j++) {
					int listIdx = RECV_LIST_FIELDS[j][0];
					int fieldIdx = RECV_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
		}
		
		for (int i = 0; i < appFieldsList.size(); i++) {
			String[] fields = (String[]) appFieldsList.get(i);
			
			for (int j = 0; j < APP_LIST_FIELDS.length; j++) {
				int listIdx = APP_LIST_FIELDS[j][0];
				int fieldIdx = APP_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
			
			if (fields[IDX_APP_CAT_DEF_ID].equals("")) continue;
			int catDefID = Integer.parseInt( fields[IDX_APP_CAT_DEF_ID] );
			
			if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER) {
				for (int j = 0; j < AC_LIST_FIELDS.length; j++) {
					int listIdx = AC_LIST_FIELDS[j][0];
					int fieldIdx = AC_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER) {
				for (int j = 0; j < WH_LIST_FIELDS.length; j++) {
					int listIdx = WH_LIST_FIELDS[j][0];
					int fieldIdx = WH_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR) {
				for (int j = 0; j < GEN_LIST_FIELDS.length; j++) {
					int listIdx = GEN_LIST_FIELDS[j][0];
					int fieldIdx = GEN_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION) {
				for (int j = 0; j < IRR_LIST_FIELDS.length; j++) {
					int listIdx = IRR_LIST_FIELDS[j][0];
					int fieldIdx = IRR_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER) {
				for (int j = 0; j < GDRY_LIST_FIELDS.length; j++) {
					int listIdx = GDRY_LIST_FIELDS[j][0];
					int fieldIdx = GDRY_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP) {
				for (int j = 0; j < HP_LIST_FIELDS.length; j++) {
					int listIdx = HP_LIST_FIELDS[j][0];
					int fieldIdx = HP_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT) {
				for (int j = 0; j < SH_LIST_FIELDS.length; j++) {
					int listIdx = SH_LIST_FIELDS[j][0];
					int fieldIdx = SH_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
			else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL) {
				for (int j = 0; j < DF_LIST_FIELDS.length; j++) {
					int listIdx = DF_LIST_FIELDS[j][0];
					int fieldIdx = DF_LIST_FIELDS[j][1];
					Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
					fields[fieldIdx] = (id != null)? id.toString() : "0";
				}
			}
		}
		
		for (int i = 0; i < orderFieldsList.size(); i++) {
			String[] fields = (String[]) orderFieldsList.get(i);
			
			for (int j = 0; j < ORDER_LIST_FIELDS.length; j++) {
				int listIdx = ORDER_LIST_FIELDS[j][0];
				int fieldIdx = ORDER_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		for (int i = 0; i < resFieldsList.size(); i++) {
			String[] fields = (String[]) resFieldsList.get(i);
			
			for (int j = 0; j < RES_LIST_FIELDS.length; j++) {
				int listIdx = RES_LIST_FIELDS[j][0];
				int fieldIdx = RES_LIST_FIELDS[j][1];
				Integer id = (Integer) valueIDMaps[listIdx].get( fields[fieldIdx] );
				fields[fieldIdx] = (id != null)? id.toString() : "0";
			}
		}
		
		ImportStarsDataTask task = new ImportStarsDataTask(user, preprocessedData);
		long id = ProgressChecker.addTask( task );
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}

}
