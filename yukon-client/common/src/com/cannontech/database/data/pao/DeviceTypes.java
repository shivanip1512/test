package com.cannontech.database.data.pao;
/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 1:45:23 PM)
 * @author: 
 */
public interface DeviceTypes extends TypeBase
{	 
	public final static int CCU710A       = DEVICE_OFFSET + 1;
	public final static int CCU711        = DEVICE_OFFSET + 2;
	public final static int TCU5000       = DEVICE_OFFSET + 3;
	public final static int TCU5500       = DEVICE_OFFSET + 4;
	public final static int LCU415        = DEVICE_OFFSET + 5;
	public final static int LCULG         = DEVICE_OFFSET + 6;
	public final static int LCU_ER        = DEVICE_OFFSET + 7;
	public final static int LCU_T3026     = DEVICE_OFFSET + 8;	
	public final static int ALPHA_PPLUS   = DEVICE_OFFSET + 9; //replaced ALPHA
	public final static int FULCRUM       = DEVICE_OFFSET + 10;  // replaces SCHLUMBERGER
	public final static int VECTRON       = DEVICE_OFFSET + 11;	// is a type of SCHLUMBERGER
	public final static int LANDISGYRS4   = DEVICE_OFFSET + 12;
	public final static int DAVISWEATHER  = DEVICE_OFFSET + 13;
	public final static int ALPHA_A1      = DEVICE_OFFSET + 14;
	public final static int DR_87         = DEVICE_OFFSET + 15;
	public final static int QUANTUM       = DEVICE_OFFSET + 16;
	public final static int SIXNET        = DEVICE_OFFSET + 17;
	public final static int REPEATER_800  = DEVICE_OFFSET + 18;
	public final static int MCT310        = DEVICE_OFFSET + 19;
	public final static int MCT318        = DEVICE_OFFSET + 20;
	public final static int MCT360        = DEVICE_OFFSET + 21;
	public final static int MCT370        = DEVICE_OFFSET + 22;
	public final static int MCT240        = DEVICE_OFFSET + 23;
	public final static int MCT248        = DEVICE_OFFSET + 24;
	public final static int MCT250        = DEVICE_OFFSET + 25;
	public final static int MCT210        = DEVICE_OFFSET + 26;
	public final static int REPEATER      = DEVICE_OFFSET + 27;
	public final static int LMT_2         = DEVICE_OFFSET + 28;	
	public final static int RTUILEX       = DEVICE_OFFSET + 29;
	public final static int RTUWELCO      = DEVICE_OFFSET + 30;
	public final static int DCT_501       = DEVICE_OFFSET + 31;
   public final static int RTU_DNP       = DEVICE_OFFSET + 32;
   		
	public final static int TAPTERMINAL    = DEVICE_OFFSET + 33;
	public final static int MCT310ID       = DEVICE_OFFSET + 34;
	public final static int MCT310IL       = DEVICE_OFFSET + 35;
	public final static int MCT318L        = DEVICE_OFFSET + 36;
	public final static int MCT213         = DEVICE_OFFSET + 37;
	public final static int WCTP_TERMINAL  = DEVICE_OFFSET + 38;
   public final static int MCT310CT       = DEVICE_OFFSET + 39;
   public final static int MCT310IM       = DEVICE_OFFSET + 40;

	public final static int LM_GROUP_EMETCON             = DEVICE_OFFSET + 41;
	public final static int LM_GROUP_VERSACOM            = DEVICE_OFFSET + 42;
	public final static int LM_GROUP_EXPRESSCOMM         = DEVICE_OFFSET + 43;
	public final static int LM_GROUP_RIPPLE              = DEVICE_OFFSET + 44;
	public final static int LM_DIRECT_PROGRAM            = DEVICE_OFFSET + 45;
	public final static int LM_CURTAIL_PROGRAM           = DEVICE_OFFSET + 46;
	public final static int LM_CONTROL_AREA              = DEVICE_OFFSET + 47;
	public final static int LM_ENERGY_EXCHANGE_PROGRAM   = DEVICE_OFFSET + 48;
	public final static int MACRO_GROUP                  = DEVICE_OFFSET + 49;	
	public final static int CAPBANK                      = DEVICE_OFFSET + 50;
	public final static int CAPBANKCONTROLLER            = DEVICE_OFFSET + 51;
	public final static int VIRTUAL_SYSTEM               = DEVICE_OFFSET + 52;
	public final static int CBC_FP_2800                  = DEVICE_OFFSET + 53;
	public final static int LM_GROUP_POINT               = DEVICE_OFFSET + 54;		
   public final static int DNP_CBC_6510                 = DEVICE_OFFSET + 55;
	public final static int SYSTEM                       = DEVICE_OFFSET + 56;
	public final static int EDITABLEVERSACOMSERIAL       = DEVICE_OFFSET + 57;
	public final static int MCTBROADCAST                 = DEVICE_OFFSET + 58;
   public final static int ION_7700                     = DEVICE_OFFSET + 59;
   public final static int ION_8300                     = DEVICE_OFFSET + 60;
   public final static int ION_7330                     = DEVICE_OFFSET + 61;

   public final static int RTU_DART                     = DEVICE_OFFSET + 62;
   public final static int MCT310IDL                    = DEVICE_OFFSET + 63;
	public final static int LM_GROUP_MCT					  = DEVICE_OFFSET + 64;
	
	public final static int MCT410IL	 			= DEVICE_OFFSET + 65;
	public final static int TRANSDATA_MARKV   		= DEVICE_OFFSET + 66;
	
	public final static int LM_GROUP_SA305			= DEVICE_OFFSET + 67;
	public final static int LM_GROUP_SA205			= DEVICE_OFFSET + 68;
	public final static int LM_GROUP_SADIGITAL		= DEVICE_OFFSET + 69;
	public final static int LM_GROUP_GOLAY			= DEVICE_OFFSET + 70;
	
	public final static int SERIES_5_LMI			= DEVICE_OFFSET + 71;
	public final static int RTC						= DEVICE_OFFSET + 72;
	
	//not actually a device, but useful to throw it in here
	public final static int LM_SCENARIO				= DEVICE_OFFSET + 73;
	
	public final static int KV 						= DEVICE_OFFSET + 74;
	public final static int KVII					= DEVICE_OFFSET + 75;
	
	public final static int RTM						= DEVICE_OFFSET + 76;
	
	public final static int CBC_EXPRESSCOM			= DEVICE_OFFSET + 77;
	
	public final static int SENTINEL				= DEVICE_OFFSET + 78;
	public final static int ALPHA_A3				= DEVICE_OFFSET + 79;
	public final static int MCT470					= DEVICE_OFFSET + 80;
	public final static int MCT410CL	 			= DEVICE_OFFSET + 81;
	
	public final static int CBC_7010				= DEVICE_OFFSET + 82;
	
	//***IMPORTANT ** If you add a deviceType, please update the total device type count!
	public final static int DEVICE_TYPES_COUNT = 82;
	

	//all the possible text representation of each device type.
	//If you want the string of any type, just access the zeroth element, example: STRING_CCU_710[0]
	public static final String[] STRING_CCU_710 = {"CCU-710A", "CCU710A"};	
	public static final String[] STRING_CCU_711 = {"CCU-711", "CCU711"};
	public static final String[] STRING_TCU_5000 = {"TCU-5000", "TCU5000"};
	public static final String[] STRING_TCU_5500 = {"TCU-5500", "TCU5500"};
	public static final String[] STRING_LCU_415 = {"LCU-415", "LCU415"};
	public static final String[] STRING_LCU_LG = {"LCU-LG", "LCULG"};
	public static final String[] STRING_LCU_T3026 = {"LCU-T3026", "LCUT3026"};
	public static final String[] STRING_LCU_ER = {"LCU-EASTRIVER"};
	public static final String[] STRING_ALPHA_POWERPLUS = {"ALPHA POWER PLUS", "ALPHA METER"};
	public static final String[] STRING_ALPHA_A1 = {"ALPHA A1"};
	public static final String[] STRING_TRANSDATA_MARKV = {"TRANSDATA MARK-V"};
	public static final String[] STRING_FULCRUM = {"FULCRUM"}; //replaced Schlumberger
	public static final String[] STRING_VECTRON = {"VECTRON"}; //replaced Schlumberger
	public static final String[] STRING_QUANTUM = {"QUANTUM"}; //replaced Schlumberger
	public static final String[] STRING_KV = {"KV"};
	public static final String[] STRING_KVII = {"KV2", "KVII"};
	public static final String[] STRING_LANDISGYR_RS4 = {"LANDIS-GYR S4", "LANDISGYRS4", "LANDIS GYR S4"};
	public static final String[] STRING_DR_87 = {"DR-87", "DR87"};
	public static final String[] STRING_DAVIS_WEATHER = {"DAVIS WEATHER", "DAVISWEATHER"};
	public static final String[] STRING_MCT_318L = {"MCT-318L", "MCT318L"};
	public static final String[] STRING_MCT_310ID = {"MCT-310ID", "MCT310ID"};
	public static final String[] STRING_MCT_310IDL = {"MCT-310IDL", "MCT310IDL"};
	public static final String[] STRING_MCT_310IL = {"MCT-310IL", "MCT310IL"};   
   	public static final String[] STRING_MCT_310CT = {"MCT-310CT", "MCT310CT"};
   	public static final String[] STRING_MCT_310IM = {"MCT-310IM", "MCT310IM"};   
	public static final String[] STRING_MCT_310 = {"MCT-310", "MCT310"};
	public static final String[] STRING_MCT_410IL = {"MCT-410IL", "MCT410IL", "MCT-410iLE", "MCT-410 kWh Only"};
	public static final String[] STRING_MCT_410CL = {"MCT-410CL"};
	
	public static final String[] STRING_MCT_470 = {"MCT-470", "MCT470"};
	public static final String[] STRING_MCT_318 = {"MCT-318", "MCT318"};
	public static final String[] STRING_MCT_360 = {"MCT-360", "MCT360"};
	public static final String[] STRING_MCT_370 = {"MCT-370", "MCT370"};
	public static final String[] STRING_MCT_240 = {"MCT-240", "MCT240"};
	public static final String[] STRING_MCT_248 = {"MCT-248", "MCT248"};
	public static final String[] STRING_MCT_250 = {"MCT-250", "MCT250"};
	public static final String[] STRING_MCT_210 = {"MCT-210", "MCT210"};
	public static final String[] STRING_MCT_213 = {"MCT-213", "MCT213"};
	public static final String[] STRING_MCT_BROADCAST = {"MCT Broadcast", "MCT-Broadcast", "MCT_Broadcast", "MCT_Broadcast"};
	public static final String[] STRING_REPEATER = {"REPEATER", "REPEATER 900"};   
	public static final String[] STRING_RTU_ILEX = {"RTU-ILEX", "RTUILEX"};
   	public static final String[] STRING_RTU_DNP = {"RTU-DNP", "RTUDNP"};
   	public static final String[] STRING_RTU_DART = {"RTU-DART", "RTUDART"};
	public static final String[] STRING_RTU_WELCO = {"RTU-WELCO", "RTUWELCO"};
	public static final String[] STRING_TAP_TERMINAL = {"TAP TERMINAL", "TAPTERMINAL", "PAGING TAP TERMINAL"};
	public static final String[] STRING_WCTP_TERMINAL = {"WCTP TERMINAL"};
	public static final String[] STRING_EMETCON_GROUP = {"EMETCON GROUP"};
	public static final String[] STRING_VERSACOM_GROUP = {"VERSACOM GROUP"};
	public static final String[] STRING_EXPRESSCOMM_GROUP = {"EXPRESSCOM GROUP"};
	public static final String[] STRING_POINT_GROUP = {"POINT GROUP"};
	public static final String[] STRING_RIPPLE_GROUP = {"RIPPLE GROUP"};
	public static final String[] STRING_MACRO_GROUP = {"MACRO GROUP"};
	public static final String[] STRING_CAP_BANK = {"CAP BANK", "CAPBANK"};
	public static final String[] STRING_CAP_BANK_CONTROLLER = { "CBC Versacom", "CBC", "CAPBANKCONTROLLER" };
	public static final String[] STRING_CBC_EXPRESSCOM = { "CBC Expresscom" };
	public static final String[] STRING_SYSTEM = {"SYSTEM"};
	public static final String[] STRING_VIRTUAL_SYSTEM = {"VIRTUAL SYSTEM", "VIRTUALSYSTEM"};
	public static final String[] STRING_LM_DIRECT_PROGRAM = {"LM DIRECT PROGRAM"};
	public static final String[] STRING_LM_CURTAIL_PROGRAM = {"LM CURTAIL PROGRAM"};
	public static final String[] STRING_LM_CONTROL_AREA = {"LM CONTROL AREA"};
	public static final String[] STRING_LM_SCENARIO = {"LMSCENARIO"};
	public static final String[] STRING_VERSACOM_SERIAL_NUMBER = {"VERSACOM SERIAL #"};
	public static final String[] STRING_LMT_2 = {"LMT-2", "LMT 2"};
	public static final String[] STRING_DCT_501 = {"DCT-501"};
	public static final String[] STRING_CBC_FP_2800 = {"CBC FP-2800", "CBC FP 2800"};
	public static final String[] STRING_REPEATER_800 = {"REPEATER 800"};
	public static final String[] STRING_LM_ENERGY_EXCHANGE_PROGRAM = {"LM ENERGY EXCHANGE", "ENERGY EXCHANGE" };
	public static final String[] STRING_SIXNET = {"SIXNET"};
   	public static final String[] STRING_DNP_CBC_6510 = {"CBC 6510", "CBC-6510" };
   	public static final String[] STRING_ION_7700 = {"ION-7700", "ION 7700" };
   	public static final String[] STRING_ION_8300 = {"ION-8300", "ION 8300" };
   	public static final String[] STRING_ION_7330 = {"ION-7330", "ION 7330" };
	public static final String[] STRING_MCT_GROUP = {"MCT GROUP", "MCT-GROUP"};
	public static final String[] STRING_SA305_GROUP = {"SA-305 Group"};
	public static final String[] STRING_SA205_GROUP = {"SA-205 Group"};
	public static final String[] STRING_SADIGITAL_GROUP = {"SA-Digital Group"};
	public static final String[] STRING_GOLAY_GROUP = {"Golay Group"};
	public static final String[] STRING_SERIES_5_LMI = {"RTU-LMI", "RTU LMI"};
	public static final String[] STRING_RTC = {"RTC"};
	public static final String[] STRING_RTM = {"RTM"};
	public static final String[] STRING_SENTINEL = {"SENTINEL"};
	public static final String[] STRING_ALPHA_A3 = {"ALPHA A3"};
	public static final String[] STRING_CBC_7010 = {"CBC 7010"};
		
}