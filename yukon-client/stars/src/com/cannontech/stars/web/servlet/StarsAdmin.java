package com.cannontech.stars.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Pair;
import com.cannontech.database.PoolManager;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteCustomerFAQ;
import com.cannontech.database.data.lite.stars.LiteInterviewQuestion;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.ECToGenericMapping;
import com.cannontech.database.db.stars.customer.CustomerAccount;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.UpdateApplianceAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.UpdateCustAccountAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLoginAction;
import com.cannontech.stars.web.action.UpdateResidenceInfoAction;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.ACType;
import com.cannontech.stars.xml.serialize.AirConditioner;
import com.cannontech.stars.xml.serialize.BinSize;
import com.cannontech.stars.xml.serialize.BlowerEnergySource;
import com.cannontech.stars.xml.serialize.BlowerHeatSource;
import com.cannontech.stars.xml.serialize.BlowerHorsePower;
import com.cannontech.stars.xml.serialize.DryerType;
import com.cannontech.stars.xml.serialize.DualFuel;
import com.cannontech.stars.xml.serialize.EnergySource;
import com.cannontech.stars.xml.serialize.Generator;
import com.cannontech.stars.xml.serialize.GrainDryer;
import com.cannontech.stars.xml.serialize.HeatPump;
import com.cannontech.stars.xml.serialize.HorsePower;
import com.cannontech.stars.xml.serialize.Irrigation;
import com.cannontech.stars.xml.serialize.IrrigationType;
import com.cannontech.stars.xml.serialize.MeterLocation;
import com.cannontech.stars.xml.serialize.MeterVoltage;
import com.cannontech.stars.xml.serialize.NumberOfGallons;
import com.cannontech.stars.xml.serialize.PumpSize;
import com.cannontech.stars.xml.serialize.PumpType;
import com.cannontech.stars.xml.serialize.SecondaryEnergySource;
import com.cannontech.stars.xml.serialize.SoilType;
import com.cannontech.stars.xml.serialize.StandbySource;
import com.cannontech.stars.xml.serialize.StorageHeat;
import com.cannontech.stars.xml.serialize.StorageType;
import com.cannontech.stars.xml.serialize.SwitchOverType;
import com.cannontech.stars.xml.serialize.Tonnage;
import com.cannontech.stars.xml.serialize.TransferSwitchManufacturer;
import com.cannontech.stars.xml.serialize.TransferSwitchType;
import com.cannontech.stars.xml.serialize.WaterHeater;
import com.cannontech.stars.xml.serialize.AnswerType;
import com.cannontech.stars.xml.serialize.BillingAddress;
import com.cannontech.stars.xml.serialize.CompanyAddress;
import com.cannontech.stars.xml.serialize.ConstructionMaterial;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.DecadeBuilt;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.Email;
import com.cannontech.stars.xml.serialize.GeneralCondition;
import com.cannontech.stars.xml.serialize.InstallationCompany;
import com.cannontech.stars.xml.serialize.InsulationDepth;
import com.cannontech.stars.xml.serialize.LMHardware;
import com.cannontech.stars.xml.serialize.LMHardwareType;
import com.cannontech.stars.xml.serialize.Location;
import com.cannontech.stars.xml.serialize.MainCoolingSystem;
import com.cannontech.stars.xml.serialize.MainFuelType;
import com.cannontech.stars.xml.serialize.MainHeatingSystem;
import com.cannontech.stars.xml.serialize.Manufacturer;
import com.cannontech.stars.xml.serialize.MCT;
import com.cannontech.stars.xml.serialize.NumberOfOccupants;
import com.cannontech.stars.xml.serialize.OwnershipType;
import com.cannontech.stars.xml.serialize.PrimaryContact;
import com.cannontech.stars.xml.serialize.QuestionType;
import com.cannontech.stars.xml.serialize.ResidenceType;
import com.cannontech.stars.xml.serialize.SquareFeet;
import com.cannontech.stars.xml.serialize.SULMProgram;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsApp;
import com.cannontech.stars.xml.serialize.StarsApplianceCategory;
import com.cannontech.stars.xml.serialize.StarsCreateLMHardware;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsCustAccount;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsCustomerAddress;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQ;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQGroup;
import com.cannontech.stars.xml.serialize.StarsCustomerFAQs;
import com.cannontech.stars.xml.serialize.StarsDefaultThermostatSettings;
import com.cannontech.stars.xml.serialize.StarsDeleteLMHardware;
import com.cannontech.stars.xml.serialize.StarsEnergyCompany;
import com.cannontech.stars.xml.serialize.StarsEnrollmentPrograms;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestion;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramSignUp;
import com.cannontech.stars.xml.serialize.StarsSULMPrograms;
import com.cannontech.stars.xml.serialize.StarsServiceCompanies;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsSiteInformation;
import com.cannontech.stars.xml.serialize.StarsUpdateAppliance;
import com.cannontech.stars.xml.serialize.StarsUpdateCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsUpdateResidenceInformation;
import com.cannontech.stars.xml.serialize.StarsUpdateLMHardware;
import com.cannontech.stars.xml.serialize.StarsUpdateLogin;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatScheduleResponse;
import com.cannontech.stars.xml.serialize.StreetAddress;
import com.cannontech.stars.xml.serialize.Substation;
import com.cannontech.stars.xml.serialize.Voltage;
import com.cannontech.stars.xml.util.SOAPUtil;

interface ImportFileParser {
	String[] populateFields(String line) throws Exception;
}

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsAdmin extends HttpServlet {
    
    public static final String ENERGY_COMPANY_TEMP = "ENERGY_COMPANY_TEMP";
    public static final String SERVICE_COMPANY_TEMP = "SERVICE_COMPANY_TEMP";
    
	public static final String PREPROCESSED_STARS_DATA = "PREPROCESSED_STARS_DATA";
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
	private static final int IDX_ACCOUNT_ID = 0;
	private static final int IDX_ACCOUNT_NO = 1;
	private static final int IDX_CUSTOMER_TYPE = 2;
	private static final int IDX_COMPANY_NAME = 3;
	private static final int IDX_LAST_NAME = 4;
	private static final int IDX_FIRST_NAME = 5;
	private static final int IDX_HOME_PHONE = 6;
	private static final int IDX_WORK_PHONE = 7;
	private static final int IDX_WORK_PHONE_EXT = 8;
	private static final int IDX_EMAIL = 9;
	private static final int IDX_ACCOUNT_NOTES = 10;
	private static final int IDX_STREET_ADDR1 = 11;
	private static final int IDX_STREET_ADDR2 = 12;
	private static final int IDX_CITY = 13;
	private static final int IDX_STATE = 14;
	private static final int IDX_ZIP_CODE = 15;
	private static final int IDX_COUNTY = 16;
	private static final int IDX_MAP_NO = 17;
	private static final int IDX_PROP_NOTES = 18;
	private static final int IDX_USERNAME = 19;
	private static final int IDX_PASSWORD = 20;
	private static final int IDX_SUBSTATION = 21;
	private static final int IDX_FEEDER = 22;
	private static final int IDX_POLE = 23;
	private static final int IDX_TRFM_SIZE = 24;
	private static final int IDX_SERV_VOLT = 25;
	private static final int NUM_ACCOUNT_FIELDS = 26;
	
	// Inventory fields
	// IDX_ACCOUNT_ID = 0
	private static final int IDX_INV_ID = 1;
	private static final int IDX_SERIAL_NO = 2;
	private static final int IDX_DEVICE_TYPE = 3;
	private static final int IDX_RECEIVE_DATE = 4;
	private static final int IDX_INSTALL_DATE = 5;
	private static final int IDX_REMOVE_DATE = 6;
	private static final int IDX_SERVICE_COMPANY = 7;
	private static final int IDX_ALT_TRACK_NO = 8;
	private static final int IDX_DEVICE_VOLTAGE = 9;
	private static final int IDX_DEVICE_STATUS = 10;
	private static final int IDX_INV_NOTES = 11;
	private static final int IDX_DEVICE_NAME = 12;
	private static final int IDX_ADDR_GROUP = 13;
	private static final int IDX_HARDWARE_ACTION = 14;
	private static final int IDX_R1_GROUP = 15;
	private static final int IDX_R2_GROUP = 16;
	private static final int IDX_R3_GROUP = 17;
	private static final int IDX_R1_STATUS = 18;
	private static final int IDX_R2_STATUS = 19;
	private static final int IDX_R3_STATUS = 20;
	private static final int NUM_INV_FIELDS = 21;
	
	// Appliance fields
	// IDX_ACCOUNT_ID = 0
	// IDX_INV_ID = 1;
	private static final int IDX_APP_ID = 2;
	private static final int IDX_RELAY_NUM = 3;
	private static final int IDX_APP_TYPE = 4;
	private static final int IDX_APP_NOTES = 5;
	private static final int IDX_MANUFACTURER = 6;
	private static final int IDX_YEAR_MADE = 7;
	private static final int IDX_APP_KW = 8;
	private static final int IDX_AC_TONNAGE = 9;
	private static final int IDX_WH_NUM_GALLONS = 10;
	private static final int IDX_WH_NUM_ELEMENTS = 11;
	private static final int IDX_WH_ENERGY_SRC = 12;
	private static final int IDX_GEN_TRAN_SWITCH_TYPE = 13;
	private static final int IDX_GEN_TRAN_SWITCH_MFC = 14;
	private static final int IDX_GEN_CAPACITY = 15;
	private static final int IDX_GEN_FUEL_CAP = 16;
	private static final int IDX_GEN_START_DELAY = 17;
	private static final int IDX_IRR_TYPE = 18;
	private static final int IDX_IRR_HORSE_POWER = 19;
	private static final int IDX_IRR_ENERGY_SRC = 20;
	private static final int IDX_IRR_SOIL_TYPE = 21;
	private static final int IDX_IRR_METER_LOC = 22;
	private static final int IDX_IRR_METER_VOLT = 23;
	private static final int IDX_GDRY_TYPE = 24;
	private static final int IDX_GDRY_BIN_SIZE = 25;
	private static final int IDX_GDRY_ENERGY_SRC = 26;
	private static final int IDX_GDRY_HORSE_POWER = 27;
	private static final int IDX_GDRY_HEAT_SRC = 28;
	private static final int IDX_HP_TYPE = 29;
	private static final int IDX_HP_SIZE = 30;
	private static final int IDX_HP_STANDBY_SRC = 31;
	private static final int IDX_HP_RESTART_DELAY = 32;
	private static final int IDX_SH_TYPE = 33;
	private static final int IDX_SH_CAPACITY = 34;
	private static final int IDX_SH_RECHARGE_TIME = 35;
	private static final int IDX_DF_SWITCH_OVER_TYPE = 36;
	private static final int IDX_DF_2ND_CAPACITY = 37;
	private static final int IDX_DF_2ND_ENERGY_SRC = 38;
	private static final int IDX_APP_CAT_DEF_ID = 39;
	private static final int NUM_APP_FIELDS = 40;
	
	// Work order fields
	// IDX_ACCOUNT_ID = 0
	// IDX_INV_ID = 1;
	private static final int IDX_ORDER_NO = 2;
	private static final int IDX_ORDER_TYPE = 3;
	private static final int IDX_ORDER_STATUS = 4;
	private static final int IDX_ORDER_CONTRACTOR = 5;
	private static final int IDX_DATE_REPORTED = 6;
	private static final int IDX_DATE_SCHEDULED = 7;
	private static final int IDX_TIME_SCHEDULED = 8;
	private static final int IDX_DATE_COMPLETED = 9;
	private static final int IDX_ORDER_DESC = 10;
	private static final int IDX_ACTION_TAKEN = 11;
	private static final int NUM_ORDER_FIELDS = 12;
	
	// Customer residence fields
	// IDX_ACCOUNT_ID = 0
	private static final int IDX_RES_TYPE = 1;
	private static final int IDX_CONSTRUCTION_TYPE = 2;
	private static final int IDX_DECADE_BUILT = 3;
	private static final int IDX_SQUARE_FEET = 4;
	private static final int IDX_INSULATION_DEPTH = 5;
	private static final int IDX_GENERAL_COND = 6;
	private static final int IDX_MAIN_COOLING_SYS = 7;
	private static final int IDX_MAIN_HEATING_SYS = 8;
	private static final int IDX_NUM_OCCUPANTS = 9;
	private static final int IDX_OWNERSHIP_TYPE = 10;
	private static final int IDX_MAIN_FUEL_TYPE = 11;
	private static final int IDX_RES_NOTES = 12;
	private static final int NUM_RES_FIELDS = 13;
	
	// Tables of index of lists(in LIST_NAMES) and index of fields(in corresponding field definition above)
	private static final int[][] SERVINFO_LIST_FIELDS = {
		{0, IDX_SUBSTATION},
	};
	
	private static final int[][] INV_LIST_FIELDS = {
		{1, IDX_DEVICE_TYPE},
		{2, IDX_DEVICE_VOLTAGE},
		{3, IDX_SERVICE_COMPANY},
	};
	
	private static final int[][] RECV_LIST_FIELDS = {
		{4, IDX_DEVICE_STATUS},
		{5, IDX_R1_GROUP},
		{5, IDX_R2_GROUP},
		{5, IDX_R3_GROUP},
	};
	
	private static final int[][] APP_LIST_FIELDS = {
		{6, IDX_APP_TYPE},
		{7, IDX_MANUFACTURER},
	};
	
	private static final int[][] AC_LIST_FIELDS = {
		{8, IDX_AC_TONNAGE},
	};
	
	private static final int[][] WH_LIST_FIELDS = {
		{9, IDX_WH_NUM_GALLONS},
		{10, IDX_WH_ENERGY_SRC},
	};
	
	private static final int[][] GEN_LIST_FIELDS = {
		{11, IDX_GEN_TRAN_SWITCH_TYPE},
		{12, IDX_GEN_TRAN_SWITCH_MFC},
	};
	
	private static final int[][] IRR_LIST_FIELDS = {
		{13, IDX_IRR_TYPE},
		{14, IDX_IRR_ENERGY_SRC},
		{15, IDX_IRR_HORSE_POWER},
		{16, IDX_IRR_METER_VOLT},
		{17, IDX_IRR_METER_LOC},
		{18, IDX_IRR_SOIL_TYPE},
	};
	
	private static final int[][] GDRY_LIST_FIELDS = {
		{19, IDX_GDRY_TYPE},
		{20, IDX_GDRY_ENERGY_SRC},
		{21, IDX_GDRY_HORSE_POWER},
		{22, IDX_GDRY_HEAT_SRC},
		{23, IDX_GDRY_BIN_SIZE},
	};
	
	private static final int[][] HP_LIST_FIELDS = {
		{24, IDX_HP_TYPE},
		{25, IDX_HP_SIZE},
		{26, IDX_HP_STANDBY_SRC},
	};
	
	private static final int[][] SH_LIST_FIELDS = {
		{27, IDX_SH_TYPE},
	};
	
	private static final int[][] DF_LIST_FIELDS = {
		{28, IDX_DF_SWITCH_OVER_TYPE},
		{29, IDX_DF_2ND_ENERGY_SRC},
	};
	
	private static final int[][] ORDER_LIST_FIELDS = {
		{30, IDX_ORDER_STATUS},
		{31, IDX_ORDER_TYPE},
		{3, IDX_ORDER_CONTRACTOR},
	};
	
	private static final int[][] RES_LIST_FIELDS = {
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
    
    private static final String NEW_ADDRESS = "NEW_ADDRESS";
    
    private static final String HARDWARE_ACTION_INSERT = "INSERT";
    private static final String HARDWARE_ACTION_UPDATE = "UPDATE";
    private static final String HARDWARE_ACTION_REMOVE = "REMOVE";
    
    private static final java.text.SimpleDateFormat starsDateFormat =
    		new java.text.SimpleDateFormat( "yyyyMMdd" );
    private static final java.text.SimpleDateFormat starsTimeFormat =
    		new java.text.SimpleDateFormat( "HHmm" );
    
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
				String[] fields = new String[NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_ACCOUNT_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_ACCOUNT_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );

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
				String[] fields = new String[ NUM_INV_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
					
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_INV_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_INV_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_ORDER_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_RES_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
				String[] fields = new String[ NUM_APP_FIELDS ];
				for (int i = 0; i < fields.length; i++)
					fields[i] = "";
				
				StreamTokenizer st = prepareStreamTokenzier( line );
				
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
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
        	resp.sendRedirect( req.getContextPath() + SOAPClient.LOGIN_URL );
        	return;
        }
        
        StarsYukonUser user = (StarsYukonUser)
        		session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        
    	referer = req.getHeader( "referer" );
    	redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
    	if (redirect == null) redirect = referer;
    	
		String action = req.getParameter( "action" );
		if (action == null) action = "";
		
		if (action.equalsIgnoreCase("DeleteCustAccounts"))
			deleteCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase("ImportCustAccounts"))
			importCustomerAccounts( user, req, session );
		else if (action.equalsIgnoreCase("PreprocessStarsData"))
			preprocessStarsData( user, req, session );
		else if (action.equalsIgnoreCase("AssignSelectionList"))
			assignSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("ImportStarsData"))
			importStarsData( user, req, session );
		else if (action.equalsIgnoreCase("UpdateAddress"))
			updateAddress( user, req, session );
		else if (action.equalsIgnoreCase("UpdateEnergyCompany"))
			updateEnergyCompany( user, req, session );
		else if (action.equalsIgnoreCase("UpdateApplianceCategory"))
			updateApplianceCategory( user, req, session );
		else if (action.equalsIgnoreCase("DeleteApplianceCategory"))
			deleteApplianceCategory( user, req, session );
		else if (action.equalsIgnoreCase("UpdateServiceCompany"))
			updateServiceCompany( user, req, session );
		else if (action.equalsIgnoreCase("DeleteServiceCompany"))
			deleteServiceCompany( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQLink"))
			updateCustomerFAQLink( user, req, session );
		else if (action.equalsIgnoreCase("UpdateFAQSubjects"))
			updateCustomerFAQSubjects( user, req, session );
		else if (action.equalsIgnoreCase("UpdateCustomerFAQs"))
			updateCustomerFAQs( user, req, session );
		else if (action.equalsIgnoreCase("DeleteFAQSubject"))
			deleteFAQSubject( user, req, session );
		else if (action.equalsIgnoreCase("UpdateInterviewQuestions"))
			updateInterviewQuestions( user, req, session );
		else if (action.equalsIgnoreCase("UpdateSelectionList"))
			updateCustomerSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("UpdateThermostatSchedule"))
			updateThermostatSchedule( user, req, session );
		else if (action.equalsIgnoreCase("NewEnergyCompany"))
			createEnergyCompany( user, req, session );
		else if (action.equalsIgnoreCase("DeleteEnergyCompany"))
			deleteEnergyCompany( user, req, session );
        
    	resp.sendRedirect( redirect );
	}
	
	private void setStarsCustAccount(StarsCustAccount account, String[] fields, LiteStarsEnergyCompany energyCompany) {
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
	
	private LiteStarsCustAccountInformation newCustomerAccount(String[] fields, StarsYukonUser user, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		// Build the request message
		StarsNewCustomerAccount newAccount = new StarsNewCustomerAccount();
		
		StarsCustomerAccount account = new StarsCustomerAccount();
		setStarsCustAccount( account, fields, energyCompany );
		newAccount.setStarsCustomerAccount( account );
		
		if (fields[IDX_USERNAME].trim().length() > 0) {
			StarsUpdateLogin login = new StarsUpdateLogin();
			login.setUsername( fields[IDX_USERNAME] );
			login.setPassword( fields[IDX_PASSWORD] );
			newAccount.setStarsUpdateLogin( login );
		}
		
		return NewCustAccountAction.newCustomerAccount( newAccount, user, energyCompany );
	}
	
	private void updateCustomerAccount(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, java.sql.Connection conn)
		throws Exception
	{
        StarsUpdateCustomerAccount updateAccount = new StarsUpdateCustomerAccount();
        setStarsCustAccount( updateAccount, fields, energyCompany );
        
        UpdateCustAccountAction.updateCustomerAccount( updateAccount, liteAcctInfo, energyCompany, conn );
	}
	
	private void updateLogin(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		LiteContact liteContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
		if (liteContact.getLoginID() == com.cannontech.user.UserUtils.USER_YUKON_ID
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
	
	private int getDeviceTypeID(LiteStarsEnergyCompany energyCompany, String deviceType) {
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
	
	private void setStarsInventory(StarsInv inv, String[] fields, LiteStarsEnergyCompany energyCompany) {
		if (fields[IDX_ALT_TRACK_NO].length() > 0)
			inv.setAltTrackingNumber( fields[IDX_ALT_TRACK_NO] );
		if (fields[IDX_INV_NOTES].length() > 0)
			inv.setNotes( fields[IDX_INV_NOTES] );
		
		starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
		
		if (fields[IDX_RECEIVE_DATE].length() > 0) {
			try {
				inv.setReceiveDate( starsDateFormat.parse(fields[IDX_RECEIVE_DATE]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_INSTALL_DATE].length() > 0) {
			inv.setInstallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
					fields[IDX_INSTALL_DATE], energyCompany.getDefaultTimeZone()) );
			
			if (inv.getInstallDate() == null) {
				try {
					inv.setInstallDate( starsDateFormat.parse(fields[IDX_INSTALL_DATE]) );
				}
				catch (java.text.ParseException e) {}
			}
		}
		
		if (fields[IDX_REMOVE_DATE].length() > 0) {
			inv.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
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
		
		if (fields[IDX_DEVICE_NAME].equals("")) {
			LMHardware hw = new LMHardware();
			LMHardwareType hwType = new LMHardwareType();
			hwType.setEntryID( Integer.parseInt(fields[IDX_DEVICE_TYPE]) );
			hw.setLMHardwareType( hwType );
			hw.setManufacturerSerialNumber( fields[IDX_SERIAL_NO] );
			
			inv.setLMHardware( hw );
		}
		else {
			// Now we only have meters
			MCT mct = new MCT();
			mct.setDeviceName( fields[IDX_DEVICE_NAME] );
			
			inv.setMCT( mct );
		}
	}
	
	private LiteInventoryBase insertLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws Exception
	{
		// Check inventory and build request message
		int devTypeID = getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
		if (devTypeID == -1)
			throw new WebClientException("Invalid device type \"" + fields[IDX_DEVICE_TYPE] + "\"");
		
		StarsCreateLMHardware createHw = null;
		LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
		
		if (liteHw != null) {
			if (liteHw.getAccountID() == liteAcctInfo.getAccountID())
				throw new WebClientException("Cannot insert hardware with serial # " + fields[IDX_SERIAL_NO] + " because it already exists");
			
			createHw = new StarsCreateLMHardware();
			StarsLiteFactory.setStarsInv( createHw, liteHw, energyCompany );
			createHw.setInstallDate( new java.util.Date() );
			createHw.setRemoveDate( new java.util.Date(0) );
			createHw.setInstallationNotes( "" );
		}
		else {
			createHw = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
		}
		
		fields[IDX_DEVICE_TYPE] = String.valueOf( devTypeID );
		setStarsInventory( createHw, fields, energyCompany );
        
        return CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany, conn );
	}
	
	private void updateLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, java.sql.Connection conn) throws Exception
	{
		if (liteAcctInfo.getInventories().size() != 1)
			throw new WebClientException( "More than one hardware in the account, cannot determine which one to be updated" );
		
		// Check inventory and build request message
		int devTypeID = getDeviceTypeID( energyCompany, fields[IDX_DEVICE_TYPE] );
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
		setStarsInventory( updateHw, fields, energyCompany );
		
		int invIDOld = ((Integer) liteAcctInfo.getInventories().get(0)).intValue();
		
		if (liteHw == null || liteHw.getInventoryID() != invIDOld) {
			// Update information doesn't match the old hardware, remove the old one
			StarsDeleteLMHardware deleteHw = new StarsDeleteLMHardware();
			deleteHw.setInventoryID( invIDOld );
			deleteHw.setDeleteFromInventory( false );
			if (fields[IDX_REMOVE_DATE].length() > 0) {
				deleteHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
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
	
	private void removeLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
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
			deleteHw.setRemoveDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(
					fields[IDX_REMOVE_DATE], energyCompany.getDefaultTimeZone()) );
		}
		
		DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany, conn );
	}
	
	private void programSignUp(ArrayList programs, LiteStarsCustAccountInformation liteAcctInfo,
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
	
	private void deleteCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		try {
			String acctNo = req.getParameter( "AcctNo" ).replace( '*', '%' );
			int[] accountIDs = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), acctNo );
			
			if (accountIDs != null) {
				for (int i = 0; i < accountIDs.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[i], true );
					DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
				}
				
				if (accountIDs.length > 1)
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accountIDs.length + " customer accounts have been deleted" );
				else
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, accountIDs.length + " customer account has been deleted" );
			}
			else
	        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Search for account number failed");
		}
		catch (Exception e) {
			e.printStackTrace();
        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Delete customer accounts failed");
		}
	}
	
	private ArrayList readFile(File file) {
		if (file.exists()) {
			try {
				java.io.BufferedReader fr = new java.io.BufferedReader(
						new java.io.FileReader(file) );
				ArrayList lines = new ArrayList();
				String line = null;
				
				while ((line = fr.readLine()) != null) {
					if (line.charAt(0) == '#') continue;
					if (line.length() > 0) lines.add(line);
				}
				
				fr.close();
				return lines;
			}
			catch (IOException e) {
				e.printStackTrace();
				CTILogger.error("Failed to read file \"" + file.getPath() + "\"");
			}
		}
		else {
			CTILogger.error("Unable to find file \"" + file.getPath() + "\"");
		}
		
		return null;
	}
	
	private void writeFile(File file, ArrayList lines) throws IOException {
		java.io.PrintWriter fw = new java.io.PrintWriter(
				new java.io.BufferedWriter( new java.io.FileWriter(file) ));
		
		for (int i = 0; i < lines.size(); i++)
			fw.println( (String)lines.get(i) );
		
		fw.close();
	}
	
	private void importCustomerAccounts(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		int numAdded = 0;
		int numUpdated = 0;
		int lineNo = 0;
		java.sql.Connection conn = null;
		
		try {
			String importID = ServerUtils.forceNotNone(
					AuthFuncs.getRolePropertyValue(user.getYukonUser(), ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT) );
			ImportFileParser parser = (ImportFileParser) parsers.get(importID);
			if (parser == null)
				throw new WebClientException("Invalid import ID: " + importID);
			
			String importFile = req.getParameter( "ImportFile" );
			ArrayList lines = readFile( new File(importFile) );
			if (lines == null)
				throw new WebClientException("Failed to read file '" + importFile + "'");
			
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (lineNo = 1; lineNo <= lines.size(); lineNo++) {
				String line = (String) lines.get(lineNo - 1);
				
				String[] custFields = parser.populateFields( line );
				String[] invFields = new String[ NUM_INV_FIELDS ];
				System.arraycopy( custFields, NUM_ACCOUNT_FIELDS, invFields, 0, NUM_INV_FIELDS );
				
				// Process request parameters for program enrollment
				ArrayList programs = new ArrayList();
				String[] catIDs = req.getParameterValues( "CatID" );
				String[] progIDs = req.getParameterValues( "ProgID" );
				
				if (progIDs != null) {
					for (int i = 0; i < progIDs.length; i++) {
						if (progIDs[i].length() == 0) continue;
						
						int progID = Integer.parseInt(progIDs[i]);
						LiteLMProgram liteProg = energyCompany.getLMProgram( progID );
						
						int groupID = 0;
						if (invFields[IDX_ADDR_GROUP].length() > 0 && liteProg.getGroupIDs() != null) {
							for (int j = 0; j < liteProg.getGroupIDs().length; j++) {
								if (PAOFuncs.getYukonPAOName( liteProg.getGroupIDs()[j] ).equalsIgnoreCase( invFields[IDX_ADDR_GROUP] ))
								{
									groupID = liteProg.getGroupIDs()[j];
									break;
								}
							}
						}
						
						int[] prog = new int[3];
						prog[0] = progID;
						prog[1] = Integer.parseInt(catIDs[i]);
						prog[2] = groupID;
						programs.add( prog );
					}
				}
				
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.searchByAccountNumber( custFields[IDX_ACCOUNT_NO] );
				
				if (liteAcctInfo == null) {
					liteAcctInfo = newCustomerAccount( custFields, user, energyCompany );
					
					LiteInventoryBase liteInv = null;
					if (!invFields[IDX_SERIAL_NO].equalsIgnoreCase(""))
						liteInv = insertLMHardware( invFields, liteAcctInfo, energyCompany, conn );
					
					Integer invID = null;
					if (liteInv != null)
						invID = new Integer( liteInv.getInventoryID() );
					programSignUp( programs, liteAcctInfo, invID, energyCompany, conn );
					
					numAdded++;
				}
				else {
					updateCustomerAccount( custFields, liteAcctInfo, energyCompany, conn );
					//updateLogin( fields, liteAcctInfo, energyCompany, session );
					
					if (!invFields[IDX_SERIAL_NO].equalsIgnoreCase(""))
					{
						if (invFields[IDX_HARDWARE_ACTION].equalsIgnoreCase( HARDWARE_ACTION_INSERT ))
							insertLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						else if (invFields[IDX_HARDWARE_ACTION].equalsIgnoreCase( HARDWARE_ACTION_UPDATE ))
							updateLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						else if (invFields[IDX_HARDWARE_ACTION].equalsIgnoreCase( HARDWARE_ACTION_REMOVE ))
							removeLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						else {	// Hardware action field not specified
							if (liteAcctInfo.getInventories().size() == 0)
								insertLMHardware( invFields, liteAcctInfo, energyCompany, conn );
							else
								updateLMHardware( invFields, liteAcctInfo, energyCompany, conn );
						}
					}
					
					if (liteAcctInfo.getInventories().size() > 0) {
						Integer dftInvID = null;
						if (liteAcctInfo.getInventories().size() == 1)
							dftInvID = (Integer) liteAcctInfo.getInventories().get(0);
						programSignUp( programs, liteAcctInfo, dftInvID, energyCompany, conn );
					}
					
					numUpdated++;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			
			String errorMsg = null;
			if (lineNo > 0)
				errorMsg = "Error encountered when processing line #" + lineNo;
			else
				errorMsg = "Failed to import customer accounts";
			
			if (e instanceof WebClientException)
				errorMsg += ": " + e.getMessage();
			
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, errorMsg);
			redirect = referer;
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		if (numAdded + numUpdated > 0) {
			StringBuffer confirmMsg = new StringBuffer();
			if (numAdded > 0)
				confirmMsg.append(numAdded).append(" customer accounts added");
			if (numUpdated > 0) {
				if (numAdded > 0) confirmMsg.append(", ");
				confirmMsg.append(numUpdated).append(" customer accounts updated");
			}
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, confirmMsg.toString());
		}
	}
	
	private void setStarsAppliance(StarsApp app, String[] fields, LiteStarsEnergyCompany energyCompany) {
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
	
	private void updateAppliance(String[] fields, int appID, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws Exception
	{
		StarsUpdateAppliance updateApp = new StarsUpdateAppliance();
		updateApp.setApplianceID( appID );
		setStarsAppliance( updateApp, fields, energyCompany );
		
		UpdateApplianceAction.updateAppliance( updateApp, liteAcctInfo );
	}
	
	private String formatTimeString(String str) {
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
	
	private void newServiceRequest(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
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
		
		starsDateFormat.setTimeZone( energyCompany.getDefaultTimeZone() );
		
		if (fields[IDX_DATE_REPORTED].length() > 0) {
			try {
				createOrder.setDateReported( starsDateFormat.parse(fields[IDX_DATE_REPORTED]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_DATE_COMPLETED].length() > 0) {
			try {
				createOrder.setDateCompleted( starsDateFormat.parse(fields[IDX_DATE_COMPLETED]) );
			}
			catch (java.text.ParseException e) {}
		}
		
		if (fields[IDX_DATE_SCHEDULED].length() > 0) {
			try {
				Date datePart = starsDateFormat.parse( fields[IDX_DATE_SCHEDULED] );
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
	
	private void newResidenceInfo(String[] fields, LiteStarsCustAccountInformation liteAcctInfo)
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
			
			ArrayList custLines = readFile( custFile );
			ArrayList servInfoLines = readFile( servInfoFile );
			ArrayList invLines = readFile( invFile );
			ArrayList recvrLines = readFile( recvrFile );
			ArrayList meterLines = readFile( meterFile );
			ArrayList loadInfoLines = readFile( loadInfoFile );
			ArrayList acInfoLines = readFile( acInfoFile );
			ArrayList whInfoLines = readFile( whInfoFile );
			ArrayList genInfoLines = readFile( genInfoFile );
			ArrayList irrInfoLines = readFile( irrInfoFile );
			ArrayList gdryInfoLines = readFile( gdryInfoFile );
			ArrayList hpInfoLines = readFile( hpInfoFile );
			ArrayList shInfoLines = readFile( shInfoFile );
			ArrayList dfInfoLines = readFile( dfInfoFile );
			ArrayList genlInfoLines = readFile( genlInfoFile );
			ArrayList workOrderLines = readFile( workOrderFile );
			ArrayList resInfoLines = readFile( resInfoFile );
			
			Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_STARS_DATA);
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
					Hashtable acctIDMap = new Hashtable();
					
					for (int i = 0; i < custLines.size(); i++) {
						String[] fields = ((String) custLines.get(i)).split(",");
						if (fields.length != 2)
							throw new WebClientException("Invalid format of file '" + custFile.getPath() + "'");
						acctIDMap.put( Integer.valueOf(fields[0]), Integer.valueOf(fields[1]) );
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
					Hashtable appIDMap = new Hashtable();
					
					for (int i = 0; i < invLines.size(); i++) {
						String[] fields = ((String) invLines.get(i)).split(",");
						if (fields.length != 4)
							throw new WebClientException("Invalid format of file '" + invFile.getPath() + "'");
						
						Integer invID = Integer.valueOf( fields[0] );
						int[] appIDs = new int[3];
						for (int j = 0; j < 3; j++)
							appIDs[j] = Integer.parseInt( fields[j+1] );
						
						appIDMap.put( invID, appIDs );
					}
					
					preprocessedData.put("HwConfigAppMap", appIDMap);
				}
				else {
					ImportFileParser parser = (ImportFileParser) parsers.get("STARS Inventory");
					
					for (int i = 0; i < invLines.size(); i++) {
						String[] fields = parser.populateFields( (String)invLines.get(i) );
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
			session.setAttribute(PREPROCESSED_STARS_DATA, preprocessedData);
			
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
		String[] values = req.getParameterValues("ImportValue");
		String[] entryIDs = req.getParameterValues("EntryID");
		String[] entryTexts = req.getParameterValues("EntryText");
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_STARS_DATA);
		TreeMap valueIDMap = (TreeMap) preprocessedData.get(listName);
		
		java.sql.Connection conn = null;
		boolean autoCommit = true;
		
		try {
			if (entryIDs != null) {
				for (int i = 0; i < values.length; i++)
					valueIDMap.put( values[i], Integer.valueOf(entryIDs[i]) );
			}
			else {
				conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				if (listName.equals("ServiceCompany")) {
					// Should first delete all existing service companies, but we will assume there is none
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
						
						valueIDMap.put( values[i], companyDB.getCompanyID() );
					}
				}
				else {
					YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
					ArrayList cEntries = cList.getYukonListEntries();
					
					if (listName.equals("Substation")) {
						// Should first delete all existing substations, but we will assume there is none
						for (int i = 0; i < entryTexts.length; i++) {
							com.cannontech.database.data.stars.Substation substation =
									new com.cannontech.database.data.stars.Substation();
							substation.getSubstation().setSubstationName( entryTexts[i] );
							substation.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
							substation.setDbConnection( conn );
							substation.add();
						
							YukonListEntry cEntry = new YukonListEntry();
							cEntry.setEntryID( substation.getSubstation().getSubstationID().intValue() );
							cEntry.setEntryText( substation.getSubstation().getSubstationName() );
							cEntries.add( cEntry );
							
							valueIDMap.put( values[i], new Integer(cEntry.getEntryID()) );
						}
					}
					else {	// All other selection lists
						autoCommit = conn.getAutoCommit();
						conn.setAutoCommit( false );
						
						// Delete all existing entries first
						for (int i = 0; i < cEntries.size(); i++) {
							int entryID = ((YukonListEntry) cEntries.get(i)).getEntryID();
							
							try {
								com.cannontech.database.db.constants.YukonListEntry entry =
										new com.cannontech.database.db.constants.YukonListEntry();
								entry.setEntryID( new Integer(entryID) );
								entry.setDbConnection( conn );
								entry.delete();
							}
							catch (java.sql.SQLException e) {
								e.printStackTrace();
								throw new WebClientException("Cannot delete list entry with id = " + entryID + ", make sure it is not referenced");
							}
						}
						
						conn.commit();
						conn.setAutoCommit( autoCommit );
						
						for (int i = 0; i < cEntries.size(); i++) {
							YukonListEntry entry = (YukonListEntry) cEntries.get(i);
							YukonListFuncs.getYukonListEntries().remove( new Integer(entry.getEntryID()) );
						}
						cEntries.clear();
						
						for (int i = 0; i < entryTexts.length; i++) {
							com.cannontech.database.db.constants.YukonListEntry entry =
									new com.cannontech.database.db.constants.YukonListEntry();
							entry.setListID( new Integer(cList.getListID()) );
							entry.setEntryText( entryTexts[i] );
							entry.setDbConnection( conn );
							entry.add();
							
							com.cannontech.common.constants.YukonListEntry cEntry =
									new com.cannontech.common.constants.YukonListEntry();
							StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
							cEntries.add( cEntry );
							YukonListFuncs.getYukonListEntries().put( new Integer(cEntry.getEntryID()), cEntry );
							
							valueIDMap.put( values[i], new Integer(cEntry.getEntryID()) );
						}
					}
					
					StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
					selectionListTable.put( starsList.getListName(), starsList );
				}
			}
			
			// Change the unassigned flag to false
			Hashtable unassignedLists = (Hashtable) session.getAttribute(UNASSIGNED_LISTS);
			unassignedLists.put(listName, new Boolean(false));
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
			
			try {
				if (conn != null) {
					conn.rollback();
					conn.setAutoCommit( autoCommit );
				} 
			}
			catch (java.sql.SQLException e2) {}
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
	
	private void importStarsData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_STARS_DATA);
		ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
		ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
		ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
		ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
		ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
		
		TreeMap[] valueIDMaps = new TreeMap[ LIST_NAMES.length ];
		for (int i = 0; i < LIST_NAMES.length; i++)
			valueIDMaps[i] = (TreeMap) preprocessedData.get( LIST_NAMES[i][0] );
		
		// Map of import_account_id(Integer) to db_account_id(Integer) or LiteStarsCustAccountInformation object
		Hashtable acctIDMap = (Hashtable) preprocessedData.get( "CustomerAccountMap" );
		if (acctIDMap == null) {
			acctIDMap = new Hashtable();
			preprocessedData.put( "CustomerAccountMap", acctIDMap );
		}
		
		// Map of import_inv_id(Integer) to db_app_ids (int[]: {relay1_app_id, relay2_app_id, relay3_app_id})
		Hashtable appIDMap = (Hashtable) preprocessedData.get( "HwConfigAppMap" );
		if (appIDMap == null) {
			appIDMap = new Hashtable();
			preprocessedData.put( "HwConfigAppMap", appIDMap );
		}
		
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
		
		int numAcctAdded = 0;
		int numInvAdded = 0;
		int numRecvrAdded = 0;
		int numMeterAdded = 0;
		int numAppAdded = 0;
		int numACAdded = 0;
		int numWHAdded = 0;
		int numGenAdded = 0;
		int numIrrAdded = 0;
		int numGDryAdded = 0;
		int numHPAdded = 0;
		int numSHAdded = 0;
		int numDFAdded = 0;
		int numGenlAdded = 0;
		int numAppUpdated = 0;
		int numOrderAdded = 0;
		int numResAdded = 0;
		
		int lineNo = 0;
		java.sql.Connection conn = null;
		ArrayList logMsg = new ArrayList();
		String errorMsg = null;
		
		long startTime = System.currentTimeMillis();
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (lineNo = 1; lineNo <= acctFieldsList.size(); lineNo++) {
				String[] fields = (String[]) acctFieldsList.get(lineNo - 1);
				LiteStarsCustAccountInformation liteAcctInfo = newCustomerAccount( fields, user, energyCompany );
				acctIDMap.put( Integer.valueOf(fields[IDX_ACCOUNT_ID]), liteAcctInfo );
				
				numAcctAdded++;
			}
			
			for (lineNo = 1; lineNo <= invFieldsList.size(); lineNo++) {
				String[] fields = (String[]) invFieldsList.get(lineNo - 1);
				Integer acctID = Integer.valueOf( fields[IDX_ACCOUNT_ID] );
				
				if (acctID.intValue() < 0) {
					// The hardware is not assigned to any account, add it to inventory
					int devTypeID = Integer.parseInt( fields[IDX_DEVICE_TYPE] );
					LiteStarsLMHardware liteHw = energyCompany.searchForLMHardware( devTypeID, fields[IDX_SERIAL_NO] );
					if (liteHw != null)
						throw new Exception("Cannot insert hardware with serial # " + fields[IDX_SERIAL_NO] + " because it already exists");
					
					StarsCreateLMHardware createHw = (StarsCreateLMHardware) StarsFactory.newStarsInv(StarsCreateLMHardware.class);
					setStarsInventory( createHw, fields, energyCompany );
					
					com.cannontech.database.data.stars.hardware.LMHardwareBase hardware =
							new com.cannontech.database.data.stars.hardware.LMHardwareBase();
					com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hardware.getLMHardwareBase();
					com.cannontech.database.db.stars.hardware.InventoryBase invDB = hardware.getInventoryBase();
					
					StarsFactory.setInventoryBase( invDB, createHw );
					invDB.setCategoryID( new Integer(ECUtils.getInventoryCategoryID(devTypeID, energyCompany)) );
					hwDB.setManufacturerSerialNumber( fields[IDX_SERIAL_NO] );
					hwDB.setLMHardwareTypeID( new Integer(devTypeID) );
					hardware.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					hardware.setDbConnection( conn );
					hardware.add();
					
					liteHw = new LiteStarsLMHardware();
					StarsLiteFactory.setLiteStarsLMHardware( liteHw, hardware );
					energyCompany.addInventory( liteHw );
				}
				else {
					LiteStarsCustAccountInformation liteAcctInfo = null;
					
					Object obj = acctIDMap.get(acctID);
					if (obj != null) {
						if (obj instanceof LiteStarsCustAccountInformation) {
							liteAcctInfo = (LiteStarsCustAccountInformation) obj;
						}
						else if (obj instanceof Integer) {
							// This is loaded from file customer.map
							liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
							if (liteAcctInfo != null)
								acctIDMap.put(acctID, liteAcctInfo);
						}
					}
					
					if (liteAcctInfo == null)
						throw new Exception("Cannot find customer account with id=" + acctID.intValue());
					
					LiteInventoryBase liteInv = insertLMHardware( fields, liteAcctInfo, energyCompany, conn );
					
					if (liteInv.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL)
						logMsg.add("Receiver (import_inv_id=" + fields[IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") is out of service");
					else if (liteInv.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL)
						logMsg.add("Receiver (import_inv_id=" + fields[IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") is temporarily out of service");
					
					// If load groups for relays are specified, automatically add appliances through program enrollment
					ArrayList appCats = energyCompany.getAllApplianceCategories();
					ArrayList programs = new ArrayList();
					int[] progIDs = new int[3];	// Save the program ID on each relay here so we can map them to appliance id later
					
					for (int i = 0; i < 3; i++) {
						if (fields[IDX_R1_GROUP + i].equals("") || fields[IDX_R1_GROUP + i].equals("0"))
							continue;
						
						int groupID = Integer.parseInt( fields[IDX_R1_GROUP + i] );
						int progID = 0;
						int appCatID = 0;
						
						for (int j = 0; j < appCats.size(); j++) {
							LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(j);
							for (int k = 0; k < liteAppCat.getPublishedPrograms().length; k++) {
								LiteLMProgram liteProg = liteAppCat.getPublishedPrograms()[k];
								for (int l = 0; l < liteProg.getGroupIDs().length; l++) {
									if (liteProg.getGroupIDs()[l] == groupID) {
										progID = liteProg.getProgramID();
										appCatID = liteAppCat.getApplianceCategoryID();
										progIDs[i] = progID;
										break;
									}
								}
								if (progID > 0) break;
							}
							if (progID > 0) break;
						}
						
						if (progID == 0)
							throw new Exception( "Cannot find LM program for load group id = " + groupID );
						
						programs.add( new int[] {progID, appCatID, groupID} );
					}
					
					if (programs.size() > 0) {
						programSignUp( programs, liteAcctInfo, new Integer(liteInv.getInventoryID()), energyCompany, conn );
						
						for (int i = 0; i < 3; i++) {
							if (fields[IDX_R1_STATUS + i].equals("1"))
								logMsg.add("Receiver (import_inv_id=" + fields[IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") relay " + (i+1) + " is out of service");
							else if (fields[IDX_R1_STATUS + i].equals("2"))
								logMsg.add("Receiver (import_inv_id=" + fields[IDX_INV_ID] + ",db_inv_id=" + liteInv.getInventoryID() + ") relay " + (i+1) + " was out before switch placed out");
						}
						
						int[] appIDs = new int[3];
						for (int i = 0; i < 3; i++) {
							if (progIDs[i] == 0) continue;
							
							for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
								LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
								if (liteApp.getLmProgramID() == progIDs[i]) {
									appIDs[i] = liteApp.getApplianceID();
									break;
								}
							}
							
							if (appIDs[i] == 0)	// shouldn't happen
								throw new Exception("Cannot find appliance with RelayNum = " + (i+1));
						}
						
						appIDMap.put( new Integer(fields[IDX_INV_ID]), appIDs );
						numAppAdded += programs.size();
					}
				}
				
				numInvAdded++;
				if (fields[IDX_DEVICE_NAME].equals(""))
					numRecvrAdded++;
				else
					numMeterAdded++;
			}
			
			for (lineNo = 1; lineNo <= appFieldsList.size(); lineNo++) {
				String[] fields = (String[]) appFieldsList.get(lineNo - 1);
				
				Integer invID = Integer.valueOf( fields[IDX_INV_ID] );
				int relayNum = Integer.parseInt( fields[IDX_RELAY_NUM] );
				int appID = 0;
				
				int[] appIDs = (int[]) appIDMap.get( invID );
				if (appIDs != null) appID = appIDs[relayNum - 1];
				
				if (appID == 0)
					throw new Exception("Cannot find appliance with InventoryID = " + invID + " and RelayNum = " + relayNum);
				
				Integer acctID = Integer.valueOf( fields[IDX_ACCOUNT_ID] );
				LiteStarsCustAccountInformation liteAcctInfo = null;
				
				Object obj = acctIDMap.get(acctID);
				if (obj != null) {
					if (obj instanceof LiteStarsCustAccountInformation) {
						liteAcctInfo = (LiteStarsCustAccountInformation) obj;
					}
					else if (obj instanceof Integer) {
						// This is loaded from file customer.map
						liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
						if (liteAcctInfo != null)
							acctIDMap.put(acctID, liteAcctInfo);
					}
				}
				
				if (liteAcctInfo == null)
					throw new Exception("Cannot find customer account with id=" + acctID.intValue());
				
				updateAppliance(fields, appID, liteAcctInfo, energyCompany);
				numAppUpdated++;
				
				if (fields[IDX_APP_CAT_DEF_ID].equals("")) continue;
				int catDefID = Integer.parseInt( fields[IDX_APP_CAT_DEF_ID] );
				
				if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_AIR_CONDITIONER)
					numACAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_WATER_HEATER)
					numWHAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GENERATOR)
					numGenAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_IRRIGATION)
					numIrrAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_GRAIN_DRYER)
					numGDryAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_HEAT_PUMP)
					numHPAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_STORAGE_HEAT)
					numSHAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DUAL_FUEL)
					numDFAdded++;
				else if (catDefID == YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT)
					numGenlAdded++;
			}
			
			for (lineNo = 1; lineNo <= orderFieldsList.size(); lineNo++) {
				String[] fields = (String[]) orderFieldsList.get(lineNo - 1);
				Integer acctID = Integer.valueOf( fields[IDX_ACCOUNT_ID] );
				
				LiteStarsCustAccountInformation liteAcctInfo = null;
				
				if (acctID.intValue() > 0) {
					Object obj = acctIDMap.get(acctID);
					if (obj instanceof LiteStarsCustAccountInformation) {
						liteAcctInfo = (LiteStarsCustAccountInformation) obj;
					}
					else if (obj instanceof Integer) {
						// This is loaded from file customer.map
						liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
						if (liteAcctInfo != null)
							acctIDMap.put(acctID, liteAcctInfo);
					}
					
					if (liteAcctInfo == null)
						throw new Exception("Cannot find customer account with id=" + acctID.intValue());
				}
				
				newServiceRequest( fields, liteAcctInfo, energyCompany );
				numOrderAdded++;
			}
			
			for (lineNo = 1; lineNo <= resFieldsList.size(); lineNo++) {
				String[] fields = (String[]) resFieldsList.get(lineNo - 1);
				Integer acctID = Integer.valueOf( fields[IDX_ACCOUNT_ID] );
				
				LiteStarsCustAccountInformation liteAcctInfo = null;
				
				if (acctID.intValue() > 0) {
					Object obj = acctIDMap.get(acctID);
					if (obj instanceof LiteStarsCustAccountInformation) {
						liteAcctInfo = (LiteStarsCustAccountInformation) obj;
					}
					else if (obj instanceof Integer) {
						// This is loaded from file customer.map
						liteAcctInfo = energyCompany.getCustAccountInformation( ((Integer)obj).intValue(), true );
						if (liteAcctInfo != null)
							acctIDMap.put(acctID, liteAcctInfo);
					}
					
					if (liteAcctInfo == null)
						throw new Exception("Cannot find customer account with id=" + acctID.intValue());
				}
				
				newResidenceInfo( fields, liteAcctInfo );
				numResAdded++;
			}
			
			//session.removeAttribute( PREPROCESSED_STARS_DATA );
			//session.removeAttribute( UNASSIGNED_LISTS );
		}
		catch (Exception e) {
			e.printStackTrace();
			
			if (lineNo == 0) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to import old STARS data");
				errorMsg = e.getMessage();
			}
			else {
				if (numAcctAdded < acctFieldsList.size())
					errorMsg = "Error encountered when processing customer file line #" + lineNo;
				else if (numInvAdded < invFieldsList.size())
					errorMsg = "Error encountered when processing inventory file line #" + lineNo;
				else if (numAppUpdated < appFieldsList.size())
					errorMsg = "Error encountered when processing loadinfo file line #" + lineNo;
				else if (numOrderAdded < orderFieldsList.size())
					errorMsg = "Error encountered when processing workorder file line #" + lineNo;
				else if (numResAdded < resFieldsList.size())
					errorMsg = "Error encountered when processing resinfo file line #" + lineNo;
				if (errorMsg != null)
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, errorMsg);
			}
		}
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
		
		// Directory where the map and log files will be written into
		String path = (String) preprocessedData.get( "CustomerFilePath" );
		
		if (numAcctAdded > 0) {
			// New accounts added, regenerate the 'customer.map' file
			ArrayList lines = new ArrayList();
			
			Iterator it = acctIDMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				
				Integer acctID = (Integer) entry.getKey();
				int acctID2 = -1;
				if (entry.getValue() instanceof LiteStarsCustAccountInformation)
					acctID2 = ((LiteStarsCustAccountInformation) entry.getValue()).getAccountID();
				else if (entry.getValue() instanceof Integer)
					acctID2 = ((Integer) entry.getValue()).intValue();
				
				String line = acctID.toString() + "," + String.valueOf(acctID2);
				lines.add( line );
			}
			
			try {
				File custMapFile = new File(path, "customer.map");
				writeFile(custMapFile, lines);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (numAppAdded > 0) {
			// New appliances added, regenerate the 'hwconfig.map' file
			ArrayList lines = new ArrayList();
			
			Iterator it = appIDMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer invID = (Integer) entry.getKey();
				int[] appIDs = (int[]) entry.getValue();
				
				String line = invID.toString()
						+ "," + appIDs[0]
						+ "," + appIDs[1]
						+ "," + appIDs[2];
				lines.add( line );
			}
			
			try {
				File hwConfigMapFile = new File(path, "hwconfig.map");
				writeFile(hwConfigMapFile, lines);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		long stopTime = System.currentTimeMillis();
		
		int minutes = (int) ((stopTime - startTime) * 0.001 / 60 + 0.5);
		int hourTaken = minutes / 60;
		int minTaken = minutes % 60;
		String timeTaken = "";
		if (hourTaken > 1)
			timeTaken += hourTaken + " hours ";
		else if (hourTaken == 1)
			timeTaken += hourTaken + " hour ";
		if (minTaken > 1)
			timeTaken += minTaken + " minutes";
		else
			timeTaken += minTaken + " minute";
		
		int idx = 0;
		logMsg.add(idx++, "Start Time: " + ServerUtils.formatDate( new Date(startTime), java.util.TimeZone.getDefault() ));
		logMsg.add(idx++, "Time Taken: " + timeTaken);
		logMsg.add(idx++, "");
		
		if (errorMsg != null) {
			logMsg.add(idx++, errorMsg);
			logMsg.add(idx++, "");
		}
		
		if (numAcctAdded > 0)
			logMsg.add(idx++, numAcctAdded + " customer accounts added");
		if (numInvAdded > 0)
			logMsg.add(idx++, numInvAdded + " hardwares added (" + numRecvrAdded + " receivers, " + numMeterAdded + " meters)");
		if (numAppAdded > 0)
			logMsg.add(idx++, numAppAdded + " appliances automatically added");
		if (numAppUpdated > 0)
			logMsg.add(idx++, numAppUpdated + " appliances updated (" +
					numACAdded + " ac, " +
					numWHAdded + " wh, " +
					numGenAdded + " gen, " +
					numIrrAdded + " irr, " +
					numGDryAdded + " gdry, " +
					numHPAdded + " hp, " +
					numSHAdded + " sh, " +
					numDFAdded + " df, " +
					numGenlAdded + " genl)"
					);
		if (numOrderAdded > 0)
			logMsg.add(idx++, numOrderAdded + " work orders added");
		if (numResAdded > 0)
			logMsg.add(idx++, numResAdded + " residence info added");
		logMsg.add(idx++, "");
		
		Date logDate = new Date();
		String logFileName = "import_" + starsDateFormat.format(logDate) + "_" + starsTimeFormat.format(logDate) + ".log";
		File logFile = new File(path, logFileName);
		
		try {
			writeFile(logFile, logMsg);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		String confirmMsg = "";
		if (errorMsg == null)
			confirmMsg += "Old STARS data imported successfully. ";
		if (logFile != null)
			confirmMsg += "For detailed information, view the log file '" + logFile.getPath() + "'.";
		if (confirmMsg.length() > 0)
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, confirmMsg);
	}
	
	private void updateAddress(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerAddress starsAddr = null;
			
			int addressID = Integer.parseInt( req.getParameter("AddressID") );
			boolean newAddress = (addressID <= 0);
			String referer = req.getParameter( "REFERER" );
			
			if (referer.equalsIgnoreCase("EnergyCompany.jsp")) {
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				starsAddr = ecTemp.getCompanyAddress();
			}
			else if (referer.startsWith("ServiceCompany.jsp")) {
				StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( SERVICE_COMPANY_TEMP );
				starsAddr = scTemp.getCompanyAddress();
			}
			
			if (!newAddress) {
				LiteAddress liteAddr = energyCompany.getAddress( starsAddr.getAddressID() );
	        	com.cannontech.database.db.customer.Address addr =
	        			(com.cannontech.database.db.customer.Address) StarsLiteFactory.createDBPersistent( liteAddr );
	        	addr.setLocationAddress1( req.getParameter("StreetAddr1") );
	        	addr.setLocationAddress2( req.getParameter("StreetAddr2") );
	        	addr.setCityName( req.getParameter("City") );
	        	addr.setStateCode( req.getParameter("State") );
	        	addr.setZipCode( req.getParameter("Zip") );
	        	addr.setCounty( req.getParameter("County") );
	        	
	        	addr = (com.cannontech.database.db.customer.Address)
	        			Transaction.createTransaction( Transaction.UPDATE, addr ).execute();
	        	StarsLiteFactory.setLiteAddress( liteAddr, addr );
	        	StarsLiteFactory.setStarsCustomerAddress( starsAddr, liteAddr );
	        	
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information updated successfully");
			}
			else {
				starsAddr.setStreetAddr1( req.getParameter("StreetAddr1") );
				starsAddr.setStreetAddr2( req.getParameter("StreetAddr2") );
				starsAddr.setCity( req.getParameter("City") );
				starsAddr.setState( req.getParameter("State") );
				starsAddr.setZip( req.getParameter("Zip") );
				starsAddr.setCounty( req.getParameter("County") );
				
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Address information created, you must submit this page to finally save it");
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update address information");
			redirect = referer;
		}
	}
	
	private void updateEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnergyCompany ec = ecSettings.getStarsEnergyCompany();
        	
			// Create the data object from the request parameters
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			
			boolean newContact = (energyCompany.getPrimaryContactID() == CtiUtilities.NONE_ID);
			LiteContact liteContact = null;
			
			if (newContact) {
				contact.getContact().setContLastName( CtiUtilities.STRING_NONE );
				contact.getContact().setContFirstName( CtiUtilities.STRING_NONE );
			}
			else {
				liteContact = ContactFuncs.getContact( energyCompany.getPrimaryContactID() );
				StarsLiteFactory.setContact( contact, liteContact );
			}
			
			com.cannontech.database.db.contact.ContactNotification notifPhone = null;
			com.cannontech.database.db.contact.ContactNotification notifFax = null;
			com.cannontech.database.db.contact.ContactNotification notifEmail = null;
			
			for (int i = 0; i < contact.getContactNotifVect().size(); i++) {
				com.cannontech.database.db.contact.ContactNotification notif =
						(com.cannontech.database.db.contact.ContactNotification) contact.getContactNotifVect().get(i);
				// Set all the opcode to DELETE first, then change it to UPDATE or add INSERT accordingly
				notif.setOpCode( Transaction.DELETE );
				
				if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_PHONE)
					notifPhone = notif;
				else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_FAX)
					notifFax = notif;
				else if (notif.getNotificationCatID().intValue() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL)
					notifEmail = notif;
			}
			
			if (req.getParameter("PhoneNo").length() > 0) {
				if (notifPhone != null) {
					notifPhone.setNotification( req.getParameter("PhoneNo") );
					notifPhone.setOpCode( Transaction.UPDATE );
				}
				else {
					notifPhone = new com.cannontech.database.db.contact.ContactNotification();
					notifPhone.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_PHONE) );
					notifPhone.setDisableFlag( "Y" );
					notifPhone.setNotification( req.getParameter("PhoneNo") );
					notifPhone.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifPhone );
				}
			}
			
			if (req.getParameter("FaxNo").length() > 0) {
				if (notifFax != null) {
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.UPDATE );
				}
				else {
					notifFax = new com.cannontech.database.db.contact.ContactNotification();
					notifFax.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_FAX) );
					notifFax.setDisableFlag( "Y" );
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifFax );
				}
			}
			
			if (req.getParameter("FaxNo").length() > 0) {
				if (notifFax != null) {
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.UPDATE );
				}
				else {
					notifFax = new com.cannontech.database.db.contact.ContactNotification();
					notifFax.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_FAX) );
					notifFax.setDisableFlag( "Y" );
					notifFax.setNotification( req.getParameter("FaxNo") );
					notifFax.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifFax );
				}
			}
			
			boolean customizedEmail = Boolean.valueOf( req.getParameter("CustomizedEmail") ).booleanValue();
			String email = req.getParameter("Email");
			
			if (!customizedEmail && email.length() > 0) {
				if (notifEmail != null) {
					notifEmail.setNotification( email );
					notifEmail.setOpCode( Transaction.UPDATE );
				}
				else {
					notifEmail = new com.cannontech.database.db.contact.ContactNotification();
					notifEmail.setNotificationCatID( new Integer(YukonListEntryTypes.YUK_ENTRY_ID_EMAIL) );
					notifEmail.setDisableFlag( "Y" );
					notifEmail.setNotification( email );
					notifEmail.setOpCode( Transaction.INSERT );
					
					contact.getContactNotifVect().add( notifEmail );
				}
			}
			
			if (newContact) {
				com.cannontech.database.db.customer.Address addr =
						new com.cannontech.database.db.customer.Address();
				addr.setStateCode( " " );
				StarsEnergyCompany ecTemp = (StarsEnergyCompany) session.getAttribute( ENERGY_COMPANY_TEMP );
				if (ecTemp != null)
					StarsFactory.setCustomerAddress( addr, ecTemp.getCompanyAddress() );
				contact.setAddress( addr );
				
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.INSERT, contact ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
				energyCompany.addContact( liteContact, null );
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(contact.getContact().getAddressID().intValue()) );
				ec.setCompanyAddress( starsAddr );
			}
			else {
				contact = (com.cannontech.database.data.customer.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contact ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
			}
			
			LiteContactNotification liteNotifPhone = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_PHONE );
			ec.setMainPhoneNumber( ServerUtils.getNotification(liteNotifPhone) );
			
			LiteContactNotification liteNotifFax = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_FAX );
			ec.setMainFaxNumber( ServerUtils.getNotification(liteNotifFax) );
			
			LiteContactNotification liteNotifEmail = ContactFuncs.getContactNotification( liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
			ec.setEmail( ServerUtils.getNotification(liteNotifEmail) );
			
			String compName = req.getParameter("CompanyName");
			if (newContact || !energyCompany.getName().equals( compName )) {
				energyCompany.setName( compName );
				energyCompany.setPrimaryContactID( contact.getContact().getContactID().intValue() );
				Transaction.createTransaction( Transaction.UPDATE, StarsLiteFactory.createDBPersistent(energyCompany) ).execute();
				
				ec.setCompanyName( compName );
			}
			
	        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	        
	        // Update energy company role DEFAULT_TIME_ZONE if necessary
	        {
		        LiteYukonUser liteUser = YukonUserFuncs.getLiteYukonUser( energyCompany.getUserID() );
		        java.util.Map rolePropertyIDMap = (java.util.Map) cache.getYukonUserRolePropertyIDLookupMap().get( liteUser );
		        Pair rolePropertyPair = (Pair) rolePropertyIDMap.get( new Integer(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		        String value = (String) rolePropertyPair.getSecond();
		        
		        String timeZone = req.getParameter("TimeZone");
		        if (!value.equalsIgnoreCase( timeZone )) {
		        	String sql = "UPDATE YukonUserRole SET Value = '" + timeZone + "'" +
		        			" WHERE UserID = " + liteUser.getUserID() +
		        			" AND RoleID = " + EnergyCompanyRole.ROLEID +
		        			" AND RolePropertyID = " + EnergyCompanyRole.DEFAULT_TIME_ZONE;
		        	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
		        			sql, CtiUtilities.getDatabaseAlias() );
		        	stmt.execute();
		        	ServerUtils.handleDBChange( liteUser, com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_TYPE_UPDATE );
		        	
		        	ec.setTimeZone( timeZone );
		        }
	        }
			
			// Update ResidentialCustomer role property CUSTOMIZED_UTIL_EMAIL_LINK if necessary
			{
		        LiteYukonGroup liteGroup = energyCompany.getResidentialCustomerGroup();
		        if (liteGroup != null) {
		        	String value = AuthFuncs.getRolePropValueGroup(
		        			liteGroup, ResidentialCustomerRole.WEB_LINK_UTIL_EMAIL, "(none)" );
		        	
		        	if (customizedEmail && !value.equals(email) ||
		        		!customizedEmail && ServerUtils.forceNotNone(value).length() > 0)
		        	{
			        	if (!customizedEmail) email = "(none)";
			        	String sql = "UPDATE YukonGroupRole SET Value = '" + email + "'" +
			        			" WHERE GroupID = " + liteGroup.getGroupID() +
			        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
			        			" AND RolePropertyID = " + ResidentialCustomerRole.WEB_LINK_UTIL_EMAIL;
			        	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
			        			sql, CtiUtilities.getDatabaseAlias() );
			        	stmt.execute();
			        	
			        	ServerUtils.handleDBChange( liteGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
		        	}
		        }
			}
        	
        	session.removeAttribute( ENERGY_COMPANY_TEMP );
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company information updated successfully");
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update energy company information");
			redirect = referer;
		}
	}
	
	private void updateApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms starsAppCats = ecSettings.getStarsEnrollmentPrograms();
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			int appCatID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean newAppCat = (appCatID == -1);
			
			com.cannontech.database.db.web.YukonWebConfiguration config =
					new com.cannontech.database.db.web.YukonWebConfiguration();
			config.setLogoLocation( req.getParameter("IconName") );
			if (Boolean.valueOf( req.getParameter("SameAsName") ).booleanValue())
				config.setAlternateDisplayName( req.getParameter("Name") );
			else
				config.setAlternateDisplayName( req.getParameter("DispName") );
			config.setDescription( req.getParameter("Description").replaceAll(LINE_SEPARATOR, "<br>") );
			config.setURL( "" );
			
			com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
					new com.cannontech.database.data.stars.appliance.ApplianceCategory();
			com.cannontech.database.db.stars.appliance.ApplianceCategory appCatDB = appCat.getApplianceCategory();
			appCatDB.setCategoryID( Integer.valueOf(req.getParameter("Category")) );
			appCatDB.setDescription( req.getParameter("Name") );
			appCat.setWebConfiguration( config );
			
			LiteApplianceCategory liteAppCat = null;
			if (newAppCat) {
				appCat.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				appCat.setDbConnection( conn );
				appCat.add();
				
				liteAppCat = (LiteApplianceCategory) StarsLiteFactory.createLite( appCat.getApplianceCategory() );
				energyCompany.addApplianceCategory( liteAppCat );
				LiteWebConfiguration liteConfig = (LiteWebConfiguration) StarsLiteFactory.createLite( appCat.getWebConfiguration() );
				SOAPServer.addWebConfiguration( liteConfig );
			}
			else {
				liteAppCat = energyCompany.getApplianceCategory( appCatID );
				appCat.setApplianceCategoryID( new Integer(appCatID) );
				appCatDB.setWebConfigurationID( new Integer(liteAppCat.getWebConfigurationID()) );
				appCat.setDbConnection( conn );
				appCat.update();
				
				StarsLiteFactory.setLiteApplianceCategory( liteAppCat, appCat.getApplianceCategory() );
				LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( appCat.getWebConfiguration().getConfigurationID().intValue() );
				StarsLiteFactory.setLiteWebConfiguration( liteConfig, appCat.getWebConfiguration() );
				energyCompany.updateStarsWebConfig( liteConfig.getConfigID() );
			}
			
			Integer applianceCategoryID = appCat.getApplianceCategory().getApplianceCategoryID();
			
			ArrayList pubProgList = new ArrayList();
			if (liteAppCat.getPublishedPrograms() != null) {
				for (int i = 0; i < liteAppCat.getPublishedPrograms().length; i++)
					pubProgList.add( liteAppCat.getPublishedPrograms()[i] );
			}
			
			String[] progIDs = req.getParameterValues( "ProgIDs" );
			String[] progDispNames = req.getParameterValues( "ProgDispNames" );
			String[] progShortNames = req.getParameterValues( "ProgShortNames" );
			String[] progDescriptions = req.getParameterValues( "ProgDescriptions" );
			String[] progCtrlOdds = req.getParameterValues( "ProgChanceOfCtrls" );
			String[] progIconNames = req.getParameterValues( "ProgIconNames" );
			
			if (progIDs != null) {
				LiteLMProgram[] pubPrograms = new LiteLMProgram[ progIDs.length ];
				for (int i = 0; i < progIDs.length; i++) {
					int progID = Integer.parseInt( progIDs[i] );
					
					for (int j = 0; j < pubProgList.size(); j++) {
						LiteLMProgram liteProg = (LiteLMProgram) pubProgList.get(j);
						if (liteProg.getProgramID() == progID) {
							pubProgList.remove(j);
							pubPrograms[i] = liteProg;
							break;
						}
					}
					
					com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
							new com.cannontech.database.data.stars.LMProgramWebPublishing();
					com.cannontech.database.db.stars.LMProgramWebPublishing pubProgDB = pubProg.getLMProgramWebPublishing();
					pubProgDB.setApplianceCategoryID( applianceCategoryID );
					pubProgDB.setLMProgramID( new Integer(progID) );
					pubProgDB.setChanceOfControlID( Integer.valueOf(progCtrlOdds[i]) );
					
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setLogoLocation( config.getLogoLocation() + "," + progIconNames[i] );
					cfg.setAlternateDisplayName( progDispNames[i] + "," + progShortNames[i] );
					cfg.setDescription( progDescriptions[i].replaceAll(LINE_SEPARATOR, "<br>") );
					pubProg.setWebConfiguration( cfg );
					
					if (pubPrograms[i] != null) {
						pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(pubPrograms[i].getWebSettingsID()) );
						pubProg.setDbConnection( conn );
						pubProg.update();
						
						pubPrograms[i].setChanceOfControlID( pubProg.getLMProgramWebPublishing().getChanceOfControlID().intValue() );
						
						// If program display name changed, we need to update all the accounts with this program
						LiteWebConfiguration liteCfg = SOAPServer.getWebConfiguration( pubPrograms[i].getWebSettingsID() );
						
						String[] fields = ServerUtils.forceNotNull( liteCfg.getAlternateDisplayName() ).split(",");
						String oldDispName = (fields.length > 0)? fields[0] : "";
						if (oldDispName.length() == 0) oldDispName = pubPrograms[i].getProgramName();
						
						String newDispName = progDispNames[i];
						if (newDispName.length() == 0) newDispName = pubPrograms[i].getProgramName();
						
						StarsLiteFactory.setLiteWebConfiguration( liteCfg, pubProg.getWebConfiguration() );
						energyCompany.updateStarsWebConfig( liteCfg.getConfigID() );
						
						if (!newDispName.equals( oldDispName )) {
							int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(new Integer(progID), energyCompany.getEnergyCompanyID());
							
							for (int j = 0; j < accountIDs.length; j++) {
								StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( accountIDs[j] );
								if (starsAcctInfo != null) {
									StarsLMPrograms starsProgs = starsAcctInfo.getStarsLMPrograms();
									
									for (int k = 0; k < starsProgs.getStarsLMProgramCount(); k++) {
										if (starsProgs.getStarsLMProgram(k).getProgramID() == progID) {
											starsProgs.getStarsLMProgram(k).setProgramName( newDispName );
											break;
										}
									}
								}
							}
						}
					}
					else {
						pubProg = (com.cannontech.database.data.stars.LMProgramWebPublishing)
								Transaction.createTransaction( Transaction.INSERT, pubProg ).execute();
						pubPrograms[i] = (LiteLMProgram) StarsLiteFactory.createLite( pubProg.getLMProgramWebPublishing() );
						energyCompany.addLMProgram( pubPrograms[i] );
						
						LiteWebConfiguration liteCfg = (LiteWebConfiguration) StarsLiteFactory.createLite( pubProg.getWebConfiguration() );
						SOAPServer.addWebConfiguration( liteCfg );
					}
				}
				
				liteAppCat.setPublishedPrograms( pubPrograms );
			}
			else
				liteAppCat.setPublishedPrograms( new LiteLMProgram[0] );
			
			// Delete the rest of published programs
			for (int i = 0; i < pubProgList.size(); i++) {
				LiteLMProgram liteProg = (LiteLMProgram) pubProgList.get(i);
				Integer programID = new Integer( liteProg.getProgramID() );
				
				// Delete all events of this program
				com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
						energyCompany.getEnergyCompanyID(), programID, conn );
				
				// Unenroll the program from all customers currently enrolled in it
				int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithProgram(
						programID, energyCompany.getEnergyCompanyID() );
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				com.cannontech.database.db.stars.appliance.ApplianceBase appDB = app.getApplianceBase();
				
				for (int j = 0; j < accountIDs.length; j++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], true );
					
					for (int k = 0; k < liteAcctInfo.getAppliances().size(); k++) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(k);
						if (liteApp.getLmProgramID() != liteProg.getProgramID()) continue;
						
						StarsLiteFactory.setApplianceBase( app, liteApp );
						appDB.setLMProgramID( new Integer(CtiUtilities.NONE_ID) );
						appDB.setDbConnection( conn );
						appDB.update();
						
						liteApp.setLmProgramID( CtiUtilities.NONE_ID );
					}
					
					ArrayList programs = liteAcctInfo.getLmPrograms();
					for (int k = 0; k < programs.size(); k++) {
						if (((LiteStarsLMProgram) programs.get(k)).getLmProgram().getProgramID() == liteProg.getProgramID()) {
							programs.remove(k);
							break;
						}
					}
					
					Iterator it = liteAcctInfo.getProgramHistory().iterator();
					while (it.hasNext()) {
						if (((LiteLMProgramEvent) it.next()).getProgramID() == liteProg.getProgramID())
							it.remove();
					}
					
					energyCompany.updateStarsCustAccountInformation( liteAcctInfo );
				}
				
				com.cannontech.database.data.stars.LMProgramWebPublishing pubProg =
						new com.cannontech.database.data.stars.LMProgramWebPublishing();
				pubProg.setApplianceCategoryID( applianceCategoryID );
				pubProg.setLMProgramID( programID );
				pubProg.getLMProgramWebPublishing().setWebSettingsID( new Integer(liteProg.getWebSettingsID()) );
				pubProg.setDbConnection( conn );
				pubProg.delete();
			
				energyCompany.deleteLMProgram( liteProg.getProgramID() );
				SOAPServer.deleteWebConfiguration( liteProg.getWebSettingsID() );
				energyCompany.deleteStarsWebConfig( liteProg.getWebSettingsID() );
			}
			
			StarsApplianceCategory starsAppCat = StarsLiteFactory.createStarsApplianceCategory( liteAppCat, energyCompany );
			if (newAppCat) {
				starsAppCats.addStarsApplianceCategory( starsAppCat );
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category is created successfully");
			}
			else {
				for (int i = 0; i < starsAppCats.getStarsApplianceCategoryCount(); i++) {
					if (starsAppCats.getStarsApplianceCategory(i).getApplianceCategoryID() == appCatID) {
						starsAppCats.setStarsApplianceCategory( i, starsAppCat );
						break;
					}
				}
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category information updated successfully");
			}
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update appliance category information");
			redirect = referer;
        }
		finally {
			try {
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	private void deleteApplianceCategory(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsEnrollmentPrograms starsAppCats = ecSettings.getStarsEnrollmentPrograms();
			
			int applianceCategoryID = Integer.parseInt( req.getParameter("AppCatID") );
			boolean deleteAll = (applianceCategoryID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int i = starsAppCats.getStarsApplianceCategoryCount() - 1; i >= 0; i--) {
				StarsApplianceCategory starsAppCat = starsAppCats.getStarsApplianceCategory(i);
				if (!deleteAll && starsAppCat.getApplianceCategoryID() != applianceCategoryID) continue;
				
				Integer appCatID = new Integer( starsAppCat.getApplianceCategoryID() );
				LiteApplianceCategory liteAppCat = energyCompany.getApplianceCategory( appCatID.intValue() );
				
				com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing( appCatID, conn );
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					int configID = liteAppCat.getPublishedPrograms()[j].getWebSettingsID();
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(configID) );
					cfg.setDbConnection( conn );
					cfg.delete();
					
					SOAPServer.deleteWebConfiguration( configID );
					energyCompany.deleteStarsWebConfig( configID );
				}
				
				int[] programIDs = new int[ liteAppCat.getPublishedPrograms().length ];
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					programIDs[j] = liteAppCat.getPublishedPrograms()[j].getProgramID();
					
					// Delete all program events
					com.cannontech.database.data.stars.event.LMProgramEvent.deleteAllLMProgramEvents(
							energyCompany.getEnergyCompanyID(), new Integer(programIDs[j]), conn );
				}
				Arrays.sort( programIDs );
				
				int[] accountIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllAccountIDsWithCategory( appCatID );
				int[] applianceIDs = com.cannontech.database.db.stars.appliance.ApplianceBase.getAllApplianceIDsWithCategory( appCatID );
				
				com.cannontech.database.data.stars.appliance.ApplianceBase app =
						new com.cannontech.database.data.stars.appliance.ApplianceBase();
				for (int j = 0; j < applianceIDs.length; j++) {
					app.setApplianceID( new Integer(applianceIDs[j]) );
					app.setDbConnection( conn );
					app.delete();
				}
				
				com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
						new com.cannontech.database.data.stars.appliance.ApplianceCategory();
				StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
				appCat.setDbConnection( conn );
				appCat.delete();
				
				SOAPServer.deleteWebConfiguration( liteAppCat.getWebConfigurationID() );
				energyCompany.deleteStarsWebConfig( liteAppCat.getWebConfigurationID() );
				
				for (int j = 0; j < accountIDs.length; j++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[j], true );
					
					Iterator appIt = liteAcctInfo.getAppliances().iterator();
					while (appIt.hasNext()) {
						LiteStarsAppliance liteApp = (LiteStarsAppliance) appIt.next();
						if (liteApp.getApplianceCategoryID() == appCatID.intValue()) {
							appIt.remove();
							
							Iterator progIt = liteAcctInfo.getLmPrograms().iterator();
							while (progIt.hasNext()) {
								LiteStarsLMProgram liteProg = (LiteStarsLMProgram) progIt.next();
								if (liteProg.getLmProgram().getProgramID() == liteApp.getLmProgramID())
									progIt.remove();
							}
						}
					}
					
					Iterator it = liteAcctInfo.getProgramHistory().iterator();
					while (it.hasNext()) {
						int progID = ((LiteLMProgramEvent) it.next()).getProgramID();
						if (Arrays.binarySearch(programIDs, progID) >= 0)
							it.remove();
					}
					
					energyCompany.updateStarsCustAccountInformation( liteAcctInfo );
				}
				
				energyCompany.deleteApplianceCategory( appCatID.intValue() );
				starsAppCats.removeStarsApplianceCategory( i );
			}
			
			if (deleteAll)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance categories have been deleted successfully");
			else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Appliance category has been deleted successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete appliance category");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}

	private void updateServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsServiceCompanies starsCompanies = ecSettings.getStarsServiceCompanies();
			
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			boolean newCompany = (companyID == -1);
			
			com.cannontech.database.data.stars.report.ServiceCompany company =
					new com.cannontech.database.data.stars.report.ServiceCompany();
			com.cannontech.database.db.stars.report.ServiceCompany companyDB = company.getServiceCompany();
			
			com.cannontech.database.data.customer.Contact contact =
					new com.cannontech.database.data.customer.Contact();
			com.cannontech.database.db.contact.Contact contactDB = contact.getContact();
			
			LiteServiceCompany liteCompany = null;
			LiteContact liteContact = null;
			LiteAddress liteAddr = null;
			
			StarsServiceCompany starsCompany = null;
			
			if (!newCompany) {
				liteCompany = energyCompany.getServiceCompany( companyID );
				StarsLiteFactory.setServiceCompany( companyDB, liteCompany );
				liteContact = ContactFuncs.getContact( liteCompany.getPrimaryContactID() );
				StarsLiteFactory.setContact( contact, liteContact );
				liteAddr = energyCompany.getAddress( liteCompany.getAddressID() );
        	}
        	
			companyDB.setCompanyName( req.getParameter("CompanyName") );
			companyDB.setMainPhoneNumber( ServletUtils.formatPhoneNumber(req.getParameter("PhoneNo")) );
			companyDB.setMainFaxNumber( ServletUtils.formatPhoneNumber(req.getParameter("FaxNo")) );
			companyDB.setHIType( req.getParameter("Type") );
			contactDB.setContLastName( req.getParameter("ContactLastName") );
			contactDB.setContFirstName( req.getParameter("ContactFirstName") );
			
			if (newCompany) {
				com.cannontech.database.db.customer.Address address = new com.cannontech.database.db.customer.Address();
				StarsServiceCompany scTemp = (StarsServiceCompany) session.getAttribute( SERVICE_COMPANY_TEMP );
				if (scTemp != null)
					StarsFactory.setCustomerAddress( address, scTemp.getCompanyAddress() );
				
				company.setAddress( address );
				company.setPrimaryContact( contactDB );
				company.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				company = (com.cannontech.database.data.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.INSERT, company ).execute();
				
				liteAddr = (LiteAddress) StarsLiteFactory.createLite( company.getAddress() );
				energyCompany.addAddress( liteAddr );
				
				liteContact = (LiteContact) StarsLiteFactory.createLite( contact );
				energyCompany.addContact( liteContact, null );
				
				liteCompany = (LiteServiceCompany) StarsLiteFactory.createLite( company.getServiceCompany() );
				energyCompany.addServiceCompany( liteCompany );
				
				starsCompany = new StarsServiceCompany();
				starsCompany.setCompanyID( liteCompany.getCompanyID() );
				starsCompanies.addStarsServiceCompany( starsCompany );
				
				PrimaryContact starsContact = new PrimaryContact();
				StarsLiteFactory.setStarsCustomerContact(
						starsContact, ContactFuncs.getContact(company.getPrimaryContact().getContactID().intValue()) );
				starsCompany.setPrimaryContact( starsContact );
				
				CompanyAddress starsAddr = new CompanyAddress();
				StarsLiteFactory.setStarsCustomerAddress(
						starsAddr, energyCompany.getAddress(company.getAddress().getAddressID().intValue()) );
				starsCompany.setCompanyAddress( starsAddr );
			}
			else {
				contactDB = (com.cannontech.database.db.contact.Contact)
						Transaction.createTransaction( Transaction.UPDATE, contactDB ).execute();
				StarsLiteFactory.setLiteContact( liteContact, contact );
				
				companyDB = (com.cannontech.database.db.stars.report.ServiceCompany)
						Transaction.createTransaction( Transaction.UPDATE, companyDB ).execute();
				StarsLiteFactory.setLiteServiceCompany( liteCompany, companyDB );
				
				for (int i = 0; i < starsCompanies.getStarsServiceCompanyCount(); i++) {
					if (starsCompanies.getStarsServiceCompany(i).getCompanyID() == companyID) {
						starsCompany = starsCompanies.getStarsServiceCompany(i);
						break;
					}
				}
				if (starsCompany == null)
					throw new Exception ("Cannot find the StarsServiceCompany object with companyID = " + companyID);
				
				starsCompany.getPrimaryContact().setLastName( liteContact.getContLastName() );
				starsCompany.getPrimaryContact().setFirstName( liteContact.getContFirstName() );
			}
			
			starsCompany.setCompanyName( liteCompany.getCompanyName() );
			starsCompany.setMainPhoneNumber( liteCompany.getMainPhoneNumber() );
			starsCompany.setMainFaxNumber( liteCompany.getMainFaxNumber() );
        	
        	session.removeAttribute( SERVICE_COMPANY_TEMP );
        	if (newCompany)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company created successfully");
        	else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company information updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update service company information");
			redirect = referer;
        }
	}
	
	private void deleteServiceCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsServiceCompanies starsCompanies = ecSettings.getStarsServiceCompanies();
			
			int companyID = Integer.parseInt( req.getParameter("CompanyID") );
			boolean deleteAll = (companyID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			for (int i = starsCompanies.getStarsServiceCompanyCount() - 1; i >= 0; i--) {
				StarsServiceCompany starsCompany = starsCompanies.getStarsServiceCompany(i);
				if (!deleteAll && starsCompany.getCompanyID() != companyID) continue;
				
				Integer compID = new Integer( starsCompany.getCompanyID() );
				LiteServiceCompany liteCompany = energyCompany.getServiceCompany( compID.intValue() );
		    	
		    	// set InstallationCompanyID = 0 for all inventory assigned to this service company
		    	ArrayList inventory = energyCompany.getAllInventory();
		    	
		    	for (int j = 0; j < inventory.size(); j++) {
					LiteInventoryBase liteInv = (LiteInventoryBase) inventory.get(j);
					
		    		if (liteInv.getInstallationCompanyID() == starsCompany.getCompanyID()) {
		    			com.cannontech.database.db.stars.hardware.InventoryBase invDB =
		    					new com.cannontech.database.db.stars.hardware.InventoryBase();
		    			StarsLiteFactory.setInventoryBase( invDB, liteInv );
		    			
		    			invDB.setInstallationCompanyID( new Integer(CtiUtilities.NONE_ID) );
		    			invDB.setDbConnection( conn );
		    			invDB.update();
		    			
		    			liteInv.setInstallationCompanyID( CtiUtilities.NONE_ID );
		    		}
		    	}
		    	
		    	// set ServiceCompanyID = 0 for all work orders assigned to this service company
		    	ArrayList orders = energyCompany.getAllWorkOrders();
		    	for (int j = 0; j < orders.size(); j++) {
		    		LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) orders.get(j);
		    		if (liteOrder.getServiceCompanyID() == starsCompany.getCompanyID()) {
		    			com.cannontech.database.db.stars.report.WorkOrderBase order =
		    					(com.cannontech.database.db.stars.report.WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
		    			order.setServiceCompanyID( new Integer(CtiUtilities.NONE_ID) );
		    			order.setDbConnection( conn );
		    			order.update();

		    			liteOrder.setServiceCompanyID( CtiUtilities.NONE_ID );
		    		}
		    	}
				
				com.cannontech.database.data.stars.report.ServiceCompany company =
						new com.cannontech.database.data.stars.report.ServiceCompany();
				StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
				company.setDbConnection( conn );
				company.delete();
				
				energyCompany.deleteAddress( liteCompany.getAddressID() );
				energyCompany.deleteContact( liteCompany.getPrimaryContactID() );
				energyCompany.deleteServiceCompany( compID.intValue() );
				
				starsCompanies.removeStarsServiceCompany( i );
			}
			
			if (deleteAll)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
	private void updateCustomerFAQLink(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        LiteYukonUser liteUser = user.getYukonUser();
        
        try {
        	boolean customizedFAQ = Boolean.valueOf( req.getParameter("CustomizedFAQ") ).booleanValue();
    		String faqLink = req.getParameter("FAQLink");
        	String value = AuthFuncs.getRolePropertyValue( liteUser, ConsumerInfoRole.WEB_LINK_FAQ, "(none)" );
			
        	LiteYukonGroup customerGroup = energyCompany.getResidentialCustomerGroup();
        	LiteYukonGroup operatorGroup = energyCompany.getWebClientOperatorGroup();
        	
        	boolean updateCustGroup = AuthFuncs.getRolePropValueGroup(customerGroup, ResidentialCustomerRole.WEB_LINK_FAQ, null) != null;
        	boolean updateOperGroup = AuthFuncs.getRolePropValueGroup(operatorGroup, ConsumerInfoRole.WEB_LINK_FAQ, null) != null;
        	
    		String sql = null;
    		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    				sql, CtiUtilities.getDatabaseAlias() );
        	
        	if (customizedFAQ && !value.equals(faqLink) ||
        		!customizedFAQ && ServerUtils.forceNotNone(value).length() > 0)
        	{
        		if (!customizedFAQ) faqLink = "(none)";
    			if (updateCustGroup) {
		        	sql = "UPDATE YukonGroupRole SET Value = '" + faqLink + "'" +
		        			" WHERE GroupID = " + customerGroup.getGroupID() +
		        			" AND RoleID = " + ResidentialCustomerRole.ROLEID +
		        			" AND RolePropertyID = " + ResidentialCustomerRole.WEB_LINK_FAQ;
		        	stmt.setSQLString( sql );
		        	stmt.execute();
    			}
	        	
	        	if (updateOperGroup) {
		        	sql = "UPDATE YukonGroupRole SET Value = '" + faqLink + "'" +
		        			" WHERE GroupID = " + operatorGroup.getGroupID() +
		        			" AND RoleID = " + ConsumerInfoRole.ROLEID +
		        			" AND RolePropertyID = " + ConsumerInfoRole.WEB_LINK_FAQ;
	        	}
	        	else {
		        	sql = "UPDATE YukonUserRole SET Value = '" + faqLink + "'" +
		        			" WHERE UserID = " + liteUser.getUserID() +
		        			" AND RoleID = " + ConsumerInfoRole.ROLEID +
		        			" AND RolePropertyID = " + ConsumerInfoRole.WEB_LINK_FAQ;
	        	}
	        	stmt.setSQLString( sql );
        		stmt.execute();
        	}
        	
    		if (updateCustGroup)
	        	ServerUtils.handleDBChange( customerGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
	        if (updateOperGroup)
	        	ServerUtils.handleDBChange( operatorGroup, DBChangeMsg.CHANGE_TYPE_UPDATE );
	        else
	        	ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_UPDATE );

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ link updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ link");
			redirect = referer;
        }
	}
	
	private void updateCustomerFAQSubjects(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			String[] subjectIDs = req.getParameterValues("SubjectIDs");
			
			for (int i = 0; i < subjectIDs.length; i++) {
				int subjectID = Integer.parseInt( subjectIDs[i] );
				YukonListEntry subject = energyCompany.getYukonListEntry( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, subjectID );
				subject.setEntryOrder( i+1 );
				
				com.cannontech.database.db.constants.YukonListEntry entry =
						StarsLiteFactory.createYukonListEntry( subject );
				Transaction.createTransaction( Transaction.UPDATE, entry ).execute();
				
				// Reorder the StarsCustomerFAQGroup objects
				for (int j = i; j < starsFAQs.getStarsCustomerFAQGroupCount(); j++) {
					StarsCustomerFAQGroup group = starsFAQs.getStarsCustomerFAQGroup(j);
					if (group.getSubjectID() == subjectID) {
						starsFAQs.removeStarsCustomerFAQGroup(j);
						starsFAQs.addStarsCustomerFAQGroup(i, group);
						break;
					}
				}
			}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "FAQ subjects updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update FAQ subjects");
			redirect = referer;
        }
	}
	
	private void updateCustomerFAQs(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean newGroup = (subjectID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			String subject = req.getParameter("Subject");
			String[] questions = req.getParameterValues("Questions");
			String[] answers = req.getParameterValues("Answers");
			
			ArrayList liteFAQs = energyCompany.getAllCustomerFAQs();
			StarsCustomerFAQGroup starsGroup = null;
			
			if (newGroup) {
				YukonSelectionList cList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
				
				int nextOrderNo = 1;
				for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
					YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(i);
					if (cEntry.getEntryOrder() >= nextOrderNo)
						nextOrderNo = cEntry.getEntryOrder() + 1;
				}
				
				com.cannontech.database.db.constants.YukonListEntry entry =
						new com.cannontech.database.db.constants.YukonListEntry();
				entry.setListID( new Integer(cList.getListID()) );
				entry.setEntryOrder( new Integer(nextOrderNo) );
				entry.setEntryText( subject );
				entry.setDbConnection( conn );
				entry.add();
				
				com.cannontech.common.constants.YukonListEntry cEntry =
						new com.cannontech.common.constants.YukonListEntry();
				StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
				YukonListFuncs.getYukonListEntries().put( entry.getEntryID(), cEntry );
				cList.getYukonListEntries().add( cEntry );
				
				starsGroup = new StarsCustomerFAQGroup();
				starsGroup.setSubjectID( cEntry.getEntryID() );
				starsGroup.setSubject( cEntry.getEntryText() );
				starsFAQs.addStarsCustomerFAQGroup( starsGroup );
			}
			else {
				synchronized (liteFAQs) {
					Iterator it = liteFAQs.iterator();
					while (it.hasNext()) {
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) it.next();
						if (liteFAQ.getSubjectID() == subjectID) {
							com.cannontech.database.db.stars.CustomerFAQ faq =
									new com.cannontech.database.db.stars.CustomerFAQ();
							faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
							faq.setDbConnection( conn );
							faq.delete();
							
							it.remove();
						}
					}
				}
				
				for (int i = 0; i < starsFAQs.getStarsCustomerFAQGroupCount(); i++) {
					if (starsFAQs.getStarsCustomerFAQGroup(i).getSubjectID() == subjectID) {
						starsGroup = starsFAQs.getStarsCustomerFAQGroup(i);
						starsGroup.removeAllStarsCustomerFAQ();
						break;
					}
				}
				
				YukonListEntry cEntry = energyCompany.getYukonListEntry(
						YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, subjectID );
				if (!cEntry.getEntryText().equals( subject )) {
					com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry( cEntry );
					entry.setEntryText( subject );
					entry.setDbConnection( conn );
					entry.update();
					
					cEntry.setEntryText( subject );
					starsGroup.setSubject( subject );
				}
			}
			
			if (questions != null) {
				for (int i = 0; i < questions.length; i++) {
					com.cannontech.database.db.stars.CustomerFAQ faq =
							new com.cannontech.database.db.stars.CustomerFAQ();
					faq.setSubjectID( new Integer(starsGroup.getSubjectID()) );
					faq.setQuestion( questions[i] );
					faq.setAnswer( answers[i] );
					faq.setDbConnection( conn );
					faq.add();
					
					LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) StarsLiteFactory.createLite( faq );
					synchronized (liteFAQs) { liteFAQs.add(liteFAQ); }
					
					StarsCustomerFAQ starsFAQ = new StarsCustomerFAQ();
					starsFAQ.setQuestionID( liteFAQ.getQuestionID() );
					starsFAQ.setQuestion( liteFAQ.getQuestion() );
					starsFAQ.setAnswer( liteFAQ.getAnswer() );
					starsGroup.addStarsCustomerFAQ( starsFAQ );
				}
			}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer FAQs updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer FAQs");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
	private void deleteFAQSubject(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsCustomerFAQs starsFAQs = ecSettings.getStarsCustomerFAQs();
			
			int subjectID = Integer.parseInt( req.getParameter("SubjectID") );
			boolean deleteAll = (subjectID == -1);
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			ArrayList liteFAQs = energyCompany.getAllCustomerFAQs();
			
			for (int i = starsFAQs.getStarsCustomerFAQGroupCount() - 1; i >= 0; i--) {
				StarsCustomerFAQGroup starsGroup = starsFAQs.getStarsCustomerFAQGroup(i);
				if (!deleteAll && starsGroup.getSubjectID() != subjectID) continue;
				
				synchronized (liteFAQs) {
					Iterator it = liteFAQs.iterator();
					while (it.hasNext()) {
						LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) it.next();
						if (liteFAQ.getSubjectID() == starsGroup.getSubjectID()) {
							com.cannontech.database.db.stars.CustomerFAQ faq =
									new com.cannontech.database.db.stars.CustomerFAQ();
							faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
							faq.setDbConnection( conn );
							faq.delete();
							
							it.remove();
						}
					}
				}
				
				YukonListEntry cEntry = energyCompany.getYukonListEntry(
						YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP, starsGroup.getSubjectID() );
				com.cannontech.database.db.constants.YukonListEntry entry =
						StarsLiteFactory.createYukonListEntry( cEntry );
				entry.setDbConnection( conn );
				entry.delete();
				
				YukonSelectionList cList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_CUSTOMER_FAQ_GROUP );
				cList.getYukonListEntries().remove( cEntry );
				YukonListFuncs.getYukonListEntries().remove( entry.getEntryID() );
				
				starsFAQs.removeStarsCustomerFAQGroup(i);
			}
			
			if (deleteAll)
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service companies have been deleted successfully");
			else
	        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Service company has been deleted successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete service company");
			redirect = referer;
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
	private void updateInterviewQuestions(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			StarsExitInterviewQuestions starsExitQuestions = ecSettings.getStarsExitInterviewQuestions();
			
			String type = req.getParameter("type");
			int qType = CtiUtilities.NONE_ID;
			if (type.equalsIgnoreCase("Exit"))
				qType = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_EXIT ).getEntryID();
			
			String[] questions = req.getParameterValues("Questions");
			String[] answerTypes = req.getParameterValues("AnswerTypes");
			
			ArrayList liteQuestions = energyCompany.getAllInterviewQuestions();

			synchronized (liteQuestions) {
				Iterator it = liteQuestions.iterator();
				while (it.hasNext()) {
					LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) it.next();
					if (liteQuestion.getQuestionType() == qType) {
						com.cannontech.database.data.stars.InterviewQuestion question =
								new com.cannontech.database.data.stars.InterviewQuestion();
						question.setQuestionID( new Integer(liteQuestion.getQuestionID()) );
						Transaction.createTransaction(Transaction.DELETE, question).execute();
						it.remove();
					}
				}
			}
			
			if (type.equalsIgnoreCase("Exit"))
				starsExitQuestions.removeAllStarsExitInterviewQuestion();
			
			if (questions != null) {
				for (int i = 0; i < questions.length; i++) {
					com.cannontech.database.data.stars.InterviewQuestion question =
							new com.cannontech.database.data.stars.InterviewQuestion();
					com.cannontech.database.db.stars.InterviewQuestion questionDB = question.getInterviewQuestion();
					questionDB.setQuestionType( new Integer(qType) );
					questionDB.setQuestion( questions[i] );
					questionDB.setMandatory( "N" );
					questionDB.setDisplayOrder( new Integer(i+1) );
					questionDB.setAnswerType( Integer.valueOf(answerTypes[i]) );
					questionDB.setExpectedAnswer( new Integer(CtiUtilities.NONE_ID) );
					question.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
					question = (com.cannontech.database.data.stars.InterviewQuestion)
							Transaction.createTransaction(Transaction.INSERT, question).execute();
					
					LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) StarsLiteFactory.createLite( question.getInterviewQuestion() );
					synchronized (liteQuestions) { liteQuestions.add(liteQuestion); }
					
					if (type.equalsIgnoreCase("Exit")) {
						StarsExitInterviewQuestion starsQuestion = new StarsExitInterviewQuestion();
						starsQuestion.setQuestionID( liteQuestion.getQuestionID() );
						starsQuestion.setQuestion( liteQuestion.getQuestion() );
						starsQuestion.setQuestionType( (QuestionType)
								StarsFactory.newStarsCustListEntry(
									energyCompany.getYukonListEntry(YukonSelectionListDefs.YUK_LIST_NAME_QUESTION_TYPE, liteQuestion.getQuestionType()),
									QuestionType.class
									)
								);
						starsQuestion.setAnswerType( (AnswerType)
								StarsFactory.newStarsCustListEntry(
									energyCompany.getYukonListEntry(YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE, liteQuestion.getAnswerType()),
									AnswerType.class
									)
								);
						starsExitQuestions.addStarsExitInterviewQuestion( starsQuestion );
					}
				}
			}

        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Interview questions updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update interview questions");
			redirect = referer;
        }
	}
	
	private void updateCustomerSelectionList(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
		java.sql.Connection conn = null;
		boolean autoCommit = true;
        
		try {
			String listName = req.getParameter("ListName");
			String ordering = req.getParameter("Ordering");
			String label = req.getParameter("Label");
			String whereIsList = req.getParameter("WhereIsList");
			
			String[] entryIDs = req.getParameterValues("EntryIDs");
			String[] entryTexts = req.getParameterValues("EntryTexts");
			String[] yukonDefIDs = req.getParameterValues("YukonDefIDs");
			
			YukonSelectionList cList = energyCompany.getYukonSelectionList( listName );
			Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit( false );
			
			// Handle substation list
			if (listName.equalsIgnoreCase(com.cannontech.database.db.stars.Substation.LISTNAME_SUBSTATION)) {
				// Create a copy of the old entry list, so we won't lose it if something goes wrong
				ArrayList oldEntries = new ArrayList();
				oldEntries.addAll( cList.getYukonListEntries() );
				
				ArrayList newEntries = new ArrayList();
				
				if (entryIDs != null) {
					for (int i = 0; i < entryIDs.length; i++) {
						int entryID = Integer.parseInt(entryIDs[i]);
						
						if (entryID == 0) {
							// This is a new entry, add it to the new entry list
							com.cannontech.database.data.stars.Substation substation =
									new com.cannontech.database.data.stars.Substation();
							substation.getSubstation().setSubstationName( entryTexts[i] );
							substation.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
							substation.setDbConnection( conn );
							substation.add();
							
							YukonListEntry cEntry = new YukonListEntry();
							cEntry.setEntryID( substation.getSubstation().getSubstationID().intValue() );
							cEntry.setEntryText( substation.getSubstation().getSubstationName() );
							newEntries.add( cEntry );
						}
						else {
							// This is an existing entry, update it
							com.cannontech.database.db.stars.Substation subDB =
									new com.cannontech.database.db.stars.Substation();
							subDB.setSubstationID( new Integer(entryID) );
							subDB.setSubstationName( entryTexts[i] );
							subDB.setDbConnection( conn );
							subDB.update();
							
							for (int j = 0; j < oldEntries.size(); j++) {
								YukonListEntry cEntry = (YukonListEntry) oldEntries.get(j);
								if (cEntry.getEntryID() == entryID) {
									cEntry.setEntryText( entryTexts[i] );
									newEntries.add( oldEntries.remove(j) );
									break;
								}
							}
						}
					}
				}
				
				// Delete all the remaining entries
				for (int i = 0; i < oldEntries.size(); i++) {
					int entryID = ((YukonListEntry) oldEntries.get(i)).getEntryID();
					
					try {
						com.cannontech.database.data.stars.Substation substation =
								new com.cannontech.database.data.stars.Substation();
						substation.setSubstationID( new Integer(entryID) );
						substation.setDbConnection( conn );
						substation.delete();
					}
					catch (java.sql.SQLException e) {
						e.printStackTrace();
						throw new WebClientException("Cannot delete substation with id = " + entryID + ", make sure it is not referenced");
					}
				}
				
				conn.commit();
				cList.setYukonListEntries( newEntries );
				
				StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
				selectionListTable.put( starsList.getListName(), starsList );
				
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Substation list updated successfully");
				return;
			}
			
			// Handle customer opt out period list
			if (listName.equalsIgnoreCase(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS)) {
				boolean sameAsOp = Boolean.valueOf( req.getParameter("SameAsOp") ).booleanValue();
				if (sameAsOp && cList == null) return;
				
				if (sameAsOp && cList != null) {
					// Remove the OptOutPeriodCustomer list
					com.cannontech.database.data.constants.YukonSelectionList list =
							new com.cannontech.database.data.constants.YukonSelectionList();
					list.setListID( new Integer(cList.getListID()) );
					list.setDbConnection( conn );
					list.delete();
					
					conn.commit();
					
					for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
						YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(i);
						YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
					}
					energyCompany.deleteYukonSelectionList( cList.getListID() );
					YukonListFuncs.getYukonSelectionLists().remove( new Integer(cList.getListID()) );
					
					selectionListTable.remove( cList.getListName() );
					
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
					return;
				}
				
				if (!sameAsOp && cList == null) {
					// Add a new list of OptOutPeriodCustomer
					YukonSelectionList opList = energyCompany.getYukonSelectionList( YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD );
					cList = energyCompany.addYukonSelectionList( listName, opList, false );
				}
			}
			
			// Handle other selection lists
			com.cannontech.database.db.constants.YukonSelectionList list =
					StarsLiteFactory.createYukonSelectionList( cList );
			list.setOrdering( ordering );
			list.setSelectionLabel( label );
			list.setWhereIsList( whereIsList );
			list.setDbConnection( conn );
			list.update();
			
			// Create a copy of the old entry list, so we won't lose it if something goes wrong
			ArrayList oldEntries = new ArrayList();
			oldEntries.addAll( cList.getYukonListEntries() );
			
			ArrayList newEntries = new ArrayList();
			
			if (entryIDs != null) {
				for (int i = 0; i < entryIDs.length; i++) {
					int entryID = Integer.parseInt( entryIDs[i] );
					int entryOrder = ordering.equalsIgnoreCase("O")? (i+1) : 0;
					
					if (entryID == 0) {
						// This is a new entry, add it to the new entry list
						com.cannontech.database.db.constants.YukonListEntry entry =
								new com.cannontech.database.db.constants.YukonListEntry();
						entry.setListID( list.getListID() );
						entry.setEntryOrder( new Integer(entryOrder) );
						entry.setEntryText( entryTexts[i] );
						entry.setYukonDefID( Integer.valueOf(yukonDefIDs[i]) );
						entry.setDbConnection( conn );
						entry.add();
						
						com.cannontech.common.constants.YukonListEntry cEntry =
								new com.cannontech.common.constants.YukonListEntry();
						StarsLiteFactory.setConstantYukonListEntry( cEntry, entry );
						newEntries.add( cEntry );
					}
					else {
						// This is an existing entry, update it
						for (int j = 0; j < oldEntries.size(); j++) {
							YukonListEntry cEntry = (YukonListEntry) oldEntries.get(j);
							
							if (cEntry.getEntryID() == entryID) {
								com.cannontech.database.db.constants.YukonListEntry entry = StarsLiteFactory.createYukonListEntry(cEntry);
								entry.setEntryOrder( new Integer(entryOrder) );
								entry.setEntryText( entryTexts[i] );
								entry.setYukonDefID( Integer.valueOf(yukonDefIDs[i]) );
								entry.setDbConnection( conn );
								entry.update();
								
								StarsLiteFactory.setConstantYukonListEntry(cEntry, entry);
								newEntries.add( oldEntries.remove(j) );
								break;
							}
						}
					}
				}
			}
			
			// Delete all the remaining entries
			for (int i = 0; i < oldEntries.size(); i++) {
				int entryID = ((YukonListEntry) oldEntries.get(i)).getEntryID();
				
				try {
					com.cannontech.database.db.constants.YukonListEntry entry =
							new com.cannontech.database.db.constants.YukonListEntry();
					entry.setEntryID( new Integer(entryID) );
					entry.setDbConnection( conn );
					entry.delete();
				}
				catch (java.sql.SQLException e) {
					e.printStackTrace();
					throw new WebClientException("Cannot delete list entry with id = " + entryID + ", make sure it is not referenced");
				}
			}
			
			conn.commit();
			
			// Update the constant objects (in both stars and core yukon cache)
			for (int i = 0; i < cList.getYukonListEntries().size(); i++) {
				YukonListEntry entry = (YukonListEntry) cList.getYukonListEntries().get(i);
				YukonListFuncs.getYukonListEntries().remove( new Integer(entry.getEntryID()) );
			}
			
			for (int i = 0; i < newEntries.size(); i++) {
				YukonListEntry entry = (YukonListEntry) newEntries.get(i);
				YukonListFuncs.getYukonListEntries().put( new Integer(entry.getEntryID()), entry );
			}
			
			StarsLiteFactory.setConstantYukonSelectionList( cList, list );
			cList.setYukonListEntries( newEntries );
			
			StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
			selectionListTable.put( starsList.getListName(), starsList );
			
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Customer selection list updated successfully");
		}
		catch (WebClientException e) {
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
			
			try {
				if (conn != null) conn.rollback();
			}
			catch (java.sql.SQLException e2) {}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update customer selection list");
			redirect = referer;
		}
		finally {
			try {
				if (conn != null) {
					conn.setAutoCommit( autoCommit );
					conn.close();
				} 
			}
			catch (java.sql.SQLException e) {}
		}
	}
	
	private void updateThermostatSchedule(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
        LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        
        try {
        	StarsDefaultThermostatSettings dftSettings = energyCompany.getStarsDefaultThermostatSettings();
        	
        	UpdateThermostatScheduleAction action = new UpdateThermostatScheduleAction();
        	SOAPMessage reqMsg = action.build( req, session );
        	StarsUpdateThermostatSchedule updateSched = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsUpdateThermostatSchedule();
        	
        	LiteStarsLMHardware liteHw = energyCompany.getDefaultLMHardware();
        	StarsUpdateThermostatScheduleResponse resp = UpdateThermostatScheduleAction.updateThermostatSchedule( updateSched, liteHw, energyCompany );
			UpdateThermostatScheduleAction.parseResponse( resp, dftSettings );
			
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Default thermostat schedule updated successfully");
        }
        catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to update default thermostat schedule");
			redirect = referer;
        }
	}
	
	private void createEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		try {
			String warning = null;
			LiteYukonGroup operGroup = null;
			LiteYukonGroup custGroup = null;
			
			String operGroupName = req.getParameter("OperatorGroup");
			if (operGroupName.length() > 0) {
				operGroup = AuthFuncs.getGroup( operGroupName );
				if (operGroup == null) {
					if (warning == null) warning = "Warning: ";
					warning += "group \"" + operGroupName + "\" doesn't exist";
				}
			}
			
			String custGroupName = req.getParameter("CustomerGroup");
			if (custGroupName.length() > 0) {
				custGroup = AuthFuncs.getGroup( custGroupName );
				if (custGroup == null) {
					if (warning == null) warning = "Warning: ";
					else warning += ", ";
					warning += "group '" + custGroupName + "' doesn't exist";
				}
			}
			
			com.cannontech.database.data.user.YukonUser yukonUser =
					new com.cannontech.database.data.user.YukonUser();
			yukonUser.getYukonUser().setUsername( req.getParameter("Username") );
			yukonUser.getYukonUser().setPassword( req.getParameter("Password") );
			yukonUser.getYukonUser().setStatus( com.cannontech.user.UserUtils.STATUS_ENABLED );
			
			// Assign the operator group to login
			if (operGroup != null) {
				com.cannontech.database.db.user.YukonGroup group =
						new com.cannontech.database.db.user.YukonGroup();
				group.setGroupID( new Integer(operGroup.getGroupID()) );
				yukonUser.getYukonGroups().add( group );
			}
			
			// Assign the EnergyCompany role to login
			LiteYukonRoleProperty[] roleProps = RoleFuncs.getRoleProperties( EnergyCompanyRole.ROLEID );
			for (int i = 0; i < roleProps.length; i++) {
				com.cannontech.database.db.user.YukonUserRole userRole =
						new com.cannontech.database.db.user.YukonUserRole();
				userRole.setRoleID( new Integer(EnergyCompanyRole.ROLEID) );
				userRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
				if (roleProps[i].getRolePropertyID() == EnergyCompanyRole.CUSTOMER_GROUP_NAME && custGroup != null)
					userRole.setValue( custGroupName );
				else if (roleProps[i].getRolePropertyID() == EnergyCompanyRole.OPERATOR_GROUP_NAME && operGroup != null)
					userRole.setValue( operGroupName );
				else
					userRole.setValue( CtiUtilities.STRING_NONE );
				yukonUser.getYukonUserRoles().add( userRole );
			}
			
			// Assign the Administrator role to login, and allow configuring energy company
			roleProps = RoleFuncs.getRoleProperties( AdministratorRole.ROLEID );
			for (int i = 0; i < roleProps.length; i++) {
				com.cannontech.database.db.user.YukonUserRole userRole =
						new com.cannontech.database.db.user.YukonUserRole();
				userRole.setRoleID( new Integer(AdministratorRole.ROLEID) );
				userRole.setRolePropertyID( new Integer(roleProps[i].getRolePropertyID()) );
				userRole.setValue( CtiUtilities.TRUE_STRING );
				if (roleProps[i].getRolePropertyID() == AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY)
					yukonUser.getYukonUserRoles().add( userRole );
				else
					userRole.setValue( CtiUtilities.STRING_NONE );
			}
			
			yukonUser = (com.cannontech.database.data.user.YukonUser)
					Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
			com.cannontech.database.db.user.YukonUser userDB = yukonUser.getYukonUser();
			
			com.cannontech.database.db.company.EnergyCompany company =
					new com.cannontech.database.db.company.EnergyCompany();
			company.setName( req.getParameter("CompanyName") );
			company.setPrimaryContactID( new Integer(CtiUtilities.NONE_ID) );
			company.setUserID( userDB.getUserID() );
			company = (com.cannontech.database.db.company.EnergyCompany)
					Transaction.createTransaction(Transaction.INSERT, company).execute();
			
			LiteStarsEnergyCompany energyCompany = new LiteStarsEnergyCompany( company );
			SOAPServer.addEnergyCompany( energyCompany );
			ServerUtils.handleDBChange( energyCompany, DBChangeMsg.CHANGE_TYPE_ADD );
			
			String sql = "INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
					company.getEnergyCompanyID() + ", " + userDB.getUserID() + ")";
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql, CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			LiteYukonUser liteUser = new LiteYukonUser(
					userDB.getUserID().intValue(),
					userDB.getUsername(),
					userDB.getPassword(),
					userDB.getStatus()
					);
			ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_ADD );
			
			// Create login for the second operator
			if (req.getParameter("Username2").length() > 0) {
				yukonUser = new com.cannontech.database.data.user.YukonUser();
				yukonUser.getYukonUser().setUsername( req.getParameter("Username2") );
				yukonUser.getYukonUser().setPassword( req.getParameter("Password2") );
				yukonUser.getYukonUser().setStatus( com.cannontech.user.UserUtils.STATUS_ENABLED );
				
				if (operGroup != null) {
					com.cannontech.database.db.user.YukonGroup group =
							new com.cannontech.database.db.user.YukonGroup();
					group.setGroupID( new Integer(operGroup.getGroupID()) );
					yukonUser.getYukonGroups().add( group );
				}
				
				yukonUser = (com.cannontech.database.data.user.YukonUser)
						Transaction.createTransaction(Transaction.INSERT, yukonUser).execute();
				userDB = yukonUser.getYukonUser();
				
				sql = "INSERT INTO EnergyCompanyOperatorLoginList VALUES(" +
						company.getEnergyCompanyID() + ", " + userDB.getUserID() + ")";
				stmt.setSQLString( sql );
				stmt.execute();
				
				liteUser = new LiteYukonUser(
						userDB.getUserID().intValue(),
						userDB.getUsername(),
						userDB.getPassword(),
						userDB.getStatus()
						);
				ServerUtils.handleDBChange( liteUser, DBChangeMsg.CHANGE_TYPE_ADD );
			}
			
        	session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, "Energy company created successfully");
        	if (warning != null) session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, warning);
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to create the energy company");
		}
	}
	
	private void deleteEnergyCompany(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        java.sql.Connection conn = null;
		
		try {
			// Delete all customer accounts
			int[] accountIDs = CustomerAccount.searchByAccountNumber(
					energyCompany.getEnergyCompanyID(), "%" );
			if (accountIDs != null) {
				for (int i = 0; i < accountIDs.length; i++) {
					LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( accountIDs[i], true );
					DeleteCustAccountAction.deleteCustomerAccount( liteAcctInfo, energyCompany );
				}
			}
			
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			// Delete all inventories
			String sql = "SELECT InventoryID FROM ECToInventoryMapping WHERE EnergyCompanyID = ?";
			java.sql.PreparedStatement stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, user.getEnergyCompanyID() );
			java.sql.ResultSet rset = stmt.executeQuery();
			
			java.sql.Connection conn2 = null;
			try {
				conn2 = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				while (rset.next()) {
					int inventoryID = rset.getInt(1);
					com.cannontech.database.data.stars.hardware.InventoryBase inventory =
							new com.cannontech.database.data.stars.hardware.InventoryBase();
					inventory.setInventoryID( new Integer(inventoryID) );
					inventory.setDbConnection( conn2 );
					inventory.delete();
				}
			}
			finally {
				if (conn2 != null) conn2.close();
			}
			
			rset.close();
			stmt.close();
			
			// Delete all substations
			ECToGenericMapping[] substations = ECToGenericMapping.getAllMappingItems(
					energyCompany.getEnergyCompanyID(), com.cannontech.database.db.stars.Substation.TABLE_NAME );
			if (substations != null) {
				for (int i = 0; i < substations.length; i++) {
					com.cannontech.database.data.stars.Substation substation =
							new com.cannontech.database.data.stars.Substation();
					substation.setSubstationID( substations[i].getItemID() );
					substation.setDbConnection( conn );
					substation.delete();
				}
			}
			
			// Delete all service companies
			for (int i = 0; i < energyCompany.getAllServiceCompanies().size(); i++) {
				LiteServiceCompany liteCompany = (LiteServiceCompany) energyCompany.getAllServiceCompanies().get(i);
				com.cannontech.database.data.stars.report.ServiceCompany company =
						new com.cannontech.database.data.stars.report.ServiceCompany();
				StarsLiteFactory.setServiceCompany( company.getServiceCompany(), liteCompany );
				company.setDbConnection( conn );
				company.delete();
			}
			
			// Delete all appliance categories
			for (int i = 0; i < energyCompany.getAllApplianceCategories().size(); i++) {
				LiteApplianceCategory liteAppCat = (LiteApplianceCategory) energyCompany.getAllApplianceCategories().get(i);
				
				com.cannontech.database.db.stars.LMProgramWebPublishing.deleteAllLMProgramWebPublishing(
						new Integer(liteAppCat.getApplianceCategoryID()), conn );
				for (int j = 0; j < liteAppCat.getPublishedPrograms().length; j++) {
					int configID = liteAppCat.getPublishedPrograms()[j].getWebSettingsID();
					com.cannontech.database.db.web.YukonWebConfiguration cfg =
							new com.cannontech.database.db.web.YukonWebConfiguration();
					cfg.setConfigurationID( new Integer(configID) );
					cfg.setDbConnection( conn );
					cfg.delete();
				}
				
				com.cannontech.database.data.stars.appliance.ApplianceCategory appCat =
						new com.cannontech.database.data.stars.appliance.ApplianceCategory();
				StarsLiteFactory.setApplianceCategory( appCat.getApplianceCategory(), liteAppCat );
				appCat.setDbConnection( conn );
				appCat.delete();
			}
			
			// Delete all interview questions
			for (int i = 0; i < energyCompany.getAllInterviewQuestions().size(); i++) {
				LiteInterviewQuestion liteQuestion = (LiteInterviewQuestion) energyCompany.getAllInterviewQuestions().get(i);
				com.cannontech.database.data.stars.InterviewQuestion question =
						new com.cannontech.database.data.stars.InterviewQuestion();
				question.setQuestionID( new Integer(liteQuestion.getQuestionID()) );
				question.setDbConnection( conn );
				question.delete();
			}
			
			// Delete all customer FAQs
			for (int i = 0; i < energyCompany.getAllCustomerFAQs().size(); i++) {
				LiteCustomerFAQ liteFAQ = (LiteCustomerFAQ) energyCompany.getAllCustomerFAQs().get(i);
				com.cannontech.database.db.stars.CustomerFAQ faq =
						new com.cannontech.database.db.stars.CustomerFAQ();
				faq.setQuestionID( new Integer(liteFAQ.getQuestionID()) );
				faq.setDbConnection( conn );
				faq.delete();
			}
			
			// Delete customer selection lists
			for (int i = 0; i < energyCompany.getAllSelectionLists().size(); i++) {
				YukonSelectionList cList = (YukonSelectionList) energyCompany.getAllSelectionLists().get(i);
				if (cList.getListID() == LiteStarsEnergyCompany.FAKE_LIST_ID) continue;
				
				Integer listID = new Integer( cList.getListID() );
				com.cannontech.database.data.constants.YukonSelectionList list =
						new com.cannontech.database.data.constants.YukonSelectionList();
				list.setListID( listID );
				list.setDbConnection( conn );
				list.delete();
				
				YukonListFuncs.getYukonSelectionLists().remove( listID );
				for (int j = 0; j < cList.getYukonListEntries().size(); j++) {
					YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(j);
					YukonListFuncs.getYukonListEntries().remove( new Integer(cEntry.getEntryID()) );
				}
			}
			
			// Delete operator logins (except the default login)
			sql = "SELECT OperatorLoginID FROM EnergyCompanyOperatorLoginList WHERE EnergyCompanyID = ?";
			stmt = conn.prepareStatement( sql );
			stmt.setInt(1, energyCompany.getLiteID());
			rset = stmt.executeQuery();
			
			try {
				conn2 = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
				
				while (rset.next()) {
					int userID = rset.getInt(1);
					if (userID == energyCompany.getUserID()) continue;
					
					com.cannontech.database.data.user.YukonUser yukonUser =
							new com.cannontech.database.data.user.YukonUser();
					yukonUser.setUserID( new Integer(userID) );
					yukonUser.setDbConnection( conn2 );
					yukonUser.deleteOperatorLogin();
					
					ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(userID), DBChangeMsg.CHANGE_TYPE_DELETE );
				}
			}
			finally {
				if (conn2 != null) conn2.close();
			}
			
			rset.close();
			stmt.close();
			
			// Delete the energy company!
			com.cannontech.database.data.company.EnergyCompanyBase ec =
					new com.cannontech.database.data.company.EnergyCompanyBase();
			ec.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			ec.getEnergyCompany().setPrimaryContactID( new Integer(energyCompany.getPrimaryContactID()) );
			ec.setDbConnection( conn );
			ec.delete();
			
			SOAPServer.deleteEnergyCompany( energyCompany.getLiteID() );
			ServerUtils.handleDBChange( energyCompany, DBChangeMsg.CHANGE_TYPE_DELETE );
			if (energyCompany.getPrimaryContactID() != CtiUtilities.NONE_ID) {
				LiteContact liteContact = ContactFuncs.getContact( energyCompany.getPrimaryContactID() );
				ServerUtils.handleDBChange( liteContact, DBChangeMsg.CHANGE_TYPE_DELETE );
			}
			
			if (energyCompany.getUserID() != com.cannontech.user.UserUtils.USER_YUKON_ID) {
				com.cannontech.database.data.user.YukonUser yukonUser =
						new com.cannontech.database.data.user.YukonUser();
				yukonUser.setUserID( new Integer(energyCompany.getUserID()) );
				yukonUser.setDbConnection( conn );
				yukonUser.deleteOperatorLogin();
				
				ServerUtils.handleDBChange( YukonUserFuncs.getLiteYukonUser(energyCompany.getUserID()), DBChangeMsg.CHANGE_TYPE_DELETE );
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to delete the energy company");
			redirect = referer;
		}
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
        }
	}
	
}
