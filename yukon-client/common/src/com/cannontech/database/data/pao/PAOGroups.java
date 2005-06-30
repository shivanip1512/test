package com.cannontech.database.data.pao;

//import com.cannontech.database.data.customer.*;

/**
 * This type was created in VisualAge.
 */
public final class PAOGroups implements RouteTypes, PortTypes, DeviceTypes, CapControlTypes
{
	public final static int INVALID = -1;
   	public final static int CAT_DEVICE = 0;
   	public final static int CAT_ROUTE = 1;
   	public final static int CAT_PORT = 2;
   	public final static int CAT_CUSTOMER = 3;
   	public final static int CAT_CAPCONTROL = 4;
   	public final static int CAT_LOADCONTROL = 5;
	  
   	public static final String STRING_CAT_DEVICE = "DEVICE";
   	public static final String STRING_CAT_ROUTE = "ROUTE";
   	public static final String STRING_CAT_PORT = "PORT";
   	public static final String STRING_CAT_CUSTOMER = "CUSTOMER";
   	public static final String STRING_CAT_CAPCONTROL = "CAPCONTROL";
   	public static final String STRING_CAT_LOADMANAGEMENT = "LOADMANAGEMENT";
   	public static final String STRING_INVALID = "(invalid)";


   	//PAOCategorys that have the same PAOClass as the category
   	public static final int CLASS_ROUTE = CAT_ROUTE;
   	public static final int CLASS_PORT = CAT_PORT;
   	public static final int CLASS_CUSTOMER = CAT_CUSTOMER;
   	public static final int CLASS_CAPCONTROL = CAT_CAPCONTROL;
   	public static final int CLASS_LOADMANAGEMENT = CAT_LOADCONTROL;

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static int getCapControlType(String typeString)
{
	if( typeString.equalsIgnoreCase( STRING_CAPCONTROL_SUBBUS ) )
		return CAP_CONTROL_SUBBUS;
	else if( typeString.equalsIgnoreCase( STRING_CAPCONTROL_FEEDER ) )
		return CAP_CONTROL_FEEDER;
	else
		return INVALID;
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static String getCategory( int intCategory ) 
{
	switch( intCategory )
	{
		case CAT_DEVICE:
			return STRING_CAT_DEVICE;
			
		case CAT_PORT:
			return STRING_CAT_PORT;

		case CAT_ROUTE:
			return STRING_CAT_ROUTE;

		case CAT_CUSTOMER:
			return STRING_CAT_CUSTOMER;

		case CAT_CAPCONTROL:
			return STRING_CAT_CAPCONTROL;
			
		case CAT_LOADCONTROL:
			return STRING_CAT_LOADMANAGEMENT;
			
		default:
			return STRING_INVALID;
	}


}
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static int getCategory( String strCategory ) 
{

	if( strCategory.equalsIgnoreCase(STRING_CAT_DEVICE) )
		return CAT_DEVICE;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_PORT) )
		return CAT_PORT;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_ROUTE) )
		return CAT_ROUTE;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_CUSTOMER) )
		return CAT_CUSTOMER;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_CAPCONTROL) )
		return CAT_CAPCONTROL;
	else if( strCategory.equalsIgnoreCase(STRING_CAT_LOADMANAGEMENT) )
		return CAT_LOADCONTROL;
	else
		return INVALID;
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static int getDeviceType(String typeString)
{

   String compareString = typeString.toUpperCase().trim();

   if (isStringDevice(compareString, STRING_CCU_710))
	  return CCU710A;
   else if (isStringDevice(compareString, STRING_CCU_711))
	  return CCU711;
   else if (isStringDevice(compareString, STRING_TCU_5000))
	  return TCU5000;
   else if (isStringDevice(compareString, STRING_TCU_5500))
	  return TCU5500;
   else if (isStringDevice(compareString, STRING_LCU_415))
	  return LCU415;
   else if (isStringDevice(compareString, STRING_LCU_T3026))
	  return LCU_T3026;
   else if (isStringDevice(compareString, STRING_LCU_LG))
	  return LCULG;
   else if (isStringDevice(compareString, STRING_LCU_ER))
	  return LCU_ER;
   else if (isStringDevice(compareString, STRING_ALPHA_POWERPLUS))
	  return ALPHA_PPLUS;
	else if (isStringDevice(compareString, STRING_TRANSDATA_MARKV))
	  return TRANSDATA_MARKV;	  
   else if (isStringDevice(compareString, STRING_ALPHA_A1))
	  return ALPHA_A1;
   else if (isStringDevice(compareString, STRING_FULCRUM))
	  return FULCRUM;
   else if (isStringDevice(compareString, STRING_VECTRON))
	  return VECTRON;
   else if (isStringDevice(compareString, STRING_KV))
	  return KV;
   else if (isStringDevice(compareString, STRING_KVII))
	  return KVII;
   else if (isStringDevice(compareString, STRING_KVII))
	  return KVII;
   else if (isStringDevice(compareString, STRING_LANDISGYR_RS4))
	  return LANDISGYRS4;
   else if (isStringDevice(compareString, STRING_QUANTUM))
   	  return QUANTUM;	
   else if (isStringDevice(compareString, STRING_DAVIS_WEATHER))
	  return DAVISWEATHER;
   else if (isStringDevice(compareString, STRING_MCT_213))
	  return MCT213;
   else if (isStringDevice(compareString, STRING_MCT_318L))
	  return MCT318L;
   else if (isStringDevice(compareString, STRING_MCT_310IL))
	  return MCT310IL;
   else if (isStringDevice(compareString, STRING_MCT_310CT))
     return MCT310CT;
   else if (isStringDevice(compareString, STRING_MCT_310IM))
     return MCT310IM;
   else if (isStringDevice(compareString, STRING_MCT_310ID))
	  return MCT310ID;
   else if (isStringDevice(compareString, STRING_MCT_310IDL))
	  return MCT310IDL;
   else if (isStringDevice(compareString, STRING_MCT_310))
	  return MCT310; 
   else if (isStringDevice(compareString, STRING_MCT_410IL))
	  return MCT410IL;
	else if (isStringDevice(compareString, STRING_MCT_410CL))
	   return MCT410CL;
   else if (isStringDevice(compareString, STRING_MCT_470))
      return MCT470;
   else if (isStringDevice(compareString, STRING_MCT_318))
	  return MCT318;
   else if (isStringDevice(compareString, STRING_MCT_360))
	  return MCT360;
   else if (isStringDevice(compareString, STRING_MCT_370))
	  return MCT370;
   else if (isStringDevice(compareString, STRING_MCT_240))
	  return MCT240;
   else if (isStringDevice(compareString, STRING_MCT_248))
	  return MCT248;
   else if (isStringDevice(compareString, STRING_MCT_250))
	  return MCT250;
   else if (isStringDevice(compareString, STRING_MCT_210))
	  return MCT210;
   else if (isStringDevice(compareString, STRING_MCT_BROADCAST))
      return MCTBROADCAST;
   else if (isStringDevice(compareString, STRING_REPEATER))
	  return REPEATER;
   else if (isStringDevice(compareString, STRING_RTU_DNP))
     return RTU_DNP;
   else if (isStringDevice(compareString, STRING_RTU_DART))
     return RTU_DART;
   else if (isStringDevice(compareString, STRING_ION_7700))
     return ION_7700;
   else if (isStringDevice(compareString, STRING_ION_7330))
     return ION_7330;
   else if (isStringDevice(compareString, STRING_ION_8300))
     return ION_8300;
   else if (isStringDevice(compareString, STRING_RTU_ILEX))
	  return RTUILEX;
   else if (isStringDevice(compareString, STRING_RTU_WELCO))
	  return RTUWELCO;
   else if (isStringDevice(compareString, STRING_TAP_TERMINAL))
	  return TAPTERMINAL;
   else if (isStringDevice(compareString, STRING_WCTP_TERMINAL))
	  return WCTP_TERMINAL;
   else if (isStringDevice(compareString, STRING_EMETCON_GROUP))
	  return LM_GROUP_EMETCON;
   else if (isStringDevice(compareString, STRING_SA305_GROUP))
   	  return LM_GROUP_SA305;
   else if (isStringDevice(compareString, STRING_SA205_GROUP))
	  return LM_GROUP_SA205;
   else if (isStringDevice(compareString, STRING_SADIGITAL_GROUP))
	  return LM_GROUP_SADIGITAL;
   else if (isStringDevice(compareString, STRING_GOLAY_GROUP))
	  return LM_GROUP_GOLAY;
   else if (isStringDevice(compareString, STRING_VERSACOM_GROUP))
	  return LM_GROUP_VERSACOM;
	else if (isStringDevice(compareString, STRING_MCT_GROUP))
	  return LM_GROUP_MCT;	  
   else if (isStringDevice(compareString, STRING_EXPRESSCOMM_GROUP))
	  return LM_GROUP_EXPRESSCOMM;
   else if (isStringDevice(compareString, STRING_POINT_GROUP))
	  return LM_GROUP_POINT;
   else if (isStringDevice(compareString, STRING_MACRO_GROUP))
	  return MACRO_GROUP;
   else if (isStringDevice(compareString, STRING_RIPPLE_GROUP))
	  return LM_GROUP_RIPPLE;
   else if (isStringDevice(compareString, STRING_CAP_BANK))
	  return CAPBANK;
   else if (isStringDevice(compareString, STRING_CAP_BANK_CONTROLLER))
	  return CAPBANKCONTROLLER;
   else if (isStringDevice(compareString, STRING_SYSTEM))
	  return SYSTEM;
   else if (isStringDevice(compareString, STRING_VIRTUAL_SYSTEM))
	  return VIRTUAL_SYSTEM;
   else if (isStringDevice(compareString, STRING_LM_DIRECT_PROGRAM))
	  return LM_DIRECT_PROGRAM;
   else if (isStringDevice(compareString, STRING_LM_CURTAIL_PROGRAM))
	  return LM_CURTAIL_PROGRAM;
   else if (isStringDevice(compareString, STRING_LM_ENERGY_EXCHANGE_PROGRAM))
	  return LM_ENERGY_EXCHANGE_PROGRAM;
   else if (isStringDevice(compareString, STRING_LM_CONTROL_AREA))
	  return LM_CONTROL_AREA;
	//not a device, but useful to place it here
	else if (isStringDevice(compareString, STRING_LM_SCENARIO))
	  return LM_SCENARIO;
	else if (isStringDevice(compareString, STRING_VERSACOM_SERIAL_NUMBER))
	  return EDITABLEVERSACOMSERIAL;
   else if (isStringDevice(compareString, STRING_DR_87))
	  return DR_87;
   else if (isStringDevice(compareString, STRING_LMT_2))
	  return LMT_2;
   else if (isStringDevice(compareString, STRING_DCT_501))
	  return DCT_501;
   else if (isStringDevice(compareString, STRING_CBC_FP_2800))
	  return CBC_FP_2800;
   else if (isStringDevice(compareString, STRING_DNP_CBC_6510))
     return DNP_CBC_6510;
   else if (isStringDevice(compareString, STRING_REPEATER_800))
	  return REPEATER_800;
   else if (isStringDevice(compareString, STRING_SIXNET))
	  return SIXNET;
   else if (isStringDevice(compareString, STRING_SERIES_5_LMI))
   	  return SERIES_5_LMI;
   else if (isStringDevice(compareString, STRING_RTC))
   	  return RTC;
   else if (isStringDevice(compareString, STRING_RTM))
   	  return RTM;
	else if (isStringDevice(compareString, STRING_CBC_EXPRESSCOM))
		return CBC_EXPRESSCOM;
	else if (isStringDevice(compareString, STRING_SENTINEL))
	   return SENTINEL;
	else if (isStringDevice(compareString, STRING_ALPHA_A3))
	   return ALPHA_A3;
	else if (isStringDevice(compareString, STRING_CBC_7010))
		return CBC_7010;
	else if (isStringDevice(compareString, STRING_CBC_7020))
		return CBC_7020;
   	else
	  return INVALID;
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static String getPAOTypeString(int type)
{
	switch( type )
	{
		//routes
		case ROUTE_CCU:
			return STRING_CCU;
		case ROUTE_LCU:
			return STRING_LCU;
		case ROUTE_MACRO:
			return STRING_MACRO;
		case ROUTE_TAP_PAGING:
			return STRING_TAP_PAGING;
		case ROUTE_WCTP_TERMINAL:
			return STRING_WCTP_TERMINAL_ROUTE;
		case ROUTE_TCU:
			return STRING_TCU;
		case ROUTE_VERSACOM:
			return STRING_VERSACOM;
		case ROUTE_RTC:
			return STRING_RTC_ROUTE;

		//capcontrol
		case CAP_CONTROL_SUBBUS:
			return STRING_CAPCONTROL_SUBBUS;
		case CAP_CONTROL_FEEDER:
			return STRING_CAPCONTROL_FEEDER;

		//ports
		case LOCAL_DIRECT:
			return STRING_LOCAL_DIRECT;
		case LOCAL_SHARED:
			return STRING_LOCAL_SERIAL;
		case LOCAL_RADIO:
			return STRING_LOCAL_RADIO;
		case LOCAL_DIALUP:
			return STRING_LOCAL_DIALUP;
		case LOCAL_DIALBACK:
			return STRING_LOCAL_DIALBACK;
		case TSERVER_DIRECT:
			return STRING_TERM_SERVER_DIRECT;
		case TSERVER_SHARED:
			return STRING_TERM_SERVER;
		case TSERVER_RADIO:
			return STRING_TERM_SERVER_RADIO;
		case TSERVER_DIALUP:
			return STRING_TERM_SERVER_DIALUP;
		case DIALOUT_POOL:
			return STRING_DIALOUT_POOL;


	  	//devices
	  	case CCU710A:
			return STRING_CCU_710[0];
	  	case CCU711:
		  	return STRING_CCU_711[0];
	  	case TCU5000:
			return STRING_TCU_5000[0];
	  	case TCU5500:
		  	return STRING_TCU_5500[0];
	  	case SERIES_5_LMI:
	  	  	return STRING_SERIES_5_LMI[0];
	  	case RTC:
	  	  	return STRING_RTC[0];
	  	case LCU415:
		  	return STRING_LCU_415[0];
	  	case LCULG:
		  	return STRING_LCU_LG[0];
	  	case LCU_ER:
		  	return STRING_LCU_ER[0];
	  	case LCU_T3026:
		  	return STRING_LCU_T3026[0];
	  	case ALPHA_PPLUS:
		  	return STRING_ALPHA_POWERPLUS[0];
	  	case ALPHA_A1:
		  	return STRING_ALPHA_A1[0];
	  	case FULCRUM:
		  	return STRING_FULCRUM[0];
	  	case VECTRON:
		  	return STRING_VECTRON[0];
	  	case LANDISGYRS4:
		  	return STRING_LANDISGYR_RS4[0];
	  	case QUANTUM:
		  	return STRING_QUANTUM[0];
	  	case DAVISWEATHER:
		  	return STRING_DAVIS_WEATHER[0];
	  	case MCT213:
		  	return STRING_MCT_213[0];
	  	case MCT318L:
		  	return STRING_MCT_318L[0];
	  	case TRANSDATA_MARKV:
	  	  	return STRING_TRANSDATA_MARKV[0];

	  	case MCT310IL:
		  	return STRING_MCT_310IL[0];
     	case MCT310CT:
        	return STRING_MCT_310CT[0];
     	case MCT310IM:
        	return STRING_MCT_310IM[0];
	  	case MCT310ID:
		  	return STRING_MCT_310ID[0];
	  	case MCT310IDL:
		  	return STRING_MCT_310IDL[0];
	  	case MCT310:
		  	return STRING_MCT_310[0];
	  	case MCT318:
		  	return STRING_MCT_318[0];
	  	case MCT360:
		  	return STRING_MCT_360[0];
	  	case MCT370:
		  	return STRING_MCT_370[0];
	  	case MCT240:
		  	return STRING_MCT_240[0];
		  
	  	case MCT248:
		  	return STRING_MCT_248[0];
	  	case MCT250:
		  	return STRING_MCT_250[0];
	  	case MCT210:
		  	return STRING_MCT_210[0];
		  
	  	case MCT410IL:
			return STRING_MCT_410IL[0];
		case MCT410CL:
			return STRING_MCT_410CL[0];
		case MCT470:
			return STRING_MCT_470[0];
		
		case REPEATER:
			return STRING_REPEATER[0];
     	case RTU_DNP:
        	return STRING_RTU_DNP[0];
     	case RTU_DART:
        	return STRING_RTU_DART[0];
     	case ION_7700:
        	return STRING_ION_7700[0];
     	case ION_7330:
        	return STRING_ION_7330[0];
     	case ION_8300:
        	return STRING_ION_8300[0];
	  	case RTUILEX:
		  	return STRING_RTU_ILEX[0];
	  	case RTUWELCO:
		  	return STRING_RTU_WELCO[0];
	  	case RTM:
	  	  	return STRING_RTM[0];
	  	case TAPTERMINAL:
		  	return STRING_TAP_TERMINAL[0];
	  	case WCTP_TERMINAL:
		  	return STRING_WCTP_TERMINAL[0];

	  	case LM_GROUP_EMETCON:
		  	return STRING_EMETCON_GROUP[0];
	  	case LM_GROUP_VERSACOM:
		  	return STRING_VERSACOM_GROUP[0];
	  	case LM_GROUP_EXPRESSCOMM:
		  	return STRING_EXPRESSCOMM_GROUP[0];
	  	case LM_GROUP_SA305:
	  	  	return STRING_SA305_GROUP[0];
	  	case LM_GROUP_SA205:
		  	return STRING_SA205_GROUP[0];
	  	case LM_GROUP_SADIGITAL:
		  	return STRING_SADIGITAL_GROUP[0];
	  	case LM_GROUP_GOLAY:
		  	return STRING_GOLAY_GROUP[0];	  
	  	case LM_GROUP_POINT:
		  	return STRING_POINT_GROUP[0];
	  	case MACRO_GROUP:
		  	return STRING_MACRO_GROUP[0];
	  	case LM_GROUP_RIPPLE:
		  	return STRING_RIPPLE_GROUP[0];
		case LM_GROUP_MCT:
			return STRING_MCT_GROUP[0];

	  	case MCTBROADCAST:
		  	return STRING_MCT_BROADCAST[0];
		  
	  	case CAPBANK:
		  	return STRING_CAP_BANK[0];
	  	case CAPBANKCONTROLLER:
		  	return STRING_CAP_BANK_CONTROLLER[0];
	  	case SYSTEM:
		  	return STRING_SYSTEM[0];

	  	case VIRTUAL_SYSTEM:
		  	return STRING_VIRTUAL_SYSTEM[0];
	  	case LM_DIRECT_PROGRAM:
		  	return STRING_LM_DIRECT_PROGRAM[0];
	  	case LM_CURTAIL_PROGRAM:
		  	return STRING_LM_CURTAIL_PROGRAM[0];
	  	case LM_ENERGY_EXCHANGE_PROGRAM:
		  	return STRING_LM_ENERGY_EXCHANGE_PROGRAM[0];
	  	case LM_CONTROL_AREA:
		  	return STRING_LM_CONTROL_AREA[0];
		case LM_SCENARIO:
			return STRING_LM_SCENARIO[0];

	  	case EDITABLEVERSACOMSERIAL:
		  	return STRING_VERSACOM_SERIAL_NUMBER[0];
	  	case DR_87:
		  	return STRING_DR_87[0];

	  	case LMT_2:
		  	return STRING_LMT_2[0];
	  	case DCT_501:
		  	return STRING_DCT_501[0];
	  	case CBC_FP_2800:
		  	return STRING_CBC_FP_2800[0];
     	case DNP_CBC_6510:
        	return STRING_DNP_CBC_6510[0];
	  	case REPEATER_800:
		  	return STRING_REPEATER_800[0];
	  	case SIXNET:
		  	return STRING_SIXNET[0];
		  
	  	case KV:
	  	  	return STRING_KV[0];
	  	case KVII:
	  	  	return STRING_KVII[0];
	  	case SENTINEL:
	  	  	return STRING_SENTINEL[0];
	  	case ALPHA_A3:
	  	  	return STRING_ALPHA_A3[0];
	  	case CBC_EXPRESSCOM:
			return STRING_CBC_EXPRESSCOM[0];
		case CBC_7010:
			return STRING_CBC_7010[0];
		case CBC_7020:
			return STRING_CBC_7020[0];
 	  	default:
		  	return STRING_INVALID;
	}

	
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static int getPAOClass( String category, String paoClass )
{
	//Maybe change the DeviceClass to PAOClasses and have every PaoClass int be unique
	if( paoClass.equalsIgnoreCase(PAOGroups.STRING_CAT_CAPCONTROL) )
	{
		return PAOGroups.CLASS_CAPCONTROL;
	}
	if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_DEVICE)
		 || category.equalsIgnoreCase(PAOGroups.STRING_CAT_LOADMANAGEMENT) )
	{
		return DeviceClasses.getClass( paoClass );
	}
	else if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_ROUTE) )
	{
		return PAOGroups.CLASS_ROUTE;
	}
	else if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_PORT) )
	{
		return PAOGroups.CLASS_PORT;
	}
	else if( category.equalsIgnoreCase(PAOGroups.STRING_CAT_CUSTOMER) )
	{
		return PAOGroups.CLASS_CUSTOMER;
	}
	else
		return PAOGroups.INVALID;
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static String getPAOClass( int category, int paoClass )
{

	//Maybe change the DeviceClass to PAOClasses and have every PaoClass int be unique
	if( category == CAT_CAPCONTROL )
	{
		return PAOGroups.STRING_CAT_CAPCONTROL;
	}
	if( category == CAT_DEVICE
		 || category == CAT_LOADCONTROL )
	{
		return DeviceClasses.getClass( paoClass );
	}
	else if( category == CAT_ROUTE )
	{
		return PAOGroups.STRING_CAT_ROUTE;
	}
	else if( category == CAT_PORT )
	{
		return PAOGroups.STRING_CAT_PORT;
	}
	else if( category == CAT_CUSTOMER )
	{
		return PAOGroups.STRING_CAT_CUSTOMER;
	}
	else
		return STRING_INVALID;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public final static int getPAOType( String category, String paoType )
{
	//both category and class for Routes and Ports will be equal!
	if( category.equalsIgnoreCase(STRING_CAT_PORT) )
	{
		return getPortType( paoType );
	}
	else if( category.equalsIgnoreCase(STRING_CAT_ROUTE) )
	{
		return getRouteType( paoType );
	}
	else if( category.equalsIgnoreCase(STRING_CAT_DEVICE)
		  		|| category.equalsIgnoreCase(STRING_CAT_LOADMANAGEMENT) )
	{
		return getDeviceType( paoType );
	}
	else if( category.equalsIgnoreCase(STRING_CAT_CAPCONTROL) )
	{
		return getCapControlType( paoType );
	}
	else
		return PAOGroups.INVALID;
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static int getPortType(String typeString) 
{
	if( typeString == null )
		return INVALID;


	if( typeString.equalsIgnoreCase(PortTypes.STRING_LOCAL_SERIAL) ||
		 typeString.equalsIgnoreCase("local serial") )
	{
		return LOCAL_SHARED;
	}
	else if( typeString.equalsIgnoreCase(PortTypes.STRING_LOCAL_DIALBACK) )
	{
		return LOCAL_DIALBACK;
	}	
	else if( typeString.equalsIgnoreCase(PortTypes.STRING_LOCAL_RADIO) ||
				 typeString.equalsIgnoreCase("local radio port") )
	{
		return LOCAL_RADIO;
	}
	else	if( typeString.equalsIgnoreCase(PortTypes.STRING_LOCAL_DIALUP) ||
				 typeString.equalsIgnoreCase("local dialup port") )
	{
		return LOCAL_DIALUP;
	}
	else if( typeString.equalsIgnoreCase(PortTypes.STRING_TERM_SERVER) ||
				 typeString.equalsIgnoreCase("terminal server shared") )
	{
		return TSERVER_SHARED;
	}
	else if( typeString.equalsIgnoreCase(PortTypes.STRING_TERM_SERVER_RADIO) ||
				 typeString.equalsIgnoreCase("terminal server radio port") )
	{
		return TSERVER_RADIO;
	}
	else if( typeString.equalsIgnoreCase(PortTypes.STRING_TERM_SERVER_DIALUP) ||
				 typeString.equalsIgnoreCase("terminal server dialup port") )
	{
		return TSERVER_DIALUP;
	}
	else	if( typeString.equalsIgnoreCase(PortTypes.STRING_DIALOUT_POOL) )
	{
		return DIALOUT_POOL;
	}
	else
		return INVALID;

}


/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static int getRouteType(String typeString)
{
	if( typeString.equalsIgnoreCase( STRING_CCU) )
		return ROUTE_CCU;
	else if( typeString.equalsIgnoreCase( STRING_LCU) )
		return ROUTE_LCU;
	else if( typeString.equalsIgnoreCase( STRING_MACRO) )
		return ROUTE_MACRO;
	else if( typeString.equalsIgnoreCase( STRING_TAP_PAGING) )
		return ROUTE_TAP_PAGING;
	else if( typeString.equalsIgnoreCase( STRING_WCTP_TERMINAL_ROUTE) )
		return ROUTE_WCTP_TERMINAL;
	else if( typeString.equalsIgnoreCase( STRING_TCU) )
		return ROUTE_TCU;
	else if( typeString.equalsIgnoreCase( STRING_VERSACOM) )
		return ROUTE_VERSACOM;
	else if( typeString.equalsIgnoreCase( STRING_SERIES_5_LMI_ROUTE) )
		return ROUTE_SERIES_5_LMI;
	else if( typeString.equalsIgnoreCase( STRING_RTC_ROUTE) )
		return ROUTE_RTC;
	else
		return INVALID;
}
/****
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isCapControl( com.cannontech.database.data.lite.LiteYukonPAObject lite )
{
	return ( (lite.getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL
				   || lite.getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE )
				 && lite.getPaoClass() == com.cannontech.database.data.pao.PAOGroups.CLASS_CAPCONTROL);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param type java.lang.String
 */
public static final boolean isDialupPort(int type) 
{
	switch( type )
	{
		case DIALOUT_POOL:
		case LOCAL_DIALUP:
		case LOCAL_DIALBACK:
		case TSERVER_DIALUP:
			return true;
		default:
			return false;	
	}

}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param type java.lang.String
 */
public static final boolean isDialupPort(String type) 
{
	type = type.trim().toLowerCase();
	int intType = getPortType(type);

	return isDialupPort(intType);
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isLoadManagement( com.cannontech.database.data.lite.LiteYukonPAObject lite )
{
	return( lite.getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.GROUP
			  || lite.getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.LOADMANAGEMENT );

}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isStringDevice(final String typeString, final String[] category ) 
{
	for( int i = 0; i < category.length; i++ )
	{
		if( typeString.equalsIgnoreCase(category[i]) )
			return true;
	}

	return false;
}
}
