/*
 * Created on Sep 24, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.command;

import java.util.ArrayList;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CommandCategory
{
	public static final String STRING_CMD_ALPHA_BASE = "All Alpha Meters";
	public static final String STRING_CMD_CBC_BASE = "All CBCs";
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

	//Strings that commander uses to decide what "category" it is displaying, these are for specific items
	// that don't necessarily have a deviceType, or where deviceType doesn't help find the commands.	
	public static final String STRING_CMD_VERSACOM_SERIAL = "VersacomSerial";
	public static final String STRING_CMD_EXPRESSCOM_SERIAL = "ExpresscomSerial";
	public static final String STRING_CMD_SERIALNUMBER = "LCRSerial";
	public static final String STRING_CMD_COLLECTION_GROUP = "CollectionGroup";
	//TODO there are no commands applicable to customers yet.
	public static final String STRING_CMD_CICUSTOMER = "CICustomer";

	//TODO Specific only to GRE, must add a role to see if these display
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
		STRING_CMD_VERSACOM_SERIAL,
		STRING_CMD_EXPRESSCOM_SERIAL,
		STRING_CMD_SERIALNUMBER
//		STRING_CMD_COLLECTION_GROUP
//		STRING_CMD_CICUSTOMER
	};


	private static ArrayList CAT_ALPHA_BASE_DEVTYPES = null;
	private static ArrayList CAT_CBC_BASE_DEVTYPES = null;
	private static ArrayList CAT_CCU_BASE_DEVTYPES = null;
	private static ArrayList CAT_DISCONNECT_BASE_DEVTYPES = null;
	private static ArrayList CAT_IED_BASE_DEVTYPES = null;
	private static ArrayList CAT_ION_BASE_DEVTYPES = null;
	private static ArrayList CAT_LCU_BASE_DEVTYPES = null;
	private static ArrayList CAT_LOAD_GROUP_BASE_DEVTYPES = null;
	private static ArrayList CAT_LP_BASE_DEVTYPES = null;
	private static ArrayList CAT_MCT_BASE_DEVTYPES = null;
	private static ArrayList CAT_RTU_BASE_DEVTYPES = null;
	private static ArrayList CAT_REPEATER_BASE_DEVTYPES = null;
	private static ArrayList CAT_TCU_BASE_DEVTYPES = null;
	private static ArrayList CAT_EXPRESSCOMSERIAL_BASE_DEVTYPES = null;
	private static ArrayList CAT_SERIALNUMBER_BASE_DEVTYPES = null;
	private static ArrayList CAT_VERSACOMSERIAL_BASE_DEVTYPES = null;
	
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
	public static ArrayList getAllTypesForCategory(String category)
	{
		if( category.equalsIgnoreCase(STRING_CMD_ALPHA_BASE))
		{
			return getAllAlphaBaseDevTypes();	
		}
		else if( category.equalsIgnoreCase(STRING_CMD_CBC_BASE))
		{
			return getAllCBCDevTypes();
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
		else if( category.equalsIgnoreCase(STRING_CMD_VERSACOM_SERIAL))
		{
			return getAllVersacomSerialDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_EXPRESSCOM_SERIAL))
		{
			return getAllExpresscomSerialDevTypes();
		}
		else if( category.equalsIgnoreCase(STRING_CMD_SERIALNUMBER))
		{
			return getAllSerialNumberDevTypes();
		}
		//TODO what to return if not found?
		return null;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllSerialNumberDevTypes()
	{
		if( CAT_SERIALNUMBER_BASE_DEVTYPES == null)
		{
			CAT_SERIALNUMBER_BASE_DEVTYPES = new ArrayList();
			CAT_RTU_BASE_DEVTYPES.add(CommandCategory.STRING_CMD_SERIALNUMBER);
		}
		return CAT_SERIALNUMBER_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllExpresscomSerialDevTypes()
	{
		if( CAT_EXPRESSCOMSERIAL_BASE_DEVTYPES == null)
		{
			CAT_EXPRESSCOMSERIAL_BASE_DEVTYPES = new ArrayList();
			CAT_RTU_BASE_DEVTYPES.add(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL);
		}
		return CAT_EXPRESSCOMSERIAL_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllVersacomSerialDevTypes()
	{
		if( CAT_VERSACOMSERIAL_BASE_DEVTYPES == null)
		{
			CAT_VERSACOMSERIAL_BASE_DEVTYPES = new ArrayList();
			CAT_RTU_BASE_DEVTYPES.add(CommandCategory.STRING_CMD_VERSACOM_SERIAL);
		}
		return CAT_VERSACOMSERIAL_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllTCUDevTypes()
	{
		if( CAT_TCU_BASE_DEVTYPES == null)
		{
			CAT_TCU_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isTCU(i) )
					CAT_TCU_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_TCU_BASE_DEVTYPES;	}

	/**
	 * @return
	 */
	private static ArrayList getAllRepeaterDevTypes()
	{
		if( CAT_REPEATER_BASE_DEVTYPES == null)
		{
			CAT_REPEATER_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isRepeater(i) )
					CAT_REPEATER_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_REPEATER_BASE_DEVTYPES;	}

	/**
	 * @return
	 */
	private static ArrayList getAllRTUDevTypes()
	{
		if( CAT_RTU_BASE_DEVTYPES == null)
		{
			CAT_RTU_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isRTU(i) )
					CAT_RTU_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_RTU_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getALLMCTDevTypes()
	{
		if( CAT_MCT_BASE_DEVTYPES == null)
		{
			CAT_MCT_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isMCT(i) )
					CAT_MCT_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_MCT_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllLPDevTypes()
	{
		if( CAT_LP_BASE_DEVTYPES == null)
		{
			CAT_LP_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isLoadProfile1Channel(i) ||
					DeviceTypesFuncs.isLoadProfile3Channel(i) ||
					DeviceTypesFuncs.isLoadProfile4Channel(i))
					CAT_LP_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_LP_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllLoadGroupDevTypes()
	{
		if( CAT_LOAD_GROUP_BASE_DEVTYPES == null)
		{
			CAT_LOAD_GROUP_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isLmGroup(i) )
					CAT_LOAD_GROUP_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_LOAD_GROUP_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllLCUDevTypes()
	{
		if( CAT_LCU_BASE_DEVTYPES == null)
		{
			CAT_LCU_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isLCU(i) )
					CAT_LCU_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_LCU_BASE_DEVTYPES;
	}

	/**
	 * @return
	 */
	private static ArrayList getAllIEDDevTypes()
	{
		if( CAT_IED_BASE_DEVTYPES == null)
		{
			CAT_IED_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isIED(i) )
					CAT_IED_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_IED_BASE_DEVTYPES;
	}

	public final static ArrayList getAllAlphaBaseDevTypes() 
	{
		if(CAT_ALPHA_BASE_DEVTYPES == null)
		{
			CAT_ALPHA_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isAlpha(i) )
					CAT_ALPHA_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_ALPHA_BASE_DEVTYPES;
	}		

	public final static ArrayList getAllCBCDevTypes()
	{
		if( CAT_CBC_BASE_DEVTYPES == null)
		{
			CAT_CBC_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isCapBankController(i) )
					CAT_CBC_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
			CAT_CBC_BASE_DEVTYPES.add(DeviceTypes.STRING_CAP_BANK[0]);
		}
		return CAT_CBC_BASE_DEVTYPES;
	}

	public final static ArrayList getAllCCUDevTypes() 
	{
		if( CAT_CCU_BASE_DEVTYPES == null)
		{
			CAT_CCU_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isCCU(i) )
					CAT_CCU_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_CCU_BASE_DEVTYPES;
	}

	public final static ArrayList getAllDisconnectDevTypes() 
	{
		if( CAT_DISCONNECT_BASE_DEVTYPES == null)
		{
			CAT_DISCONNECT_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isDisconnectMCT(i) )
					CAT_DISCONNECT_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_DISCONNECT_BASE_DEVTYPES;
	}

	public final static ArrayList getAllIONDevTypes() 
	{
		if( CAT_ION_BASE_DEVTYPES == null)
		{
			CAT_ION_BASE_DEVTYPES = new ArrayList();
			for (int i = 0; i < DeviceTypes.DEVICE_TYPES_COUNT; i++)
			{
				if( DeviceTypesFuncs.isIon(i) )
					CAT_ION_BASE_DEVTYPES.add(PAOGroups.getPAOTypeString(i));
			}
		}
		return CAT_ION_BASE_DEVTYPES;
	}
}
