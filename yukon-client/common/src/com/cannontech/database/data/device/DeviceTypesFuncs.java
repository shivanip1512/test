package com.cannontech.database.data.device;

/**
 * This type was created in VisualAge.
 */
public final class DeviceTypesFuncs implements com.cannontech.database.data.pao.DeviceTypes
{
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param type java.lang.String
 */
public final static boolean allowRebroadcast(int type) {

	switch( type )
	{
		case CCU710A:
		case CCU711:
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
public final static boolean allowRebroadcast(String type) 
{
	int intType = com.cannontech.database.data.pao.PAOGroups.getDeviceType(type);

	switch( intType )
	{
		case CCU710A:
		case CCU711:
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
public final static boolean hasMasterAddress(int intType) 
{

   switch( intType )
   {
      case RTU_DNP:
      case ION_7700:
      case ION_7330:
      case ION_8300:
         return true;
      default:
         return false;
   }
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isCapBankController( int deviceType )
{
	return ( deviceType == CAPBANKCONTROLLER
			    || deviceType == CBC_FP_2800
             || deviceType == DNP_CBC_6510 );
}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isCapBankController( com.cannontech.database.data.lite.LiteYukonPAObject lite )
{
	return (  com.cannontech.database.data.pao.PAOGroups.isCapControl(lite)
				 && isCapBankController(lite.getType()) );
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isCarrier(int deviceType) 
{
	switch(deviceType)
	{
		case MCT210:
		case MCT213:
		case MCT240:
		case MCT248:
		case MCT250:
		case MCT310:
		case MCT310ID:
		case MCT310IL:
      case MCT310CT:
      case MCT310IM:      
		case MCT318L:
		case MCT318:
		case MCT360:
		case MCT370:
		case REPEATER:
		case REPEATER_800:
		case DCT_501:
		case LMT_2:
		case MCTBROADCAST:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isCCU(int deviceType) 
{
	switch(deviceType)
	{
		case CCU710A:
		case CCU711:
			return true;
	
		default:
			return false;
	}

}
/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
private final static boolean isInCateogry(final String typeString, final String[] category ) 
{
	for( int i = 0; i < category.length; i++ )
	{
		if( typeString.equalsIgnoreCase(category[i]) )
			return true;
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isInjector(int deviceType) 
{
//"Type='CCU-700' OR Type='CCU-710A' OR Type='CCU-711' 
//OR Type='LCU-LG' OR Type='LCU-415' OR Type='TCU-5000'
//OR Type='TCU-5500'",

	switch(deviceType)
	{	
		case CCU710A:
		case CCU711:
		case LCULG:
		case LCU415:
		case LCU_T3026:
		case TCU5000:
		case TCU5500:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isLCU(int deviceType) 
{
	switch(deviceType)
	{	
		case LCULG:
		case LCU_ER:
		case LCU_T3026:
      case LCU415:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isLmGroup(int deviceType) 
{
	switch(deviceType)
	{	
		case LM_GROUP_EMETCON:
		case LM_GROUP_VERSACOM:
		case LM_GROUP_EXPRESSCOMM:
		case LM_GROUP_RIPPLE:
		case LM_GROUP_POINT:
		case MACRO_GROUP:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isLMProgram(int deviceType) 
{
	switch(deviceType)
	{
		case LM_DIRECT_PROGRAM:
		case LM_CURTAIL_PROGRAM:
		case LM_ENERGY_EXCHANGE_PROGRAM:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isLoadProfile1Channel(int deviceType) 
{
	switch(deviceType)
	{	
		case MCT240:
		case MCT248:
		case MCT250:
      case MCT310IL:
		case LMT_2:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isLoadProfile4Channel(int deviceType) 
{
	switch(deviceType)
	{	
		case MCT318L:
		case DCT_501:
			return true;
	
		default:
			return false;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isLoadProfile3Channel(int deviceType) 
{
   switch(deviceType)
   {  
      case MCT310CT:
      case MCT310IM:
         return true;
   
      default:
         return false;
   }
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isMCTOnly(int deviceType) 
{
	switch(deviceType)
	{
		case MCT213:
		case MCT310:
		case MCT318:
		case MCT360:
		case MCT370:
		case MCT240:
		case MCT248:
		case MCT250:
		case MCT210:
		case MCT310ID:
		case MCT310IL:
	    case MCT310CT:
    	case MCT310IM:
		case MCT318L:
			return true;
	
		default:
			return false;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isMCT(int deviceType) 
{
	switch(deviceType)
	{
		case MCT213:
		case MCT310:
		case MCT318:
		case MCT360:
		case MCT370:
		case MCT240:
		case MCT248:
		case MCT250:
		case MCT210:
		case LMT_2:
		case DCT_501:
		case MCT310ID:
		case MCT310IL:
      case MCT310CT:
      case MCT310IM:
		case MCT318L:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isMCT3xx(int deviceType) 
{
	switch(deviceType)
	{
		case MCT310:
		case MCT318:
		case MCT360:
		case MCT370:
		case MCT310ID:
		case MCT310IL:
      case MCT310CT:
      case MCT310IM:      
		case MCT318L:
			return true;
	
		default:
			return false;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7/27/2001 9:45:34 AM)
 * @return boolean
 */
public static boolean isMCTiORMCT2XX( int type )
{
	switch (type)
	{
		case MCT210 :
		case MCT213 :
		case MCT240 :
		case MCT248 :
		case MCT250 :
		case MCT310 :
		case MCT310ID :
		case MCT310IL :
      case MCT310CT:
      case MCT310IM:      
			return true;
		default :
			return false;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isMeter(int deviceType) 
{
	switch(deviceType)
	{	
		case ALPHA_PPLUS:
		case ALPHA_A1:
		case FULCRUM:
		case VECTRON:
		case LANDISGYRS4:
		case DR_87:
		case QUANTUM:
		case SIXNET:
		case ION_7700:
		case ION_7330:
		case ION_8300:
			return true;
	
		default:
			return false;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isIon(int deviceType) 
{
	switch(deviceType)
	{	
		case ION_7700:
		case ION_7330:
		case ION_8300:
			return true;
	
		default:
			return false;
	}
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isRepeater(int deviceType) 
{
	switch(deviceType)
	{	
		case REPEATER:
		case REPEATER_800:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isRTU(int deviceType) 
{
	switch(deviceType)
	{	
      case ION_7700:
      case ION_7330:
      case ION_8300:
      case RTU_DNP:
		case RTUILEX:
		case RTUWELCO:
			return true;
	
		default:
			return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isTCU(int deviceType)
{
	switch(deviceType)
	{
		case TCU5000:
		case TCU5500:
			return true;
	
		default:
			return false;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isTransmitter(int deviceType) 
{
	switch(deviceType)
	{
		case CCU710A:
		case CCU711:
		case TCU5000:
		case TCU5500:
		case LCU415:
		case LCULG:
		case LCU_ER:
		case LCU_T3026:
		case REPEATER:
		case REPEATER_800:
		case TAPTERMINAL:
		case WCTP_TERMINAL:
			return true;
	
		default:
			return false;
	}
}
}
