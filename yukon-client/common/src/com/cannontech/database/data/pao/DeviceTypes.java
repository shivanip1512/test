package com.cannontech.database.data.pao;
/**
 * Insert the type's description here.
 * Creation date: (10/2/2001 1:45:23 PM)
 * @author: 
 */
public interface DeviceTypes
{
	public final static int CCU710A = 	10;
	public final static int CCU711 	= 	11;
	public final static int TCU5000	=	12;
	public final static int TCU5500	=	13;
	public final static int LCU415	=	14;
	public final static int LCULG	=	15;
	public final static int LCU_ER = 16;
	public final static int LCU_T3026 = 17;
	
	public final static int ALPHA_PPLUS	=	20; //replaced ALPHA
	public final static int FULCRUM = 21;  // replaces SCHLUMBERGER
	public final static int VECTRON = 22;	// is a type of SCHLUMBERGER
	public final static int LANDISGYRS4 = 23;
	public final static int DAVISWEATHER = 24;
	public final static int ALPHA_A1	=	25;
	public final static int DR_87	=	26;
	public final static int QUANTUM = 27;
	public final static int SIXNET = 28;

	public final static int REPEATER_800 =	29;
	public final static int MCT310	=	30;
	public final static int MCT318	=	31;
	public final static int MCT360	=	32;
	public final static int MCT370	=	33;
	public final static int MCT240	=	34;
	public final static int MCT248	=	35;
	public final static int MCT250	=	36;
	public final static int MCT210	=	37;
	public final static int REPEATER	=	38;
	public final static int LMT_2	=	39;
	
	public final static int RTUILEX	=	40;
	public final static int RTUWELCO	=	41;
	public final static int DCT_501	=	42;
   public final static int RTU_DNP  =  43;
	
	public final static int TAPTERMINAL	=	50;
	public final static int MCT310ID	=	51;
	public final static int MCT310IL =	52;
	public final static int MCT318L	=	53;
	public final static int MCT213 = 54;
	public final static int WCTP_TERMINAL	=	55;

	public final static int LM_GROUP_EMETCON = 100;
	public final static int LM_GROUP_VERSACOM = 101;
	public final static int LM_GROUP_EXPRESSCOMM = 102;
	public final static int LM_GROUP_RIPPLE = 103;
	public final static int LM_DIRECT_PROGRAM = 104;
	public final static int LM_CURTAIL_PROGRAM = 105;
	public final static int LM_CONTROL_AREA = 106;
	public final static int LM_ENERGY_EXCHANGE_PROGRAM = 107;
	public final static int MACRO_GROUP = 108;
	
	public final static int CAPBANK = 110;
	public final static int CAPBANKCONTROLLER = 111;
	public final static int VIRTUAL_SYSTEM = 112;
	public final static int CBC_FP_2800 = 113;
	public final static int LM_GROUP_POINT = 114;
		
   public final static int DNP_CBC_6510  = 130;
   
      
	public final static int SYSTEM = 200;
	public final static int EDITABLEVERSACOMSERIAL = 210;

	public final static int MCTBROADCAST = 220;

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
	public static final String[] STRING_FULCRUM = {"FULCRUM"}; //replaced Schlumberger
	public static final String[] STRING_VECTRON = {"VECTRON"}; //replaced Schlumberger
	public static final String[] STRING_QUANTUM = {"QUANTUM"}; //replaced Schlumberger
	public static final String[] STRING_LANDISGYR_RS4 = {"LANDIS-GYR S4", "LANDISGYRS4", "LANDIS GYR S4"};
	public static final String[] STRING_DR_87 = {"DR-87", "DR87"};
	public static final String[] STRING_DAVIS_WEATHER = {"DAVIS WEATHER", "DAVISWEATHER"};
	public static final String[] STRING_MCT_318L = {"MCT-318L", "MCT318L"};
	public static final String[] STRING_MCT_310ID = {"MCT-310ID", "MCT310ID"};
	public static final String[] STRING_MCT_310IL = {"MCT-310IL", "MCT310IL"};
	public static final String[] STRING_MCT_310 = {"MCT-310", "MCT310"};
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
	public static final String[] STRING_SYSTEM = {"SYSTEM"};
	public static final String[] STRING_VIRTUAL_SYSTEM = {"VIRTUAL SYSTEM", "VIRTUALSYSTEM"};
	public static final String[] STRING_LM_DIRECT_PROGRAM = {"LM DIRECT PROGRAM"};
	public static final String[] STRING_LM_CURTAIL_PROGRAM = {"LM CURTAIL PROGRAM"};
	public static final String[] STRING_LM_CONTROL_AREA = {"LM CONTROL AREA"};
	public static final String[] STRING_VERSACOM_SERIAL_NUMBER = {"VERSACOM SERIAL #"};
	public static final String[] STRING_LMT_2 = {"LMT-2", "LMT 2"};
	public static final String[] STRING_DCT_501 = {"DCT-501"};
	public static final String[] STRING_CBC_FP_2800 = {"CBC FP-2800", "CBC FP 2800"};
	public static final String[] STRING_REPEATER_800 = {"REPEATER 800"};
	public static final String[] STRING_LM_ENERGY_EXCHANGE_PROGRAM = {"LM ENERGY EXCHANGE", "ENERGY EXCHANGE" };
	public static final String[] STRING_SIXNET = {"SIXNET"};
   public static final String[] STRING_DNP_CBC_6510 = {"CBC 6510", "CBC-6510" };
}