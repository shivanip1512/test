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
import com.cannontech.database.data.lite.LiteContact;
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
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsInv;
import com.cannontech.stars.xml.serialize.StarsLMHardwareConfig;
import com.cannontech.stars.xml.serialize.StarsNewCustomerAccount;
import com.cannontech.stars.xml.serialize.StarsOperation;
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
	public static final String USER_LABELS = "USER_LABELS";
	
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
    
	public static final String HARDWARE_ACTION_INSERT = "INSERT";
	public static final String HARDWARE_ACTION_UPDATE = "UPDATE";
	public static final String HARDWARE_ACTION_REMOVE = "REMOVE";
    
	private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );
	private static final Integer ZERO = new Integer(0);
    
	private static StreamTokenizer prepareStreamTokenzier(String line) {
		StreamTokenizer st = new StreamTokenizer( new StringReader(line) );
		st.resetSyntax();
		st.wordChars( 0, 255 );
		st.ordinaryChar( ',' );
		st.quoteChar( '"' );
		
		return st;
	}
    
	public static String[] prepareFields(int numFields) {
		String[] fields = new String[ numFields ];
		for (int i = 0; i < numFields; i++)
			fields[i] = "";
		
		return fields;
	}
	
	public static String[] parseColumns(String line) throws java.io.IOException {
		StreamTokenizer st = prepareStreamTokenzier( line );
		ArrayList colList = new ArrayList();
		
		String token = null;	// Current token
		boolean isSeparatorLast = true;	// Whether the last token is a separator (,)
		
		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			if (isSeparatorLast) {
				if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"') {
					colList.add( st.sval );
					isSeparatorLast = false;
				}
				else if (st.ttype == ',') {
					colList.add( "" );
				}
			}
			else {
				if (st.ttype == ',') isSeparatorLast = true;
			}
		}
		
		// If the line ends with a comma, add an empty string to the column list 
		if (isSeparatorLast) colList.add( "" );
		
		String[] columns = new String[ colList.size() ];
		colList.toArray( columns );
		return columns;
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
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS);
				
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
				
				fields[NUM_ACCOUNT_FIELDS + IDX_DEVICE_TYPE] = "ExpressStat";
				
				if (fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY].equalsIgnoreCase("Western"))
					fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = "\"Western Heating\"";
				else if (fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY].equalsIgnoreCase("Ridgeway"))
					fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = "\"Ridgeway Industrial\"";
				else if (fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY].equalsIgnoreCase("Access"))
					fields[NUM_ACCOUNT_FIELDS + IDX_SERVICE_COMPANY] = "\"Access Heating\"";
				
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
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS);
				
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
				String[] fields = prepareFields(NUM_ACCOUNT_FIELDS + NUM_INV_FIELDS);
				
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
				
				fields[NUM_ACCOUNT_FIELDS + IDX_DEVICE_TYPE] = "ExpressStat";
				
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
			primContact.setHomePhone( ServletUtils.formatPhoneNumber(fields[IDX_HOME_PHONE]) );
			primContact.setWorkPhone( ServletUtils.formatPhoneNumber(fields[IDX_WORK_PHONE]) + fields[IDX_WORK_PHONE_EXT] );
	    }
	    catch (WebClientException se) {}
	    
	    Email email = new Email();
	    email.setNotification( fields[IDX_EMAIL] );
	    email.setEnabled( false );
	    primContact.setEmail( email );
	    account.setPrimaryContact( primContact );
	}

	public static LiteStarsCustAccountInformation newCustomerAccount(String[] fields, StarsYukonUser user,
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint) throws Exception
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
		
		return NewCustAccountAction.newCustomerAccount(newAccount, user, energyCompany, checkConstraint);
	}

	public static void updateCustomerAccount(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
	    StarsUpdateCustomerAccount updateAccount = new StarsUpdateCustomerAccount();
	    ImportManager.setStarsCustAccount( updateAccount, fields, energyCompany );
	    
	    UpdateCustAccountAction.updateCustomerAccount( updateAccount, liteAcctInfo, energyCompany );
	}

	public static void updateLogin(String[] fields, LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws Exception
	{
		LiteContact liteContact = energyCompany.getContact( liteAcctInfo.getCustomer().getPrimaryContactID(), liteAcctInfo );
		if ((liteContact.getLoginID() == UserUtils.USER_YUKON_ID ||
			liteContact.getLoginID() == UserUtils.USER_STARS_DEFAULT_ID)
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
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint) throws Exception
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
				throw new ImportProblem( ImportProblem.NO_DEVICE_NAME );
			liteInv = energyCompany.searchForDevice( categoryID, fields[IDX_DEVICE_NAME] );
			if (liteInv == null)
				throw new ImportProblem( ImportProblem.DEVICE_NAME_NOT_FOUND );
		}
		
		if (liteInv != null) {
			if (liteInv.getAccountID() > 0)
				throw new ImportProblem( ImportProblem.DUPLICATE_HARDWARE );
			
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
		ImportManager.setStarsInventory( createHw, fields, energyCompany );
	    
	    return CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
	}

	public static LiteInventoryBase updateLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws Exception
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
	        
			DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany );
			return CreateLMHardwareAction.addInventory( createHw, liteAcctInfo, energyCompany );
		}
		else {
			UpdateLMHardwareAction.updateInventory( updateHw, liteHw, energyCompany );
			return energyCompany.getInventory( liteHw.getInventoryID(), true );
		}
	}

	public static void removeLMHardware(String[] fields, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws Exception
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
		
		DeleteLMHardwareAction.removeInventory( deleteHw, liteAcctInfo, energyCompany );
	}
	
	/**
	 * @param programs Array of (ProgramID, ApplianceCategoryID, GroupID).
	 * The ApplianceCategoryID and GroupID are optional, set them to -1 if you don't want to privide the value.
	 * @param invID ID of the hardware the programs are attached to
	 */
	public static void programSignUp(int[][] programs, LiteStarsCustAccountInformation liteAcctInfo,
		Integer invID, LiteStarsEnergyCompany energyCompany) throws Exception
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
			suPrograms.addSULMProgram( suProg );
		}
		
		StarsProgramSignUp progSignUp = new StarsProgramSignUp();
		progSignUp.setStarsSULMPrograms( suPrograms );
	    
	    ProgramSignUpAction.updateProgramEnrollment( progSignUp, liteAcctInfo, invID, energyCompany );
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
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint) throws Exception
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
		
		boolean isMultiPart = DiskFileUpload.isMultipartContent( req );
		List items = null;
		String action = null;
		
		if (isMultiPart) {
			try {
				DiskFileUpload upload = new DiskFileUpload();
				items = upload.parseRequest( req );
				action = ServerUtils.getFormField( items, "action" );
				redirect = ServerUtils.getFormField( items, ServletUtils.ATT_REDIRECT );
			}
			catch (FileUploadException e) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to parse the form data");
			}
		}
		else {
			action = req.getParameter( "action" );
			redirect = req.getParameter( ServletUtils.ATT_REDIRECT );
		}
		
		if (action == null) action = "";
		referer = req.getHeader( "referer" );
		if (redirect == null) redirect = referer;
		
		if (user.getAttribute(ServletUtils.ATT_CONTEXT_SWITCHED) != null && !action.equalsIgnoreCase("RestoreContext")) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Operation not allowed because you are currently checking information of a member. To make any changes, you must first log into the member energy company through \"Member Management\"." );
			resp.sendRedirect( referer );
			return;
		}
		
		if (action.equalsIgnoreCase("ImportCustAccounts"))
			importCustomerAccounts( items, user, req, session );
		else if (action.equalsIgnoreCase("ImportINIData"))
			importINIData( user, req, session );
		else if (action.equalsIgnoreCase("PreprocessStarsData"))
			preprocessStarsData( user, req, session );
		else if (action.equalsIgnoreCase("AssignSelectionList"))
			assignSelectionList( user, req, session );
		else if (action.equalsIgnoreCase("ImportStarsData"))
			importStarsData( user, req, session );
        
		resp.sendRedirect( redirect );
	}
	
	private void importCustomerAccounts(List items, StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		try {
			File custFile = ServerUtils.getUploadFile( items, "CustFile" );
			File hwFile = ServerUtils.getUploadFile( items, "HwFile" );
			
			ImportCustAccountsTask task = new ImportCustAccountsTask( user, custFile, hwFile );
			long id = ProgressChecker.addTask( task );
			
			// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
			for (int i = 0; i < 5; i++) {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {}
				
				if (task.getStatus() == ImportCustAccountsTask.STATUS_FINISHED) {
					session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
					ProgressChecker.removeTask( id );
					return;
				}
				
				if (task.getStatus() == ImportCustAccountsTask.STATUS_ERROR) {
					session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
					ProgressChecker.removeTask( id );
					return;
				}
			}
			
			session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
			redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
		}
		catch (WebClientException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
			redirect = referer;
		}
	}
	
	private void importSelectionLists(File selListFile, LiteStarsEnergyCompany energyCompany) throws Exception {
		String[] selListLines = ServerUtils.readFile( selListFile );
		if (selListLines == null)
			throw new WebClientException("Unable to read selection list file '" + selListFile.getPath() + "'");
		
		String listName = null;
		ArrayList listEntries = null;
		boolean isInList = false;
		
		for (int i = 0; i < selListLines.length; i++) {
			if (!isInList) {
				if (!selListLines[i].startsWith("[")) continue;
				
				for (int j = 0; j < LIST_NAMES.length; j++) {
					if (LIST_NAMES[j][2].equals( selListLines[i] )) {
						listName = LIST_NAMES[j][0];
						listEntries = new ArrayList();
						isInList = true;
						break;
					}
				}
			}
			else {
				if (selListLines[i].trim().equals("")) {
					if (isInList && listEntries.size() > 0) {
						// Find the end of a list, update the list entries
						if (listName.equals("ServiceCompany")) {
							StarsAdmin.deleteAllServiceCompanies( energyCompany );
							
							for (int j = 0; j < listEntries.size(); j++) {
								String entry = (String) listEntries.get(j);
								StarsAdmin.createServiceCompany( entry, energyCompany );
							}
						}
						else if (listName.equals("LoadType")) {
							for (int j = 0; j < listEntries.size(); j++) {
								String entry = (String) listEntries.get(j);
								StarsAdmin.createApplianceCategory( entry, energyCompany );
							}
						}
						else {
							// Always add an empty entry at the beginning of the list
							listEntries.add(0, " ");
							
							Object[][] entryData = new Object[ listEntries.size() ][];
							for (int j = 0; j < listEntries.size(); j++) {
								entryData[j] = new Object[3];
								entryData[j][0] = ZERO;
								entryData[j][1] = listEntries.get(j);
								entryData[j][2] = ZERO;
							}
							
							YukonSelectionList cList = energyCompany.getYukonSelectionList(listName, false);
							StarsAdmin.updateYukonListEntries( cList, entryData, energyCompany );
						}
					}
					
					isInList = false;
				}
				else {
					if (selListLines[i].endsWith("="))
						selListLines[i] = selListLines[i].substring(0, selListLines[i].length() - 1);
					listEntries.add( selListLines[i] );
				}
			}
		}
	}
	
	private void importUserLabels(File usrLabelFile, HttpSession session) throws WebClientException {
		String[] usrLabelLines = ServerUtils.readFile( usrLabelFile );
		if (usrLabelLines == null)
			throw new WebClientException( "Unable to read user label file '" + usrLabelFile.getPath() + "'" );
		
		boolean inUsrLabelDefs = false;
		Hashtable userLabels = new Hashtable();
		
		for (int i = 0; i < usrLabelLines.length; i++) {
			if (!inUsrLabelDefs) {
				if (usrLabelLines[i].equals( "[User Labels]" ))
					inUsrLabelDefs = true;
				continue;
			}
			else {
				if (usrLabelLines[i].startsWith("[")) break;
				if (usrLabelLines[i].trim().equals("")) continue;
				
				int pos = usrLabelLines[i].indexOf('=');
				if (pos < 0) continue;
				String name = usrLabelLines[i].substring( 0, pos );
				String value = usrLabelLines[i].substring( pos+1 );
				if (!value.startsWith("@"))
					userLabels.put( name, value );
			}
		}
		
		session.setAttribute( USER_LABELS, userLabels );
	}
	
	private void importINIData(StarsYukonUser user, HttpServletRequest req, HttpSession session) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
		
		try {
			boolean hasSelListFile = false;
			
			if (req.getParameter("SelListFile").length() > 0) {
				File selListFile = new File( req.getParameter("SelListFile") );
				importSelectionLists( selListFile, energyCompany );
				hasSelListFile = true;
			}
			
			if (req.getParameter("UsrLabelFile").length() > 0) {
				File usrLabelFile = new File( req.getParameter("UsrLabelFile") );
				importUserLabels( usrLabelFile, session );
			}
			
			String msg = "INI file(s) imported successfully.";
			if (hasSelListFile)
				msg += " Please go to the energy company settings page to update the appliance category information.";
			session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, msg);
		}
		catch (WebClientException e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to import INI file(s)");
		}
	}
	
	private Integer getTextEntryID(String text, String listName, LiteStarsEnergyCompany energyCompany) {
		if (listName.equals("ServiceCompany")) {
			if (text.equals("")) return null;
			
			ArrayList companies = energyCompany.getAllServiceCompanies();
			for (int i = 0; i < companies.size(); i++) {
				LiteServiceCompany liteCompany = (LiteServiceCompany) companies.get(i);
				if (text.equals( liteCompany.getCompanyName() ))
					return new Integer( liteCompany.getCompanyID() );
			}
		}
		else if (listName.equals("LoadType")) {
			if (text.equals("")) return null;
			
			ArrayList appCats = energyCompany.getAllApplianceCategories();
			for (int i = 0; i < appCats.size(); i++) {
				LiteApplianceCategory liteAppCat = (LiteApplianceCategory) appCats.get(i);
				if (text.equals( liteAppCat.getDescription() ))
					return new Integer( liteAppCat.getApplianceCategoryID() );
			}
		}
		else {
			YukonSelectionList list = energyCompany.getYukonSelectionList( listName );
			if (list != null) {
				for (int i = 0; i < list.getYukonListEntries().size(); i++) {
					YukonListEntry entry = (YukonListEntry) list.getYukonListEntries().get(i);
					if (text.equals( entry.getEntryText().trim() ))
						return new Integer( entry.getEntryID() );
				}
			}
		}
		
		return ZERO;
	}
	
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
	private String[] parseStarsCustomer(String line) throws Exception {
		String[] fields = prepareFields( NUM_ACCOUNT_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 18)
			throw new WebClientException( "Incorrect number of fields in customer file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_ACCOUNT_NO] = columns[1];
		if (columns[2].length() > 0)
			fields[IDX_ACCOUNT_NO] += "-" + columns[2];
		fields[IDX_CUSTOMER_TYPE] = columns[3];
		fields[IDX_LAST_NAME] = columns[4];
		fields[IDX_FIRST_NAME] = columns[5];
		fields[IDX_COMPANY_NAME] = columns[7];
		fields[IDX_MAP_NO] = columns[8];
		fields[IDX_STREET_ADDR1] = columns[9];
		fields[IDX_STREET_ADDR2] = columns[10];
		fields[IDX_CITY] = columns[11];
		fields[IDX_STATE] = columns[12];
		fields[IDX_ZIP_CODE] = columns[13];
		fields[IDX_HOME_PHONE] = columns[14];
		fields[IDX_WORK_PHONE] = columns[15];
		fields[IDX_ACCOUNT_NOTES] = columns[17];
		
		return fields;
	}
	
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
	 * 10-17	PROP_NOTES
	 */
	private String[] parseStarsServiceInfo(String line, Hashtable userLabels) throws Exception {
		String[] fields = prepareFields( NUM_ACCOUNT_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 17)
			throw new WebClientException( "Incorrect number of fields in service info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		if (columns[1].length() > 0)
			fields[IDX_PROP_NOTES] += "MeterNumber: " + columns[1] + LINE_SEPARATOR;
		fields[IDX_SUBSTATION] = columns[2];
		fields[IDX_FEEDER] = columns[3];
		fields[IDX_POLE] = columns[4];
		fields[IDX_TRFM_SIZE] = columns[5];
		fields[IDX_SERV_VOLT] = columns[6];
		if (columns[9].length() > 0 && userLabels.get(LABEL_SI_CHAR1) != null) {
			String text = columns[9].substring( columns[9].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_CHAR1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[10].length() > 0 && userLabels.get(LABEL_SI_CHAR2) != null) {
			String text = columns[10].substring( columns[10].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_CHAR2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[11].length() > 0 && userLabels.get(LABEL_SI_DROPBOX1) != null) {
			String text = columns[11].substring( columns[11].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_DROPBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[12].length() > 0 && userLabels.get(LABEL_SI_DROPBOX2) != null) {
			String text = columns[12].substring( columns[12].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_DROPBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[13].length() > 0 && userLabels.get(LABEL_SI_CHECKBOX1) != null) {
			String text = columns[13].substring( columns[13].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_CHECKBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[14].length() > 0 && userLabels.get(LABEL_SI_CHECKBOX2) != null) {
			String text = columns[14].substring( columns[14].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_CHECKBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[15].length() > 0 && userLabels.get(LABEL_SI_NUMERIC1) != null) {
			String text = columns[15].substring( columns[15].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_NUMERIC1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[16].length() > 0 && userLabels.get(LABEL_SI_NUMERIC2) != null) {
			String text = columns[16].substring( columns[16].indexOf(':')+1 );
			fields[IDX_PROP_NOTES] += userLabels.get(LABEL_SI_NUMERIC2) + ": " + text + LINE_SEPARATOR;
		}
		
		return fields;
	}
    
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
	private String[] parseStarsInventory(String line, Hashtable userLabels) throws Exception {
		String[] fields = prepareFields(NUM_INV_FIELDS);
		String[] columns = parseColumns( line );
		if (columns.length != 19)
			throw new WebClientException( "Incorrect number of fields in inventory file" );
		
		fields[IDX_INV_ID] = columns[0];
		fields[IDX_SERIAL_NO] = columns[1];
		fields[IDX_ACCOUNT_ID] = columns[2];
		fields[IDX_ALT_TRACK_NO] = columns[3];
		fields[IDX_DEVICE_NAME] = columns[4];
		if (columns[5].length() > 0)
			fields[IDX_INV_NOTES] += "MapNumber: " + columns[5] + LINE_SEPARATOR;
		if (columns[6].length() > 0)
			fields[IDX_INV_NOTES] += "OriginAddr1: " + columns[6] + LINE_SEPARATOR;
		if (columns[7].length() > 0)
			fields[IDX_INV_NOTES] += "OriginAddr2: " + columns[7] + LINE_SEPARATOR;
		fields[IDX_DEVICE_TYPE] = columns[8];
		fields[IDX_DEVICE_VOLTAGE] = columns[12];
		fields[IDX_RECEIVE_DATE] = columns[13];
		fields[IDX_SERVICE_COMPANY] = columns[15];
		if (columns[16].length() > 0 && userLabels.get(LABEL_DI_CHAR1) != null) {
			String text = columns[16].substring( columns[16].indexOf(':')+1 );
			fields[IDX_INV_NOTES] += userLabels.get(LABEL_DI_CHAR1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[17].length() > 0 && userLabels.get(LABEL_DI_DROPBOX1) != null) {
			String text = columns[17].substring( columns[17].indexOf(':')+1 );
			fields[IDX_INV_NOTES] += userLabels.get(LABEL_DI_DROPBOX1) + ": " + text + LINE_SEPARATOR;
		}
		//TODO: Date entry label not found in INI file 
		
		return fields;
	}
	
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
	private String[] parseStarsReceiver(String line) throws Exception {
		String[] fields = prepareFields( NUM_INV_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 20)
			throw new WebClientException( "Incorrect number of fields in receiver file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_INV_ID] = columns[1];
		fields[IDX_INSTALL_DATE] = columns[2];
		for (int i = 3; i <= 5; i++) {
			if (columns[i].length() > 0)
				fields[IDX_INV_NOTES] += columns[i] + LINE_SEPARATOR;
		}
		if (columns[6].length() > 0)
			fields[IDX_INV_NOTES] += "Technician: " + columns[6] + LINE_SEPARATOR;
		if (columns[7].length() > 0)
			fields[IDX_INV_NOTES] += "Location: " + columns[7] + LINE_SEPARATOR;
		for (int i = 8; i <= 9; i++) {
			if (columns[i].length() > 0)
				fields[IDX_INV_NOTES] += columns[i] + LINE_SEPARATOR;
		}
		fields[IDX_DEVICE_STATUS] = columns[10];
		for (int i = 0; i < 3; i++) {
			fields[IDX_R1_GROUP + i] = columns[11+3*i].substring( "RX-GROUP:".length() );
			if (columns[12+3*i].length() > 0)
				fields[IDX_INV_NOTES] += columns[12+3*i] + LINE_SEPARATOR;
			fields[IDX_R1_STATUS + i] = columns[13+3*i].substring( "RX-STATUS:".length() );
		}
		
		return fields;
	}
	
	/** Old STARS meter table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(INV_ID)
	 * 3		DEVICE_NAME
	 * 4		INV_NOTES
	 * 5		INSTALL_DATE
	 * 6		INV_NOTES		
	 */
	private String[] parseStarsMeter(String line) throws Exception {
		String[] fields = prepareFields( NUM_INV_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 6)
			throw new WebClientException( "Incorrect number of fields in meter file" );

		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_INV_ID] = columns[1];
		fields[IDX_DEVICE_NAME] = columns[2];
		if (columns[3].length() > 0)
			fields[IDX_INV_NOTES] += "Technician: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_INSTALL_DATE] = columns[4];
		if (columns[5].length() > 0)
			fields[IDX_INV_NOTES] += columns[5];
		
		return fields;
	}
	
	/** Old STARS load info table
	 * COL_NUM:	COL_NAME
	 * 1		APP_ID
	 * 2		(ACCOUNT_ID)
	 * 3		(INV_ID)
	 * 4		RELAY_NUM
	 * 5		APP_DESC
	 * 6		APP_TYPE
	 * 7		APP_NOTES
	 * 8		MANUFACTURER
	 * 9-11		APP_NOTES
	 * 12		AVAIL_FOR_CTRL
	 * 13		YEAR_MADE
	 * 14-21	APP_NOTES
	 */
	private String[] parseStarsLoadInfo(String line, Hashtable userLabels) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 21)
			throw new WebClientException( "Incorrect number of fields in load info file" );
		
		fields[IDX_APP_ID] = columns[0];
		fields[IDX_ACCOUNT_ID] = columns[1];
		fields[IDX_INV_ID] = columns[2];
		fields[IDX_RELAY_NUM] = columns[3];
		fields[IDX_APP_DESC] = columns[4];
		fields[IDX_APP_TYPE] = columns[5];
		if (columns[6].length() > 0)
			fields[IDX_APP_NOTES] += "EquipCode: " + columns[6] + LINE_SEPARATOR;
		fields[IDX_MANUFACTURER] = columns[7];
		if (columns[8].length() > 0 && userLabels.get(LABEL_CONTRACTOR1) != null)
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_CONTRACTOR1) + ": " + columns[8] + LINE_SEPARATOR;
		if (columns[9].length() > 0 && userLabels.get(LABEL_CONTRACTOR2) != null)
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_CONTRACTOR2) + ": " + columns[9] + LINE_SEPARATOR;
		if (columns[10].length() > 0)
			fields[IDX_APP_NOTES] += "WarrantyInfo: " + columns[10] + LINE_SEPARATOR;
		fields[IDX_AVAIL_FOR_CTRL] = columns[11];
		fields[IDX_YEAR_MADE] = columns[12];
		if (columns[13].length() > 0 && userLabels.get(LABEL_LI_CHAR1) != null) {
			String text = columns[13].substring( columns[13].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_CHAR1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[14].length() > 0 && userLabels.get(LABEL_LI_DROPBOX1) != null) {
			String text = columns[14].substring( columns[14].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_DROPBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[15].length() > 0 && userLabels.get(LABEL_LI_DROPBOX2) != null) {
			String text = columns[15].substring( columns[15].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_DROPBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[16].length() > 0 && userLabels.get(LABEL_LI_DROPBOX3) != null) {
			String text = columns[16].substring( columns[16].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_DROPBOX3) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[17].length() > 0 && userLabels.get(LABEL_LI_CHECKBOX1) != null) {
			String text = columns[17].substring( columns[17].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_CHECKBOX1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[18].length() > 0 && userLabels.get(LABEL_LI_CHECKBOX2) != null) {
			String text = columns[18].substring( columns[18].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_CHECKBOX2) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[19].length() > 0 && userLabels.get(LABEL_LI_NUMERIC1) != null) {
			String text = columns[19].substring( columns[19].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_NUMERIC1) + ": " + text + LINE_SEPARATOR;
		}
		if (columns[20].length() > 0 && userLabels.get(LABEL_LI_NUMERIC2) != null) {
			String text = columns[20].substring( columns[20].indexOf(':')+1 );
			fields[IDX_APP_NOTES] += userLabels.get(LABEL_LI_NUMERIC2) + ": " + text + LINE_SEPARATOR;
		}
		
		return fields;
	}
	
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
	private String[] parseStarsWorkOrder(String line) throws Exception {
		String[] fields = prepareFields( NUM_ORDER_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 20)
			throw new WebClientException( "Incorrect number of fields in work order file" );
		
		fields[IDX_ORDER_NO] = columns[0];
		fields[IDX_ACCOUNT_ID] = columns[1];
		fields[IDX_INV_ID] = columns[2];
		fields[IDX_DATE_REPORTED] = columns[8];
		fields[IDX_DATE_COMPLETED] = columns[9];
		fields[IDX_ORDER_STATUS] = columns[10];
		fields[IDX_ORDER_TYPE] = columns[11];
		fields[IDX_ORDER_DESC] = columns[12] + LINE_SEPARATOR;
		fields[IDX_ACTION_TAKEN] = columns[13] + LINE_SEPARATOR;
		fields[IDX_TIME_SCHEDULED] = columns[14];
		if (fields[IDX_TIME_SCHEDULED].length() > 0)
			fields[IDX_ORDER_DESC] += "Time Scheduled: " + fields[IDX_TIME_SCHEDULED];
		fields[IDX_DATE_SCHEDULED] = columns[15];
		if (columns[16].length() > 0)
			fields[IDX_ACTION_TAKEN] += columns[16] + LINE_SEPARATOR;
		if (columns[17].length() > 0)
			fields[IDX_ACTION_TAKEN] += "Overtime Hours: " + columns[17] + LINE_SEPARATOR;
		if (columns[18].length() > 0)
			fields[IDX_ACTION_TAKEN] += "Technician: " + columns[18];
		fields[IDX_ORDER_CONTRACTOR] = columns[19];
		
		return fields;
	}
	
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
	private String[] parseStarsResidenceInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_RES_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 21)
			throw new WebClientException( "Incorrect number of fields in residence info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_OWNERSHIP_TYPE] = columns[1];
		fields[IDX_RES_TYPE] = columns[2];
		fields[IDX_CONSTRUCTION_TYPE] = columns[3];
		fields[IDX_DECADE_BUILT] = columns[4];
		fields[IDX_SQUARE_FEET] = columns[5];
		fields[IDX_NUM_OCCUPANTS] = columns[6];
		fields[IDX_GENERAL_COND] = columns[7];
		fields[IDX_INSULATION_DEPTH] = columns[8];
		fields[IDX_MAIN_FUEL_TYPE] = columns[9];
		//fields[IDX_RES_NOTES] += "SetBackThermostat: " + columns[10] + LINE_SEPARATOR;
		fields[IDX_MAIN_COOLING_SYS] = columns[11];
		//fields[IDX_RES_NOTES] += "CoolingSystemYearInstalled: " + columns[12] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "CoolingSystemEff.: " + columns[13] + LINE_SEPARATOR;
		fields[IDX_MAIN_HEATING_SYS] = columns[14];
		//fields[IDX_RES_NOTES] += "HeatingSystemYearInstalled: " + columns[15] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "HeatingSystemEfficiency: " + columns[16] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "EnergyAudit: " + columns[17] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "HeatLoss: " + columns[18] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "HeatGain: " + columns[19] + LINE_SEPARATOR;
		//fields[IDX_RES_NOTES] += "EnergyManagementParticipant: " + columns[20];
		
		return fields;
	}
	
	/** Old STARS AC info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4		APP_NOTES
	 * 5		AC_TONNAGE
	 * 6,7		NOTES
	 */
	private String[] parseStarsACInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 7)
			throw new WebClientException( "Incorrect number of fields in AC info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_AC_TONNAGE] = columns[4];
		fields[IDX_APP_NOTES] += "BTU_Hour: " + columns[5] + LINE_SEPARATOR;
		fields[IDX_APP_NOTES] += columns[6];
			
		return fields;
	}
	
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
	 * 9		APP_NOTES
	 */
	private String[] parseStarsWHInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in WH info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_WH_NUM_GALLONS] = columns[4];
		fields[IDX_WH_NUM_ELEMENTS] = columns[5];
		fields[IDX_WH_ENERGY_SRC] = columns[6];
		fields[IDX_APP_NOTES] += "Location: " + columns[7] + LINE_SEPARATOR;
		fields[IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
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
	private String[] parseStarsGeneratorInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 8)
			throw new WebClientException( "Incorrect number of fields in generator info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_NOTES] += "StandbyKW: " + columns[2] + LINE_SEPARATOR;
		fields[IDX_GEN_FUEL_CAP] = columns[3];
		fields[IDX_GEN_START_DELAY] = columns[4];
		fields[IDX_GEN_CAPACITY] = columns[5];
		fields[IDX_GEN_TRAN_SWITCH_MFC] = columns[6];
		fields[IDX_GEN_TRAN_SWITCH_TYPE] = columns[7];
		
		return fields;
	}
	
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
	private String[] parseStarsIrrigationInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 10)
			throw new WebClientException( "Incorrect number of fields in irrigation info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_IRR_TYPE] = columns[4];
		fields[IDX_IRR_ENERGY_SRC] = columns[5];
		fields[IDX_IRR_HORSE_POWER] = columns[6];
		fields[IDX_IRR_METER_VOLT] = columns[7];
		fields[IDX_IRR_METER_LOC] += columns[8];
		fields[IDX_IRR_SOIL_TYPE] = columns[9];
		
		return fields;
	}
	
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
	private String[] parseStarsGrainDryerInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in grain dryer info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_GDRY_TYPE] = columns[4];
		fields[IDX_GDRY_ENERGY_SRC] = columns[5];
		fields[IDX_GDRY_HORSE_POWER] = columns[6];
		fields[IDX_GDRY_HEAT_SRC] = columns[7];
		fields[IDX_GDRY_BIN_SIZE] = columns[8];
		
		return fields;
	}
	
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
	private String[] parseStarsHeatPumpInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in heat pump info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_HP_TYPE] = columns[4];
		fields[IDX_HP_SIZE] = columns[5];
		fields[IDX_HP_STANDBY_SRC] = columns[6];
		fields[IDX_HP_RESTART_DELAY] = columns[7];
		fields[IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
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
	private String[] parseStarsStorageHeatInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in storage heat info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_SH_TYPE] = columns[4];
		fields[IDX_SH_CAPACITY] = columns[5];
		fields[IDX_SH_RECHARGE_TIME] = columns[6];
		fields[IDX_APP_NOTES] += "ContractHours: " + columns[7] + LINE_SEPARATOR;
		fields[IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
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
	private String[] parseStarsDualFuelInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 9)
			throw new WebClientException( "Incorrect number of fields in dual fuel info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_APP_NOTES] += "PrimarySize: " + columns[4] + LINE_SEPARATOR;
		fields[IDX_DF_2ND_ENERGY_SRC] = columns[5];
		fields[IDX_DF_2ND_CAPACITY] = columns[6];
		fields[IDX_DF_SWITCH_OVER_TYPE] = columns[7];
		fields[IDX_APP_NOTES] += columns[8];
		
		return fields;
	}
	
	/** Old STARS general info table
	 * COL_NUM:	COL_NAME
	 * 1		(ACCOUNT_ID)
	 * 2		(APP_ID)
	 * 3		APP_KW
	 * 4,5		APP_NOTES
	 */
	private String[] parseStarsGeneralInfo(String line) throws Exception {
		String[] fields = prepareFields( NUM_APP_FIELDS );
		String[] columns = parseColumns( line );
		if (columns.length != 5)
			throw new WebClientException( "Incorrect number of fields in general info file" );
		
		fields[IDX_ACCOUNT_ID] = columns[0];
		fields[IDX_APP_ID] = columns[1];
		fields[IDX_APP_KW] = columns[2];
		fields[IDX_APP_NOTES] += "Rebate: " + columns[3] + LINE_SEPARATOR;
		fields[IDX_APP_NOTES] += columns[4];
		
		return fields;
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
			
			String[] custLines = ServerUtils.readFile( custFile );
			String[] servInfoLines = ServerUtils.readFile( servInfoFile, false );
			String[] invLines = ServerUtils.readFile( invFile );
			String[] recvrLines = ServerUtils.readFile( recvrFile, false );
			String[] meterLines = ServerUtils.readFile( meterFile, false );
			String[] loadInfoLines = ServerUtils.readFile( loadInfoFile );
			String[] acInfoLines = ServerUtils.readFile( acInfoFile, false );
			String[] whInfoLines = ServerUtils.readFile( whInfoFile, false );
			String[] genInfoLines = ServerUtils.readFile( genInfoFile, false );
			String[] irrInfoLines = ServerUtils.readFile( irrInfoFile, false );
			String[] gdryInfoLines = ServerUtils.readFile( gdryInfoFile, false );
			String[] hpInfoLines = ServerUtils.readFile( hpInfoFile, false );
			String[] shInfoLines = ServerUtils.readFile( shInfoFile, false );
			String[] dfInfoLines = ServerUtils.readFile( dfInfoFile, false );
			String[] genlInfoLines = ServerUtils.readFile( genlInfoFile, false );
			String[] workOrderLines = ServerUtils.readFile( workOrderFile );
			String[] resInfoLines = ServerUtils.readFile( resInfoFile );
			
			Hashtable preprocessedData = (Hashtable) session.getAttribute(PREPROCESSED_DATA);
			if (preprocessedData != null) {
				// Clear up the old data from memory
				ArrayList acctFieldsList = (ArrayList) preprocessedData.get("CustomerAccount");
				if (acctFieldsList != null) {
					acctFieldsList.clear();
					preprocessedData.remove( "CustomerAccount" );
				}
				
				ArrayList invFieldsList = (ArrayList) preprocessedData.get("Inventory");
				if (invFieldsList != null) {
					invFieldsList.clear();
					preprocessedData.remove( "Inventory" );
				}
				 
				ArrayList appFieldsList = (ArrayList) preprocessedData.get("Appliance");
				if (appFieldsList != null) {
					appFieldsList.clear();
					preprocessedData.remove( "Appliance" );
				}
				
				ArrayList orderFieldsList = (ArrayList) preprocessedData.get("WorkOrder");
				if (orderFieldsList != null) {
					orderFieldsList.clear();
					preprocessedData.remove( "WorkOrder" );
				}
				
				ArrayList resFieldsList = (ArrayList) preprocessedData.get("CustomerResidence");
				if (resFieldsList != null) {
					resFieldsList.clear();
					preprocessedData.remove( "CustomerResidence" );
				}
			}
			else
				preprocessedData = new Hashtable();
			
			Hashtable userLabels = (Hashtable) session.getAttribute( USER_LABELS );
			if (userLabels == null) userLabels = new Hashtable();
			
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
			for (int i = 0; i < LIST_NAMES.length; i++) {
				valueIDMaps[i] = (TreeMap) preprocessedData.get( LIST_NAMES[i][0] );
				if (valueIDMaps[i] == null) valueIDMaps[i] = new TreeMap();
			}
			
			if (custLines != null) {
				if (custFile.getName().equals("customer.map")) {
					// customer.map file format: import_account_id,db_account_id
					// Notice: the parsed lines have line# inserted at the beginning
					Hashtable acctIDMap = new Hashtable();
					
					for (int i = 0; i < custLines.length; i++) {
						String[] fields = custLines[i].split(",");
						if (fields.length != 2)
							throw new WebClientException("Invalid format of file '" + custFile.getPath() + "'");
						acctIDMap.put( Integer.valueOf(fields[0]), Integer.valueOf(fields[1]) );
					}
					
					preprocessedData.put("CustomerAccountMap", acctIDMap);
				}
				else {
					for (int i = 0; i < custLines.length; i++) {
						if (custLines[i].trim().equals("") || custLines[i].charAt(0) == '#')
							continue;
						
						String[] fields = parseStarsCustomer( custLines[i] );
						fields[IDX_LINE_NUM] = String.valueOf(i + 1);
						acctFieldsList.add( fields );
						acctIDFields.put( fields[IDX_ACCOUNT_ID], fields );
					}
				}
				
				preprocessedData.put("CustomerFilePath", custFile.getParent());
			}
			else {
				if (preprocessedData.get("CustomerAccountMap") == null)
					throw new WebClientException("No customer information found. If you have already imported the customer file, select the generated 'customer.map' file in the 'Customer File' field");
			}
			
			if (servInfoLines != null) {
				for (int i = 0; i < servInfoLines.length; i++) {
					String[] fields = parseStarsServiceInfo( servInfoLines[i], userLabels );
					String[] custFields = (String[]) acctIDFields.get( fields[IDX_ACCOUNT_ID] );
					
					if (custFields != null) {
						for (int j = 0; j < custFields.length; j++)
							if (fields[j].length() > 0) custFields[j] = fields[j];
						
						for (int j = 0; j < SERVINFO_LIST_FIELDS.length; j++) {
							int listIdx = SERVINFO_LIST_FIELDS[j][0];
							int fieldIdx = SERVINFO_LIST_FIELDS[j][1];
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (invLines != null) {
				for (int i = 0; i < invLines.length; i++) {
					if (invLines[i].trim().equals("") || invLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsInventory( invLines[i], userLabels );
					fields[IDX_LINE_NUM] = String.valueOf(i + 1);
					
					if (fields[IDX_DEVICE_TYPE].equals("") || fields[IDX_DEVICE_TYPE].equals("-1"))
						continue;
					
					invFieldsList.add( fields );
					invIDFields.put( fields[IDX_INV_ID], fields );
					
					for (int j = 0; j < INV_LIST_FIELDS.length; j++) {
						int listIdx = INV_LIST_FIELDS[j][0];
						int fieldIdx = INV_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			if (recvrLines != null) {
				if (recvrFile.getName().equals("hwconfig.map")) {
					// hwconfig.map file format: import_inv_id,relay1_db_app_id,relay2_db_app_id,relay3_db_app_id
					Hashtable appIDMap = new Hashtable();
					
					for (int i = 0; i < recvrLines.length; i++) {
						String[] fields = recvrLines[i].split(",");
						if (fields.length != 4)
							throw new WebClientException("Invalid format of file '" + recvrFile.getPath() + "'");
						
						Integer invID = Integer.valueOf( fields[0] );
						int[] appIDs = new int[3];
						for (int j = 0; j < 3; j++)
							appIDs[j] = Integer.parseInt( fields[j+1] );
						
						appIDMap.put( invID, appIDs );
					}
					
					preprocessedData.put("HwConfigAppMap", appIDMap);
				}
				else {
					for (int i = 0; i < recvrLines.length; i++) {
						String[] fields = parseStarsReceiver( recvrLines[i] );
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
								if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
									Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
									if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
								}
							}
						}
					}
				}
			}
			
			if (meterLines != null) {
				for (int i = 0; i < meterLines.length; i++) {
					String[] fields = parseStarsMeter( meterLines[i] );
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
				if (recvrLines == null && preprocessedData.get("HwConfigAppMap") == null)
					throw new WebClientException("No hardware config information found. If you have already imported the receiver file, select the generated 'hwconfig.map' file in the 'Receiver File' field.");
				
				for (int i = 0; i < loadInfoLines.length; i++) {
					if (loadInfoLines[i].trim().equals("") || loadInfoLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsLoadInfo( loadInfoLines[i], userLabels );
					fields[IDX_LINE_NUM] = String.valueOf(i + 1);
					appFieldsList.add( fields );
					appIDFields.put( fields[IDX_APP_ID], fields );
					
					for (int j = 0; j < APP_LIST_FIELDS.length; j++) {
						int listIdx = APP_LIST_FIELDS[j][0];
						int fieldIdx = APP_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			if (acInfoLines != null) {
				for (int i = 0; i < acInfoLines.length; i++) {
					String[] fields = parseStarsACInfo( acInfoLines[i] );
					String[] appFields = (String[]) appIDFields.get( fields[IDX_APP_ID] );
					
					if (appFields != null) {
						// Set the appliance category field (which will be used to decide the appliance type later)
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (whInfoLines != null) {
				for (int i = 0; i < whInfoLines.length; i++) {
					String[] fields = parseStarsWHInfo( whInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (genInfoLines != null) {
				for (int i = 0; i < genInfoLines.length; i++) {
					String[] fields = parseStarsGeneratorInfo( genInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (irrInfoLines != null) {
				for (int i = 0; i < irrInfoLines.length; i++) {
					String[] fields = parseStarsIrrigationInfo( irrInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (gdryInfoLines != null) {
				for (int i = 0; i < gdryInfoLines.length; i++) {
					String[] fields = parseStarsGrainDryerInfo( gdryInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (hpInfoLines != null) {
				for (int i = 0; i < hpInfoLines.length; i++) {
					String[] fields = parseStarsHeatPumpInfo( hpInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (shInfoLines != null) {
				for (int i = 0; i < shInfoLines.length; i++) {
					String[] fields = parseStarsStorageHeatInfo( shInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (dfInfoLines != null) {
				for (int i = 0; i < dfInfoLines.length; i++) {
					String[] fields = parseStarsDualFuelInfo( dfInfoLines[i] );
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
							if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
								Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
								if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
							}
						}
					}
				}
			}
			
			if (genlInfoLines != null) {
				for (int i = 0; i < genlInfoLines.length; i++) {
					String[] fields = parseStarsGeneralInfo( genlInfoLines[i] );
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
				for (int i = 0; i < workOrderLines.length; i++) {
					if (workOrderLines[i].trim().equals("") || workOrderLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsWorkOrder( workOrderLines[i] );
					fields[IDX_LINE_NUM] = String.valueOf(i + 1);
					orderFieldsList.add( fields );
					
					for (int j = 0; j < ORDER_LIST_FIELDS.length; j++) {
						int listIdx = ORDER_LIST_FIELDS[j][0];
						int fieldIdx = ORDER_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
					}
				}
			}
			
			if (resInfoLines != null) {
				for (int i = 0; i < resInfoLines.length; i++) {
					if (resInfoLines[i].trim().equals("") || resInfoLines[i].charAt(0) == '#')
						continue;
					
					String[] fields = parseStarsResidenceInfo( resInfoLines[i] );
					fields[IDX_LINE_NUM] = String.valueOf(i + 1);
					resFieldsList.add( fields );
					
					for (int j = 0; j < RES_LIST_FIELDS.length; j++) {
						int listIdx = RES_LIST_FIELDS[j][0];
						int fieldIdx = RES_LIST_FIELDS[j][1];
						if (!valueIDMaps[listIdx].containsKey( fields[fieldIdx] )) {
							Integer entryID = getTextEntryID( fields[fieldIdx], LIST_NAMES[listIdx][0], energyCompany );
							if (entryID != null) valueIDMaps[listIdx].put( fields[fieldIdx], entryID );
						}
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
			
			for (int i = 0; i < LIST_NAMES.length; i++) {
				if (valueIDMaps[i].containsValue(ZERO) && LIST_NAMES[i][1].length() > 0)
					unassignedLists.put( LIST_NAMES[i][0], new Boolean(true) );
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
		
		StarsEnergyCompanySettings ecSettings =
				(StarsEnergyCompanySettings) user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
		Hashtable selectionListTable = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
		
		String listName = req.getParameter("ListName");
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
		else if (entryTexts != null) {
			try {
				if (listName.equals("ServiceCompany")) {
					for (int i = 0; i < entryTexts.length; i++) {
						LiteServiceCompany liteCompany = StarsAdmin.createServiceCompany( entryTexts[i], energyCompany );
						valueIDMap.put( assignedValues.get(i), new Integer(liteCompany.getCompanyID()) );
					}
				}
				else {
					YukonSelectionList cList = energyCompany.getYukonSelectionList(listName, false);
					ArrayList entries = cList.getYukonListEntries();
					
					Object[][] entryData = new Object[entries.size() + entryTexts.length][];
					for (int i = 0; i < entries.size(); i++) {
						YukonListEntry cEntry = (YukonListEntry) entries.get(i);
						entryData[i] = new Object[3];
						entryData[i][0] = new Integer( cEntry.getEntryID() );
						entryData[i][1] = cEntry.getEntryText();
						entryData[i][2] = new Integer( cEntry.getYukonDefID() );
					}
					
					for (int i = 0; i < entryTexts.length; i++) {
						entryData[entries.size()+i] = new Object[3];
						entryData[entries.size()+i][0] = ZERO;
						entryData[entries.size()+i][1] = entryTexts[i];
						entryData[entries.size()+i][2] = ZERO;
					}
					
					StarsAdmin.updateYukonListEntries( cList, entryData, energyCompany );
					
					StarsCustSelectionList starsList = StarsLiteFactory.createStarsCustSelectionList( cList );
					selectionListTable.put( starsList.getListName(), starsList );
					
					for (int i = 0; i < assignedValues.size(); i++) {
						for (int j = 0; j < cList.getYukonListEntries().size(); j++) {
							YukonListEntry cEntry = (YukonListEntry) cList.getYukonListEntries().get(j);
							if (cEntry.getEntryText().equals(entryTexts[i]) && cEntry.getYukonDefID() == 0) {
								valueIDMap.put( assignedValues.get(i), new Integer(cEntry.getEntryID()) );
								break;
							}
						}
					}
				}
			}
			catch (WebClientException e) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, e.getMessage());
				redirect = referer;
			}
			catch (Exception e) {
				e.printStackTrace();
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Failed to assign import values to selection list");
				redirect = referer;
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
			
			int categoryID = ECUtils.getInventoryCategoryID( Integer.parseInt(fields[IDX_DEVICE_TYPE]), energyCompany );
			if (ECUtils.isLMHardware( categoryID )) {
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
		
		// Wait 5 seconds for the task to finish (or error out), if not, then go to the progress page
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {}
			
			if (task.getStatus() == ImportStarsDataTask.STATUS_FINISHED) {
				session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE, task.getProgressMsg());
				ProgressChecker.removeTask( id );
				return;
			}
			
			if (task.getStatus() == ImportStarsDataTask.STATUS_ERROR) {
				session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, task.getErrorMsg());
				ProgressChecker.removeTask( id );
				return;
			}
		}
		
		session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
		redirect = req.getContextPath() + "/operator/Admin/Progress.jsp?id=" + id;
	}

}
