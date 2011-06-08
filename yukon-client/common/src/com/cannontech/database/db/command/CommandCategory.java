/*
 * Created on Sep 24, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.command;

import java.util.ArrayList;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;

public class CommandCategory
{
	public static final String STRING_CMD_ALPHA_BASE = "All Alpha Meters";
	public static final String STRING_CMD_CBC_BASE = "All CBCs";
	public static final String STRING_CMD_CBC_ONEWAY = "Oneway CBCs";
	public static final String STRING_CMD_CBC_TWOWAY = "Twoway CBCs";
	public static final String STRING_CMD_CCU_BASE = "All CCUs";
	public static final String STRING_CMD_DISCONNECT_BASE = "All Disconnect Meters";
	public static final String STRING_CMD_LOAD_GROUP_BASE = "All Load Group";
	public static final String STRING_CMD_ION_BASE = "All ION Meters";
	public static final String STRING_CMD_LCU_BASE = "All LCUs";
	public static final String STRING_CMD_MCT_BASE = "All MCTs";
	public static final String STRING_CMD_IED_BASE = "All IED Meters";
	public static final String STRING_CMD_LP_BASE = "All LP Meters";
	public static final String STRING_CMD_RTU_BASE = "All RTUs";
	public static final String STRING_CMD_REPEATER_BASE = "All Repeaters";
	public static final String STRING_CMD_TCU_BASE = "All TCUs";
	public static final String STRING_CMD_STATUSINPUT_BASE = "All Status Input";
	public static final String STRING_CMD_PING_BASE = "All Ping-able";
	public static final String STRING_CMD_MCT_4XX_SERIES_BASE = "All MCT-4xx Series";
	public static final String STRING_CMD_TWO_WAY_LCR_BASE = "All Two Way LCR";

	//Strings that commander uses to decide what "category" it is displaying, these are for specific items
	// that don't necessarily have a deviceType, or where deviceType doesn't help find the commands.	
	public static final String STRING_CMD_VERSACOM_SERIAL = "VersacomSerial";
	public static final String STRING_CMD_EXPRESSCOM_SERIAL = "ExpresscomSerial";
	public static final String STRING_CMD_SERIALNUMBER = "LCRSerial";
	public static final String STRING_CMD_DEVICE_GROUP = "Device Group";

	// Specific only to GRE, must add a role to see if these display
	public static final String STRING_CMD_SA205_SERIAL = "SA205Serial";
	public static final String STRING_CMD_SA305_SERIAL = "SA305Serial";
	

	private static final String[] ALL_CATEGORIES = {
		STRING_CMD_ALPHA_BASE,
		STRING_CMD_CBC_BASE,
		STRING_CMD_CCU_BASE,
		STRING_CMD_DISCONNECT_BASE,
		STRING_CMD_IED_BASE,
		STRING_CMD_ION_BASE,
		STRING_CMD_LCU_BASE,
		STRING_CMD_LOAD_GROUP_BASE,
		STRING_CMD_LP_BASE,
		STRING_CMD_MCT_BASE,
		STRING_CMD_RTU_BASE,
		STRING_CMD_REPEATER_BASE,
		STRING_CMD_TCU_BASE,
		STRING_CMD_STATUSINPUT_BASE,
		STRING_CMD_PING_BASE,
		STRING_CMD_MCT_4XX_SERIES_BASE,
		STRING_CMD_TWO_WAY_LCR_BASE
	};


	private static ArrayList<PaoType> CAT_ALPHA_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_CBC_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_CCU_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_DISCONNECT_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_IED_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_ION_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_LCU_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_LOAD_GROUP_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_LP_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_MCT_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_RTU_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_REPEATER_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_TCU_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_STATUSINPUT_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_PING_BASE_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_MCT_4XX_SERIES_DEVTYPES = null;
	private static ArrayList<PaoType> CAT_TWO_WAY_LCR_DEVTYPES = null;
	
	/**
	 * Returns String [] of all Categories  
	 * @return
	 */
	public final static String[] getAllCategories()
	{
		return ALL_CATEGORIES;
	}
	
	/**
	 * Return true if category is one from AllCategories
	 *  false if not (more than likely it is just a YukonPaobject.paoType value.
	 * @param category
	 * @return
	 */
	public static boolean isCommandCategory(String category)
	{
		for(int i = 0; i < getAllCategories().length; i++)
		{
			if(getAllCategories()[i].equalsIgnoreCase(category))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns Vector of String values (deviceTypes)
	 * @param category
	 * @return
	 */
	public static ArrayList<PaoType> getAllTypesForCategory(String category)
	{
		if( category.equalsIgnoreCase(STRING_CMD_ALPHA_BASE))
		{
			return getAllAlphaBaseDevTypes();	
		}
		else if( category.equalsIgnoreCase(STRING_CMD_CBC_BASE))
		{
			return getAllCBCDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_CBC_ONEWAY))
        {
            return getAllOnewayCBCDevTypes();
        }
		else if( category.equalsIgnoreCase(STRING_CMD_CBC_TWOWAY))
        {
            return getAllTwowayCBCDevTypes();
        }
		else if( category.equalsIgnoreCase(STRING_CMD_CCU_BASE))
		{
			return getAllCCUDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_DISCONNECT_BASE))
		{
			return getAllDisconnectDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_IED_BASE))
		{
			return getAllIEDDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_ION_BASE))
		{
			return getAllIONDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_LCU_BASE))
		{
			return getAllLCUDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_LOAD_GROUP_BASE))
		{
			return getAllLoadGroupDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_LP_BASE))
		{
			return getAllLPDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_MCT_BASE))
		{
			return getALLMCTDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_RTU_BASE))
		{
			return getAllRTUDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_REPEATER_BASE))
		{
			return getAllRepeaterDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_TCU_BASE))
		{
			return getAllTCUDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_STATUSINPUT_BASE))
		{
			return getAllStatusInputDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_PING_BASE))
		{
			return getAllPingableDevTypes();
		}
		else if (category.equalsIgnoreCase(STRING_CMD_MCT_4XX_SERIES_BASE) )
		{
			return getAllMCT4XXSeriesDevTypes();
		}
		else if (category.equalsIgnoreCase(STRING_CMD_TWO_WAY_LCR_BASE) )
		{
			return getAllTwoWayLCRDevTypes();
		}
		return null;
	}

    /**
	 * @return
	 */
	private static ArrayList<PaoType> getAllStatusInputDevTypes()
	{
		if( CAT_STATUSINPUT_BASE_DEVTYPES == null)
		{
			CAT_STATUSINPUT_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.hasStatusInput(paoType.getDeviceTypeId())) {
					CAT_STATUSINPUT_BASE_DEVTYPES.add(paoType);
				}
			}
		}
		return CAT_STATUSINPUT_BASE_DEVTYPES;
	}
	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllPingableDevTypes()
	{
		if( CAT_PING_BASE_DEVTYPES == null)
		{
			CAT_PING_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isLoopable(paoType.getDeviceTypeId()) )
					CAT_PING_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_PING_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllTCUDevTypes()
	{
		if( CAT_TCU_BASE_DEVTYPES == null)
		{
			CAT_TCU_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isTCU(paoType.getDeviceTypeId()) )
					CAT_TCU_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_TCU_BASE_DEVTYPES;	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllRepeaterDevTypes()
	{
		if( CAT_REPEATER_BASE_DEVTYPES == null)
		{
			CAT_REPEATER_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isRepeater(paoType.getDeviceTypeId()) )
					CAT_REPEATER_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_REPEATER_BASE_DEVTYPES;	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllRTUDevTypes()
	{
		if( CAT_RTU_BASE_DEVTYPES == null)
		{
			CAT_RTU_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isRTU(paoType.getDeviceTypeId()) )
					CAT_RTU_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_RTU_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getALLMCTDevTypes()
	{
		if( CAT_MCT_BASE_DEVTYPES == null)
		{
			CAT_MCT_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isMCT(paoType.getDeviceTypeId()) )
					CAT_MCT_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_MCT_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllLPDevTypes()
	{
		if( CAT_LP_BASE_DEVTYPES == null)
		{
			CAT_LP_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isLoadProfile1Channel(paoType.getDeviceTypeId()) ||
					DeviceTypesFuncs.isLoadProfile3Channel(paoType.getDeviceTypeId()) ||
					DeviceTypesFuncs.isLoadProfile4Channel(paoType.getDeviceTypeId()))
					CAT_LP_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_LP_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllLoadGroupDevTypes()
	{
		if( CAT_LOAD_GROUP_BASE_DEVTYPES == null)
		{
			CAT_LOAD_GROUP_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isLmGroup(paoType.getDeviceTypeId()) )
					CAT_LOAD_GROUP_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_LOAD_GROUP_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllLCUDevTypes()
	{
		if( CAT_LCU_BASE_DEVTYPES == null)
		{
			CAT_LCU_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isLCU(paoType.getDeviceTypeId()) )
					CAT_LCU_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_LCU_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList<PaoType> getAllIEDDevTypes()
	{
		if( CAT_IED_BASE_DEVTYPES == null)
		{
			CAT_IED_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isIED(paoType.getDeviceTypeId()) )
					CAT_IED_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_IED_BASE_DEVTYPES;
	}

	public final static ArrayList<PaoType> getAllAlphaBaseDevTypes() 
	{
		if(CAT_ALPHA_BASE_DEVTYPES == null)
		{
			CAT_ALPHA_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isAlpha(paoType.getDeviceTypeId()) )
					CAT_ALPHA_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_ALPHA_BASE_DEVTYPES;
	}		

	public final static ArrayList<PaoType> getAllCBCDevTypes()
	{
		if( CAT_CBC_BASE_DEVTYPES == null)
		{
			CAT_CBC_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isCapBankController(paoType.getDeviceTypeId()) )
					CAT_CBC_BASE_DEVTYPES.add(paoType);
			}
			CAT_CBC_BASE_DEVTYPES.add(PaoType.CAPBANK);
		}
		return CAT_CBC_BASE_DEVTYPES;
	}
	
	public final static ArrayList<PaoType> getAllOnewayCBCDevTypes()
    {
        if( CAT_CBC_BASE_DEVTYPES == null)
        {
            CAT_CBC_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
                if( DeviceTypesFuncs.isCBCOneWay(paoType.getDeviceTypeId()) )
                    CAT_CBC_BASE_DEVTYPES.add(paoType);
            }
            CAT_CBC_BASE_DEVTYPES.add(PaoType.CAPBANK);
        }
        return CAT_CBC_BASE_DEVTYPES;
    }
	
	public final static ArrayList<PaoType> getAllTwowayCBCDevTypes()
    {
        if( CAT_CBC_BASE_DEVTYPES == null)
        {
            CAT_CBC_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
                if( DeviceTypesFuncs.isCBCTwoWay(paoType.getDeviceTypeId()) )
                    CAT_CBC_BASE_DEVTYPES.add(paoType);
            }
            CAT_CBC_BASE_DEVTYPES.add(PaoType.CAPBANK);
        }
        return CAT_CBC_BASE_DEVTYPES;
    }

	public final static ArrayList<PaoType> getAllCCUDevTypes() 
	{
		if( CAT_CCU_BASE_DEVTYPES == null)
		{
			CAT_CCU_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isCCU(paoType.getDeviceTypeId()) )
					CAT_CCU_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_CCU_BASE_DEVTYPES;
	}

	public final static ArrayList<PaoType> getAllDisconnectDevTypes() 
	{
		if( CAT_DISCONNECT_BASE_DEVTYPES == null)
		{
			CAT_DISCONNECT_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isDisconnectMCT(paoType.getDeviceTypeId()) )
					CAT_DISCONNECT_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_DISCONNECT_BASE_DEVTYPES;
	}

	public final static ArrayList<PaoType> getAllMCT4XXSeriesDevTypes() 
	{
		if( CAT_MCT_4XX_SERIES_DEVTYPES == null)
		{
			CAT_MCT_4XX_SERIES_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isMCT4XX(paoType.getDeviceTypeId()) )
					CAT_MCT_4XX_SERIES_DEVTYPES.add(paoType);
			}
		}
		return CAT_MCT_4XX_SERIES_DEVTYPES;
	}
	
	public final static ArrayList<PaoType> getAllTwoWayLCRDevTypes() {
		
		if( CAT_TWO_WAY_LCR_DEVTYPES == null) {
			
			CAT_TWO_WAY_LCR_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isTwoWayLcr(paoType.getDeviceTypeId()) )
					CAT_TWO_WAY_LCR_DEVTYPES.add(paoType);
			}
		}
		return CAT_TWO_WAY_LCR_DEVTYPES;
	}

	public final static ArrayList<PaoType> getAllIONDevTypes() 
	{
		if( CAT_ION_BASE_DEVTYPES == null)
		{
			CAT_ION_BASE_DEVTYPES = new ArrayList<PaoType>();
			for (PaoType paoType : PaoType.values()) {
				if( DeviceTypesFuncs.isIon(paoType.getDeviceTypeId()) )
					CAT_ION_BASE_DEVTYPES.add(paoType);
			}
		}
		return CAT_ION_BASE_DEVTYPES;
	}
}
