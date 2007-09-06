/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InventoryUtils {

	public static final int SA205_UNUSED_ADDR = 3909;
	
	public static final int HW_CONFIG_TYPE_EXPRESSCOM = 1;
	public static final int HW_CONFIG_TYPE_VERSACOM = 2;
	public static final int HW_CONFIG_TYPE_SA205 = 3;
	public static final int HW_CONFIG_TYPE_SA305 = 4;
	public static final int HW_CONFIG_TYPE_SA_SIMPLE = 5;
	
	public static int getInventoryCategoryID(int deviceTypeID, LiteStarsEnergyCompany energyCompany) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( deviceTypeID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE ||
            entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP) 
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC).getEntryID();
		}
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC).getEntryID();
		}
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT)
		{
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT).getEntryID();
		}
		
		return CtiUtilities.NONE_ZERO_ID;
	}
	
	public static boolean isLMHardware(int categoryID) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC);
	}

	public static boolean isNonYukonMeter(int categoryID) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_NON_YUKON_METER);
	}
	
	public static boolean isMCT(int categoryID) {
		YukonListEntry entry = DaoFactory.getYukonListDao().getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
	}
	
	public static int getHardwareConfigType(int devTypeID) {
		int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry( devTypeID ).getYukonDefID();
		if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO
            || devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP)
			return HW_CONFIG_TYPE_EXPRESSCOM;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000)
			return HW_CONFIG_TYPE_VERSACOM;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205)
			return HW_CONFIG_TYPE_SA205;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305)
			return HW_CONFIG_TYPE_SA305;
		else if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE)
			return HW_CONFIG_TYPE_SA_SIMPLE;
		
		return 0;
	}
	
	public static boolean supportServiceInOut(int devTypeID) {
		int hwConfigType = getHardwareConfigType( devTypeID );
		return hwConfigType == HW_CONFIG_TYPE_EXPRESSCOM
			|| hwConfigType == HW_CONFIG_TYPE_VERSACOM;
	}
	
	public static boolean supportConfiguration(int devTypeID) {
		int devTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry( devTypeID ).getYukonDefID();
		return (devTypeDefID != YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE);
	}
	
	public static boolean isAdditionalProtocol(int devTypeDefID) {
		return (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA_SIMPLE);
	}
	
	/**
	 * Check to see if the thermostat schedule is vaild
	 */
	public static boolean isValidThermostatSchedule(LiteLMThermostatSchedule liteSched) {
		if (liteSched == null || liteSched.getThermostatSeasons().size() != 2)
			return false;
		
		int thermTypeDefID = DaoFactory.getYukonListDao().getYukonListEntry( liteSched.getThermostatTypeID() ).getYukonDefID();
		for (int i = 0; i < liteSched.getThermostatSeasons().size(); i++) {
			int numSeasonEntries = ((LiteLMThermostatSeason) liteSched.getThermostatSeasons().get(i)).getSeasonEntries().size();
			if ((thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
					|| thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
                    || thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT_HEATPUMP)
					&& numSeasonEntries != 12
				|| thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO
					&& numSeasonEntries != 28)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static List<LiteStarsLMHardware> getLMHardwareInRange(LiteStarsEnergyCompany energyCompany, int devTypeDefID, Integer snFrom, Integer snTo) {
		List<LiteStarsLMHardware> hwList = new ArrayList<LiteStarsLMHardware>();
		
        List<LiteInventoryBase> inventory = energyCompany.loadAllInventory( true );
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				if (!(inventory.get(i) instanceof LiteStarsLMHardware)) continue;
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(i);
				
				if (DaoFactory.getYukonListDao().getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID() != devTypeDefID)
					continue;
				
				try {
					int serialNo = Integer.parseInt( liteHw.getManufacturerSerialNumber() );
					if (snFrom != null && serialNo < snFrom.intValue()) continue;
					if (snTo != null && serialNo > snTo.intValue()) continue;
				}
				catch (NumberFormatException nfe) { continue; }
				
				hwList.add( liteHw );
			}
		}
		
		return hwList;
	}
    
    public static boolean isSerialWithPossibleCharsGreaterThan(String serial, String minimumVal)
    {
        Long serialToCompare = null;
        try 
        {
            serialToCompare = Long.valueOf( serial );
        }
        catch (NumberFormatException e) {}
        
        Long requiredMin = null;
        try 
        {
            requiredMin = Long.valueOf( minimumVal );
        }
        catch (NumberFormatException e) {}
        
        if (serialToCompare != null && requiredMin != null) 
        {
            int result = serialToCompare.compareTo( requiredMin );
            if(result >= 0) 
                return true;
            return false;
        }
        else if(requiredMin == null && serialToCompare == null)
        {
            int result = serial.compareTo(minimumVal);
            if(result >= 0)
                return true;
            return false;
        }
        else
            return false;
       
    }
    
    public static boolean isSerialWithPossibleCharsLessThan(String serial, String maximumVal)
    {
        Long serialToCompare = null;
        try 
        {
            serialToCompare = Long.valueOf( serial );
        }
        catch (NumberFormatException e) {}
        
        Long requiredMax = null;
        try 
        {
            requiredMax = Long.valueOf( maximumVal );
        }
        catch (NumberFormatException e) {}
        
        if (serialToCompare != null && requiredMax != null) 
        {
            int result = serialToCompare.compareTo( requiredMax );
            if(result <= 0) 
                return true;
            return false;
        }
        else if(requiredMax == null && serialToCompare == null)
        {
            int result = serial.compareTo( maximumVal );
            if(result <= 0)
                return true;
            return false;
        }
        else
            return false;
       
    }
    
    public static Integer returnIntegerIfPossible(String specificString)
    {
        Integer convert = new Integer(-999999);
        try 
        {
            convert = Integer.valueOf( specificString );
        }
        catch (NumberFormatException e) {}
        
        return convert;
    }
}
