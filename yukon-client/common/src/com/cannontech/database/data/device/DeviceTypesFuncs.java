package com.cannontech.database.data.device;

import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceMCT400Series;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;

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
		case CCU721:
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
		case CCU721:
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
      case RTU_DART:
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
public final static boolean hasSlaveAddress(int intType) 
{

   switch( intType )
   {
      case ION_7700:
      case ION_7330:
      case ION_8300:
      case RTU_DNP:
      case RTU_MODBUS:
         return true;
      default:
         return false;
   }
}

/**
 * All meters that have status input point. Info provided by Matt 20040928
 */
public final static boolean hasStatusInput(int intType) 
{

   switch( intType )
   {
		case MCT248:
		case MCT250:
		case MCT318:
		case MCT318L:
		case MCT360:
		case MCT370:
			return true;
		default:
			return false;
   }
}
/**
 * All Meters that have a device scan rate.  The meters in this function are take from
 * DeviceEditorPanel.java (//3 - DeviceScanRateEditorPanel)...probably should be updated once in a while.
 * @return boolean
 * @param type int
 */
public final static boolean hasDeviceScanRate(int intType) 
{
    if( isCCU(intType) ||
         isTCU(intType) ||
         isLCU(intType) ||
         isMeter(intType) ||
         isMCT(intType) ||
         isRepeater(intType) ||
         isRTU(intType) ||
         intType == DNP_CBC_6510)
        return true;
    else
        return false;
}

/**
 * All meters that can be "looped".  Info provided by Matt 20040928
 * @return boolean
 * @param type int
 */
public final static boolean isLoopable(int intType) 
{
	if( isCCU(intType) ||
		isTCU(intType) ||
		isLCU(intType) ||
		isMCT(intType) ||
		isRepeater(intType) ||
		intType == DAVISWEATHER ||
		intType == RTUILEX ||
		intType == RTUWELCO	||
		intType == MCT410IL ||
		intType == MCT410CL ||
		intType == MCT410FL ||
		intType == MCT410GL ||
		intType == MCT420FL ||
		intType == MCT420FLD ||
		intType == MCT420CL ||
		intType == MCT420CLD ||
        intType == MCT430A ||
        intType == MCT430S4 ||
        intType == MCT430SL ||
        intType == MCT430A3 ||
		intType == MCT470) //||
//		isRTU(intType) ) //FUTURE
		return true;
	else
		return false;
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
             || deviceType == DNP_CBC_6510
             || deviceType == CBC_EXPRESSCOM
             || deviceType == CBC_7010
             || deviceType == CBC_7011
             || deviceType == CBC_7012
             || deviceType == CBC_7020 
             || deviceType == CBC_7022
             || deviceType == CBC_7023
             || deviceType == CBC_7024
             || deviceType == CBC_8020
             || deviceType == CBC_8024
             || deviceType == CBC_DNP);
}

public final static boolean isCapBankController702X( int deviceType )
{
    return ( deviceType == CBC_7020 
             || deviceType == CBC_7022
             || deviceType == CBC_7023
             || deviceType == CBC_7024);
}

public final static boolean isCapBankControllerDNP( int deviceType )
{
    return ( deviceType == CBC_DNP);
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param typeString java.lang.String
 */
public final static boolean isCapBankController( com.cannontech.database.data.lite.LiteYukonPAObject lite )
{
	return (  com.cannontech.database.data.pao.PAOGroups.isCapControl(lite)
				 && isCapBankController(lite.getPaoType().getDeviceTypeId()) );
}
/**
 * Returns all the CBC that require a port for communications.
 * @return
 */
public final static boolean cbcHasPort( int cbcType ) {

	return
		cbcType == DNP_CBC_6510
		|| cbcType == CBC_7020
        || cbcType == CBC_7022
        || cbcType == CBC_7023
        || cbcType == CBC_7024
        || cbcType == CBC_8020
        || cbcType == CBC_8024
        || cbcType == CBC_DNP;
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
		case MCT310IDL:
		case MCT310IL:
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
		case MCT420FL:
		case MCT420FLD:
		case MCT420CL:
		case MCT420CLD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
		case MCT470:
      	case MCT310CT:
      	case MCT310IM:      
		case MCT318L:
		case MCT318:
		case MCT360:
		case MCT370:
		case REPEATER:
		case REPEATER_902:
		case REPEATER_800:
		case REPEATER_850:
        case REPEATER_801:
        case REPEATER_921:
		case DCT_501:
		case LMT_2:
		case MCTBROADCAST:
		case LCR3102:
			return true;
	
		default:
			return false;
	}
}


public final static boolean isTwoWayLcr(int deviceType) 
{
	switch(deviceType)
	{
		case LCR3102:
			return true;
	
		default:
			return false;
	}
}

/**
 * @param deviceType
 * @return
 */
public final static boolean isAlpha(int deviceType) 
{
	switch(deviceType)
	{
		case ALPHA_A1:
		case ALPHA_PPLUS:
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
		case CCU721:
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
		case CCU721:
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
		case LM_GROUP_MCT:
		case LM_GROUP_SA305:
		case LM_GROUP_SA205:
		case LM_GROUP_SADIGITAL:
		case LM_GROUP_GOLAY:
		case LM_GROUP_DIGI_SEP:
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
		case LM_SEP_PROGRAM:
		case LM_CURTAIL_PROGRAM:
		case LM_ENERGY_EXCHANGE_PROGRAM:
			return true;
	
		default:
			return false;
	}
}

public final static boolean isLMProgramDirect(int deviceType) 
{
	switch(deviceType)
	{
		case LM_DIRECT_PROGRAM:
		case LM_SEP_PROGRAM:
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
      case MCT310IDL:
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
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
		case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
		case MCT470:
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
public final static boolean isLoadProfileVoltage(int deviceType) 
{
	switch(deviceType)
	{  
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
		case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
		case MCT470:
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
		case MCT310IDL:
		case MCT310IL:
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
		case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
		case MCT470:
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
		case MCT310IDL:
		case MCT310IL:
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
		case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
		case MCT470:
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
		case MCT310IDL:
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
public static boolean isMCT2XXORMCT310XX( int type )
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
		case MCT310IDL:
		case MCT310IL :	
      	case MCT310CT:
      	case MCT310IM:      
			return true;
		default :
			return false;
	}

}

public static boolean isMCT4XX( int type )
{
	switch (type)
	{
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
		case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
		case MCT470:  
			return true;
		default :
			return false;
	}

}

public static boolean isMCT470( int type )
{
    switch (type)
    {
        case MCT470:
            return true;
        default:
            return false;
    }
}

public static boolean isMCT430( int type )
{
    switch (type)
    {
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
            return true;
        default:
            return false;
    }
}

    
public static boolean isMCT410( int type )
{
	switch (type)
	{
		case MCT410IL:
		case MCT410CL:
		case MCT410FL:
		case MCT410GL:
			return true;
		default :
			return false;
	}

}

public static boolean isMCT420( int type )
{
    switch (type)
    {
        case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
            return true;
        default:
            return false;
    }
}

public static boolean isDisconnectMCT( int type)
{
	switch (type)
	{
		case MCT213:
		case MCT310ID:
        case MCT310IDL:
        case MCT420FLD:
        case MCT420CLD:
			return true;
		default:
			return false;
	}
}

public final static boolean isTouMCT(int deviceType) 
{
    switch(deviceType)
    {
        case MCT410IL:
        case MCT410CL:
        case MCT410FL:
        case MCT410GL:
        case MCT420FL:
        case MCT420FLD:
        case MCT420CL:
        case MCT420CLD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
        case MCT470:
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
		case TRANSDATA_MARKV:
		case KV:
		case KVII:
		case SENTINEL:
		case FOCUS:
		case ALPHA_A3:
		case DAVISWEATHER:
		case RFN410FL:
		case RFN410FX:
        case RFN410FD:
        case RFN430A3:
        case RFN430KV:
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
		case REPEATER_902:
		case REPEATER_800:
        case REPEATER_801:
        case REPEATER_850:
        case REPEATER_921:
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
      case RTU_MODBUS:
      case RTU_DART:
		case RTUILEX:
		case RTUWELCO:
		case RTM:
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
		case CCU721:
		case TCU5000:
		case TCU5500:
		case LCU415:
		case LCULG:
		case LCU_ER:
		case LCU_T3026:
		case REPEATER:
		case REPEATER_902:
		case REPEATER_800:
		case REPEATER_850:
        case REPEATER_801:
        case REPEATER_921:
		case TAPTERMINAL:
		case TNPP_TERMINAL:
		case WCTP_TERMINAL:
		case SNPP_TERMINAL:
		case SERIES_5_LMI:
		case RTC:
		case RDS_TERMINAL:
			return true;
	
		default:
			return false;
	}
}

public final static boolean isVerifiable(int deviceType)
{
	switch(deviceType)
	{
		case RTC:
			return true;
	
		default:
			return false;
	}

}

public final static boolean isVirtualDevice(int deviceType) 
{
	switch(deviceType)
	{
		case VIRTUAL_SYSTEM:
			return true;
	
		default:
			return false;
	}
}

public final static boolean usesDeviceMeterGroup(int deviceType)
{
    if (isMCT(deviceType) || isMeter(deviceType)) {
        return true;
    }
	return false;
}
public final static boolean isIED(int deviceType) 
{
	switch(deviceType)
	{
		case MCT360:
		case MCT370:
			return true;
	
		default:
			return false;
	}
}

public final static boolean isReceiver(int deviceType)
{
	if( (DeviceTypesFuncs.isRTU(deviceType) && !DeviceTypesFuncs.isIon(deviceType) )  || 
			deviceType == SERIES_5_LMI)
			return true;
	return false;
}

@Deprecated /** use ChangeDeviceTypeService */
public static  ICapBankController changeCBCType (String newType, ICapBankController val) {
	return (ICapBankController) changeType (newType, val, null, null, false, false, false, false, false, false, false, false, null);
}

@Deprecated /** use ChangeDeviceTypeService */
public static Object changeType (String newType, 
        Object val, 
        DBPersistent extraObj, 
		Vector extra410Objs, 
        boolean loadProfileExists, 
		boolean blinkCountExists, 
        boolean totalKWhExists,
        boolean kWDemandExists,
        boolean voltageExists,
        boolean peakKWExists, 
        boolean maxVoltsExists,
        boolean minVoltsExists,
        DeviceBase currentDevice) {
	
        String type = newType;

		DBPersistent oldDevice = null;
		
		//get a deep copy of val
		try
		{
			oldDevice =
					(DBPersistent)CtiUtilities.copyObject( val );

			Transaction t =
				Transaction.createTransaction(
					Transaction.DELETE_PARTIAL,
					((DBPersistent) val));

			val = t.execute();
		}
		catch( Exception e )
		{
			CTILogger.error( e );
			CTILogger.info(
					"*** An exception occured when trying to change type of " +
					val + ", action aborted.");
			
			return val;
		}

		//create a brand new DeviceBase object
		val = DeviceFactory.createDevice( PAOGroups.getDeviceType(type) );
		
		//set all the device specific stuff here
		((DeviceBase) val).setDevice(
			((DeviceBase) oldDevice).getDevice() );
			
		((DeviceBase) val).setPAOName(
			((DeviceBase) oldDevice).getPAOName() );

		((DeviceBase) val).setDisableFlag(
			((DeviceBase) oldDevice).getPAODisableFlag() );

		((DeviceBase) val).setPAOStatistics(
			((DeviceBase) oldDevice).getPAOStatistics() );

		//remove then add the new elements for PAOExclusion
		((DeviceBase) val).getPAOExclusionVector().removeAllElements();
		((DeviceBase) val).getPAOExclusionVector().addAll(
			((DeviceBase) oldDevice).getPAOExclusionVector() );

		if( val instanceof CarrierBase
			 && oldDevice instanceof CarrierBase )
		{
			((CarrierBase) val).getDeviceCarrierSettings().setAddress(
				((CarrierBase) oldDevice).getDeviceCarrierSettings().getAddress() );

			((CarrierBase) val).getDeviceRoutes().setRouteID(
				((CarrierBase) oldDevice).getDeviceRoutes().getRouteID() );

		}
		else if( val instanceof IGroupRoute
		          && oldDevice instanceof IGroupRoute )
		{
			 ((IGroupRoute) val).setRouteID(
					((IGroupRoute) oldDevice).getRouteID() );
		}
		else if( val instanceof IDLCBase
			 	  && oldDevice instanceof IDLCBase)
		{
			 ((IDLCBase) val).getDeviceIDLCRemote().setAddress(
					((IDLCBase) oldDevice).getDeviceIDLCRemote().getAddress() );
		}
			 
		if( val instanceof RemoteBase
	 	    && oldDevice instanceof RemoteBase )
		{
			 RemoteBase newBase = (RemoteBase)val;
			 RemoteBase oldBase = (RemoteBase)oldDevice;
			 
			 newBase.getDeviceDirectCommSettings().setPortID(oldBase.getDeviceDirectCommSettings().getPortID());
			 newBase.setIpAddress(oldBase.getIpAddress());
			 newBase.setPort(oldBase.getPort());
		}

		if( val instanceof IDeviceMeterGroup
	 	    && oldDevice instanceof IDeviceMeterGroup )
		{
			 ((IDeviceMeterGroup) val).setDeviceMeterGroup(
					((IDeviceMeterGroup) oldDevice).getDeviceMeterGroup() );			 			
		}

		if( val instanceof TwoWayDevice
	 	    && oldDevice instanceof TwoWayDevice )
		{
			((TwoWayDevice) val).setDeviceScanRateMap(
					((TwoWayDevice) oldDevice).getDeviceScanRateMap() );
		}
		
		if( val instanceof CapBankController)
		{
			((CapBankController) val).setDeviceCBC(((CapBankController)oldDevice).getDeviceCBC());
		}
		
		//support for the 702x devices - wasn't in the old device change type panel
		if (val instanceof CapBankController702x ) {
			CapBankController702x device702 = (CapBankController702x) val;
			DeviceCBC deviceCBC = ((CapBankController702x) oldDevice).getDeviceCBC();		
			DeviceAddress deviceAddress = ((CapBankController702x) oldDevice).getDeviceAddress();
			device702.setDeviceAddress( deviceAddress);			
			device702.setDeviceCBC( deviceCBC);	
		}
		
        if (val instanceof CapBankControllerDNP ) {
            CapBankControllerDNP device702 = (CapBankControllerDNP) val;
            DeviceCBC deviceCBC = ((CapBankControllerDNP) oldDevice).getDeviceCBC();       
            DeviceAddress deviceAddress = ((CapBankControllerDNP) oldDevice).getDeviceAddress();
            device702.setDeviceAddress( deviceAddress);         
            device702.setDeviceCBC( deviceCBC); 
        }
        		
		if( val instanceof MCT410IL)
		{
			Integer[] insertedIDs = new Integer[10];
			
			if(loadProfileExists)
			{
				StringBuffer lp = new StringBuffer(((MCTBase)oldDevice).getDeviceLoadProfile().getLoadProfileCollection());
				lp.delete(1, 4);
				lp.append("NNN");
				((MCT410IL)val).getDeviceLoadProfile().setLoadProfileCollection(lp.toString());
				((MCT410IL)val).getDeviceLoadProfile().setLoadProfileDemandRate(((MCTBase)oldDevice).getDeviceLoadProfile().getLoadProfileDemandRate());
			}
			else
			{
				((MCT410IL)val).getDeviceLoadProfile().setLoadProfileCollection("NNNN");
				((MCT410IL)val).getDeviceLoadProfile().setLoadProfileDemandRate(new Integer(3600));
			}
			
			((MCT410IL)val).getDeviceLoadProfile().setVoltageDmdRate(new Integer(3600));
			((MCT410IL)val).getDeviceLoadProfile().setVoltageDmdInterval(new Integer(60));
			
            /*TODO: This process needs to be handled much more generally instead of specifying the
             * type and offset of each required point like it is now. 
             */
			try
			{
				for(int j = 0; j < extra410Objs.size(); j++)
				{
					Transaction.createTransaction(Transaction.UPDATE,
						((DBPersistent) extra410Objs.elementAt(j))).execute();
				}
			
				if(!totalKWhExists)
				{
					Transaction.createTransaction(Transaction.INSERT, 
						PointFactory.createPulseAccumPoint(
						   "kWh",
						   ((DeviceBase) val).getDevice().getDeviceID(),
						   DaoFactory.getPointDao().getNextPointId(),
						   PointTypes.PT_OFFSET_TOTAL_KWH,
						   com.cannontech.database.data.point.PointUnits.UOMID_KWH,
						   0.1, StateGroupUtils.STATEGROUP_ANALOG,
						   PointUnit.DEFAULT_DECIMAL_PLACES,
						   PointArchiveType.NONE,
						   PointArchiveInterval.ZERO) ).execute();
				}
				
				if(!blinkCountExists)	
				{   
					Transaction.createTransaction(Transaction.INSERT, 
						PointFactory.createPulseAccumPoint(
						   "Blink Count",
						   ((DeviceBase) val).getDevice().getDeviceID(),
                           DaoFactory.getPointDao().getNextPointId(),
						   PointTypes.PT_OFFSET_BLINK_COUNT,
						   com.cannontech.database.data.point.PointUnits.UOMID_COUNTS,
						   1.0, StateGroupUtils.STATEGROUP_ANALOG,
						   PointUnit.DEFAULT_DECIMAL_PLACES,
						   PointArchiveType.NONE,
						   PointArchiveInterval.ZERO) ).execute();
				}
				
				if(!loadProfileExists)
				{
					Transaction.createTransaction(Transaction.INSERT,  
						PointFactory.createDmdAccumPoint(
						   "kW-LP",
						   ((DeviceBase) val).getDevice().getDeviceID(),
                           DaoFactory.getPointDao().getNextPointId(),
						   PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND,
						   com.cannontech.database.data.point.PointUnits.UOMID_KW,
						   0.1, StateGroupUtils.STATEGROUP_ANALOG,
						   PointUnit.DEFAULT_DECIMAL_PLACES,
						   PointArchiveType.NONE,
						   PointArchiveInterval.ZERO) ).execute();
				}
				
				Transaction.createTransaction(Transaction.INSERT,  
					PointFactory.createDmdAccumPoint(
						"Voltage-LP",
						((DeviceBase) val).getDevice().getDeviceID(),
                        DaoFactory.getPointDao().getNextPointId(),                        
						PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) ).execute();
		   	
                if(!peakKWExists) {
    				Transaction.createTransaction(Transaction.INSERT, 
    					PointFactory.createDmdAccumPoint(
    						"Peak kW",
    						((DeviceBase) val).getDevice().getDeviceID(),
                            DaoFactory.getPointDao().getNextPointId(),                                                
    						PointTypes.PT_OFFSET_PEAK_KW_DEMAND,
    						com.cannontech.database.data.point.PointUnits.UOMID_KW,
    						0.1,StateGroupUtils.STATEGROUP_ANALOG,
                            PointUnit.DEFAULT_DECIMAL_PLACES,
						    PointArchiveType.NONE,
						    PointArchiveInterval.ZERO) ).execute();
                }
			
                if(!maxVoltsExists) {
    				Transaction.createTransaction(Transaction.INSERT, 
    					PointFactory.createDmdAccumPoint(
    						"Max Volts",
    						((DeviceBase) val).getDevice().getDeviceID(),
                            DaoFactory.getPointDao().getNextPointId(),                                                                        
    						PointTypes.PT_OFFSET_MAX_VOLT_DEMAND,
    						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
    						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                            PointUnit.DEFAULT_DECIMAL_PLACES,
						    PointArchiveType.NONE,
						    PointArchiveInterval.ZERO) ).execute();
                }
    			
                if(!minVoltsExists) {
    				Transaction.createTransaction(Transaction.INSERT, 
    					PointFactory.createDmdAccumPoint(
    						"Min Volts",
    						((DeviceBase) val).getDevice().getDeviceID(),
                            DaoFactory.getPointDao().getNextPointId(),                                                                                                
    						PointTypes.PT_OFFSET_MIN_VOLT_DEMAND,
    						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
    						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                            PointUnit.DEFAULT_DECIMAL_PLACES,
						    PointArchiveType.NONE,
						    PointArchiveInterval.ZERO) ).execute();
                }
                
				Transaction.createTransaction(Transaction.INSERT, 
					PointFactory.createDmdAccumPoint(
						"Frozen Peak Demand",
						((DeviceBase) val).getDevice().getDeviceID(),
                        DaoFactory.getPointDao().getNextPointId(),
						PointTypes.PT_OFFSET_FROZEN_PEAK_DEMAND,
						com.cannontech.database.data.point.PointUnits.UOMID_KW,
						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) ).execute();			
			
				Transaction.createTransaction(Transaction.INSERT, 
					PointFactory.createDmdAccumPoint(
						"Frozen Max Volts",
						((DeviceBase) val).getDevice().getDeviceID(),
                        DaoFactory.getPointDao().getNextPointId(),
						PointTypes.PT_OFFSET_FROZEN_MAX_VOLT,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) ).execute();
			
				Transaction.createTransaction(Transaction.INSERT, 
					PointFactory.createDmdAccumPoint(
						"Frozen Min Volts",
						((DeviceBase) val).getDevice().getDeviceID(),
                        DaoFactory.getPointDao().getNextPointId(),                        
						PointTypes.PT_OFFSET_FROZEN_MIN_VOLT,
						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                        PointUnit.DEFAULT_DECIMAL_PLACES,
					    PointArchiveType.NONE,
					    PointArchiveInterval.ZERO) ).execute();
			
                if(!kWDemandExists) {
    				Transaction.createTransaction(Transaction.INSERT, 
    					PointFactory.createDmdAccumPoint(
    						"kW",
    						((DeviceBase) val).getDevice().getDeviceID(),
                            DaoFactory.getPointDao().getNextPointId(),                        
    						PointTypes.PT_OFFSET_KW_DEMAND,
    						com.cannontech.database.data.point.PointUnits.UOMID_KW,
    						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                            PointUnit.DEFAULT_DECIMAL_PLACES,
						    PointArchiveType.NONE,
						    PointArchiveInterval.ZERO) ).execute();
                }
                
                if(!voltageExists) {
    				Transaction.createTransaction(Transaction.INSERT, 
    					PointFactory.createDmdAccumPoint(
    						"Voltage",
    						((DeviceBase) val).getDevice().getDeviceID(),
                            DaoFactory.getPointDao().getNextPointId(),                        
    						PointTypes.PT_OFFSET_VOLTAGE_DEMAND,
    						com.cannontech.database.data.point.PointUnits.UOMID_VOLTS,
    						0.1, StateGroupUtils.STATEGROUP_ANALOG,
                            PointUnit.DEFAULT_DECIMAL_PLACES,
						    PointArchiveType.NONE,
						    PointArchiveInterval.ZERO) ).execute();
                }
                
				Transaction t2 =
					Transaction.createTransaction(
						Transaction.ADD_PARTIAL,
						((DBPersistent) val));
						val = t2.execute();
				
			}
			catch (TransactionException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

			}	
			
			return val;
		}else if( val instanceof MCT310 && oldDevice instanceof MCT410IL) 
        {
            //TODO delete old 410 points
            int deviceId = currentDevice.getPAObjectID();
            List<LitePoint> ltPoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(deviceId);

            for (LitePoint point : ltPoints) {
                if( (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                     && point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND ) || 
                     
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_PEAK_KW_DEMAND ) ||
                     
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_MAX_VOLT_DEMAND ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_MIN_VOLT_DEMAND ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_FROZEN_PEAK_DEMAND ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_FROZEN_MAX_VOLT ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_FROZEN_MIN_VOLT ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_KW_DEMAND ) ||
                             
                     (point.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_VOLTAGE_DEMAND ) ||
                             
                     (point.getPointType() == PointTypes.STATUS_POINT
                             && point.getPointOffset() == PointTypes.PT_OFFSET_TOTAL_KWH ))
                {
                    Transaction deletePoint = Transaction.createTransaction(Transaction.DELETE, ((DBPersistent)LiteFactory.convertLiteToDBPers(point)));
                    try 
                    {
                        deletePoint.execute();
                    }
                    catch (TransactionException e)
                    {
                        com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
                    }
                }
                
            }
            
            try
            {
                Transaction t2 =
                    Transaction.createTransaction(
                        Transaction.ADD_PARTIAL,
                        ((DBPersistent) val));
    
                val = t2.execute();
    
            }
            catch (TransactionException e)
            {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    
            }
        
            //execute the actual command in the database to create extra objects 
            try
            {
                if( extraObj != null )  {
                    Transaction.createTransaction(
                        Transaction.INSERT,
                        ((DBPersistent) extraObj)).execute();
                
                    SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
                    multi.addDBPersistent( (DBPersistent)val );
                    multi.addDBPersistent( extraObj );
                    multi.setOwnerDBPersistent( (DBPersistent)val );
                    
                    return multi;
                }
    
            }
            catch (TransactionException e)
            {
                com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    
            }
    
            return val;
        }else
		{//execute the actual command in the database 
			try
			{
				Transaction t2 =
					Transaction.createTransaction(
						Transaction.ADD_PARTIAL,
						((DBPersistent) val));
	
				val = t2.execute();
	
			}
			catch (TransactionException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	
			}
		
			//execute the actual command in the database to create extra objects 
			try
			{
				if( extraObj != null )  {
					Transaction.createTransaction(
						Transaction.INSERT,
						((DBPersistent) extraObj)).execute();
				
					SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
					multi.addDBPersistent( (DBPersistent)val );
					multi.addDBPersistent( extraObj );
					multi.setOwnerDBPersistent( (DBPersistent)val );
					
					return multi;
				}
	
			}
			catch (TransactionException e)
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	
			}
	
			return val;
		}
	}

    /**
     * Returns true for all Disconnect MCTs and All MCTs(400series) with disconnect collar defined.
     * @param yukonDevice
     * @return
     */
    public static boolean isDisconnectMCTOrHasCollar( SimpleDevice yukonDevice )
    {
        if (isDisconnectMCT(yukonDevice.getType()) )
            return true;
        if( isMCT410(yukonDevice.getType()) || yukonDevice.getType() == MCT420FL )
            return DeviceMCT400Series.hasExistingDisconnectAddress(yukonDevice.getDeviceId());
        return false;
    }
    
    public static boolean isCBCTwoWay(int deviceType) {
    	switch (deviceType) {
	    	case DNP_CBC_6510:
	    	case CBC_7020:
	    	case CBC_7022:
	    	case CBC_7023:
	    	case CBC_7024:
	    	case CBC_8020:
	    	case CBC_8024:
	    	case CBC_DNP:
	    		return true;
		
			default:
				return false;
		}
    }
    
    public static boolean isCBCOneWay(int deviceType) {
        switch (deviceType) {
            case CBC_7010:
            case CBC_7011:
            case CBC_7012:
            case CBC_EXPRESSCOM:
            case CAPBANKCONTROLLER:
                return true;
        
            default:
                return false;
        }
    }
    
    public static boolean is702xDevice(int deviceType) {
        switch (deviceType) {
            case CBC_7020:
            case CBC_7022:
            case CBC_7023:
            case CBC_7024:
                return true;
        
            default:
                return false;
        }
    }
    
    public static boolean isTcpPort(int portId) {
        PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
        return paoDao.getYukonPao(portId).getPaoIdentifier().getPaoType() == PaoType.TCPPORT;
    }
    
    public static boolean isRfn(int deviceType) {
        try {
            return PaoType.getForId(deviceType).getPaoClass() == PaoClass.RFMESH;
        } catch (IllegalArgumentException e) {
            // shouldn't happen, but we'll mimic old behavior just in case
            return false;
        }
    }
}
