/*
 * Created on Jan 3, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonListFuncs;
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
	
	public static int getInventoryCategoryID(int deviceTypeID, LiteStarsEnergyCompany energyCompany) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( deviceTypeID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305)
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
		
		return CtiUtilities.NONE_ID;
	}
	
	public static boolean isLMHardware(int categoryID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_TWOWAYREC);
	}
	
	public static boolean isMCT(int categoryID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( categoryID );
		return (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_INV_CAT_MCT);
	}
	
	public static int getHardwareConfigType(int devTypeID) {
		int devTypeDefID = YukonListFuncs.getYukonListEntry( devTypeID ).getYukonDefID();
		if (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
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
		
		return 0;
	}
	
	public static boolean supportServiceInOut(int devTypeID) {
		int hwConfigType = getHardwareConfigType( devTypeID );
		return hwConfigType == HW_CONFIG_TYPE_EXPRESSCOM
			|| hwConfigType == HW_CONFIG_TYPE_VERSACOM;
	}
	
	public static boolean isAdditionalProtocol(int devTypeDefID) {
		return (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205
			|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305);
	}
	
	/**
	 * Check to see if the thermostat schedule is vaild
	 */
	public static boolean isValidThermostatSchedule(LiteLMThermostatSchedule liteSched) {
		if (liteSched == null || liteSched.getThermostatSeasons().size() != 2)
			return false;
		
		int thermTypeDefID = YukonListFuncs.getYukonListEntry( liteSched.getThermostatTypeID() ).getYukonDefID();
		for (int i = 0; i < liteSched.getThermostatSeasons().size(); i++) {
			int numSeasonEntries = ((LiteLMThermostatSeason) liteSched.getThermostatSeasons().get(i)).getSeasonEntries().size();
			if ((thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
					|| thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT)
					&& numSeasonEntries != 12
				|| thermTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO
					&& numSeasonEntries != 28)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static ArrayList getLMHardwareInRange(LiteStarsEnergyCompany energyCompany, int devTypeDefID, Integer snFrom, Integer snTo) {
		ArrayList hwList = new ArrayList();
		
		ArrayList inventory = energyCompany.loadAllInventory( true );
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				if (!(inventory.get(i) instanceof LiteStarsLMHardware)) continue;
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(i);
				
				if (YukonListFuncs.getYukonListEntry(liteHw.getLmHardwareTypeID()).getYukonDefID() != devTypeDefID)
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
}
