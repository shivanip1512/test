package com.cannontech.common.constants;

/**
* Enumeration of predefined YukonRole rows (roles).
 * @author alauinger
 */
public interface RoleTypes {

	// Client category
	public static final int POINT_ID_EDIT = -1;
	public static final int DBEDITOR_CORE = -2;
	public static final int DBEDITOR_LM = -3;
	public static final int DBEDITOR_CAP_CONTROL = -4;
	public static final int DBEDITOR_SYSTEM = -5;
	public static final int DISPATCH_MACHINE = -6;
	public static final int DISPATCH_PORT = -7;
	public static final int PORTER_MACHINE = -8;
	public static final int PORTER_PORT = -9;
	public static final int MACS_MACHINE = -10;
	public static final int MACS_PORT = -11;
	public static final int CAP_CONTROL_MACHINE = -12;
	public static final int CAP_CONTROL_PORT = -13;
	public static final int LOADCONTROL_MACHINE = -14;
	public static final int LOADCONTROL_PORT = -15;
	public static final int LOADCONTROL_EDIT = -16;
	public static final int MACS_EDIT = -17;
	public static final int TDC_EXPRESS = -18;
	public static final int TDC_MAX_ROWS = -19;
	public static final int CALC_HISTORICAL_INTERVAL = -20;
	public static final int CALC_HISTORICAL_BASELINE_CALCTIME = -21;
	public static final int CALC_HISTORICAL_DAYSPREVIOUSTOCOLLECT = -22;
	public static final int WEBGRAPH_HOME_DIRECTORY = -23;
	public static final int WEBGRAPH_RUN_INTERVAL = -24;
	public static final int GRAPH_EDIT_GRAPHDEFINITION = -25;
	public static final int DECIMAL_PLACES = -26;
	public static final int CAP_CONTROL_INTERFACE = -27;
	public static final int UTILITY_ID_RANGE = -28;
	public static final int TDC_RIGHTS = -29;
	public static final int CBC_CREATION_NAME = -30;
	public static final int BILLING_WIZ_ACTIVATE = -31;
	public static final int BILLING_INPUT_FILE = -32;
	public static final int CLIENT_LOG_LEVEL = -33;
	public static final int CLIENT_LOG_FILE = -34;
	public static final int CLIENT_TDC_ALRM_CNT = -35;
	public static final int CLIENT_PFACTOR_DEC_PLACES = -36;

	//WebClient category
	public static final int HOME_URL = -100;
	public static final int WEB_OPERATOR = -101;
	public static final int WEB_RESIDENTIAL_CUSTOMER = -102;
	public static final int WEB_CICUSTOMER = -103;

	//Consumer Info category
	public static final int OPERATOR_CONSUMER_INFO = -120;
	public static final int OPERATOR_COMMERCIAL_METERING = -121;
	public static final int OPERATOR_LOADCONTROL = -122;
	public static final int OPERATOR_HARDWARE_INVENTORY = -123;
	public static final int OPERATOR_WORK_ORDERS = -124;
	public static final int OPERATOR_ADMINISTRATION = -125;

	public static final int CICUSTOMER_DIRECT_CONTROL = -140;
	public static final int CICUSTOMER_CURTAILMENT = -141;
	public static final int CICUSTOMER_ENERGY_EXCHANGE = -142;

	public static final int LOADCONTROL_CONTROL_ODDS = -150;
	public static final int CONSUMERINFO_ACCOUNT = -160;
	public static final int CONSUMERINFO_ACCOUNT_GENERAL = -161;
	public static final int CONSUMERINFO_ACCOUNT_CALL_TRACKING = -162;
	public static final int CONSUMERINFO_METERING = -163;
	public static final int CONSUMERINFO_METERING_INTEVAL_DATA = -164;
	public static final int CONSUMERINFO_METERING_USAGE = -165;
	public static final int CONSUMERINFO_PROGRAMS = -166;
	public static final int CONSUMERINFO_PROGRAMS_CONTROL_HISTORY = -167;
	public static final int CONSUMERINFO_PROGRAMS_ENROLLMENT = -168;
	public static final int CONSUMERINFO_PROGRAMS_OPTOUT = -169;
	public static final int CONSUMERINFO_APPLIANCES = -170;
	public static final int CONSUMERINFO_APPLIANCES_CREATE = -171;
	public static final int CONSUMERINFO_HARDWARE = -172;
	public static final int CONSUMERINFO_HARDWARE_CREATE = -173;
	public static final int CONSUMERINFO_WORKORDERS = -174;
	public static final int CONSUMERINFO_ADMIN = -175;
	public static final int CONSUMERINFO_ADMIN_CHANGE_PASSWORD = -176;
	public static final int CONSUMERINFO_THERMOSTAT = -177;
	public static final int CONSUMERINFO_QUESTIONS = -178;

	//Esub category
	public static final int ESUBVIEW = -200;
	public static final int ESUBEDIT = -201;
	public static final int ESUBCONTROL = -202;

	// Used for testing
	public static final int WEB_OPERATOR_SUPER = -1000;

	// WebText category
	// text formerly specified in text.properties, sorta belongs near energy exchange and curtailment stuff
	public static final int TRENDING_DISCLAIMER_TEXT = -9000;
	public static final int ENERGYEXCHANGE_TEXT = -9001;
	public static final int ENERGYEXCHANGE_HEADING_TEXT = -9002;
	public static final int ENERGYEXCHANGE_PHONE_TEXT = -9003;

	public static final int CURTAILMENT_TEXT = -9010;
	public static final int CURTAILMENT_PROVIDER_TEXT = -9011;
}
