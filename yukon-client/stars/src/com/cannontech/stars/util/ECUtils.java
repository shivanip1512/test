/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.types.StarsLoginStatus;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;
import com.cannontech.stars.xml.serialize.types.StarsThermostatTypes;
import com.cannontech.user.UserUtils;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ECUtils {
	
	public static final int YUK_WEB_CONFIG_ID_COOL = -1;
	public static final int YUK_WEB_CONFIG_ID_HEAT = -2;
	
	/**
	 * Remove all future activation events from the hardware or program event list
	 */
	public static void removeFutureActivationEvents(ArrayList custEventHist, LiteStarsEnergyCompany energyCompany) {
		int futureActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		
		try {
			java.util.Iterator it = custEventHist.iterator();
			while (it.hasNext()) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) it.next();
				
				if (liteEvent.getActionID() == futureActID) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					Transaction.createTransaction( Transaction.DELETE, event ).execute();
					it.remove();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Remove future activation events of a particular program from the program event list
	 */
	public static void removeFutureActivationEvents(ArrayList custEventHist, int programID, LiteStarsEnergyCompany energyCompany) {
		int futureActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		int termID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
		
		try {
			for (int i = custEventHist.size() - 1; i >= 0; i--) {
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) custEventHist.get(i);
				if (liteEvent.getProgramID() != programID) continue;
				
				if (liteEvent.getActionID() == termID) break;
				
				if (liteEvent.getActionID() == futureActID) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					Transaction.createTransaction( Transaction.DELETE, event ).execute();
					custEventHist.remove( i );
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static StarsThermoModeSettings getThermSeasonMode(int configID) {
		if (configID == YUK_WEB_CONFIG_ID_COOL)
			return StarsThermoModeSettings.COOL;
		else if (configID == YUK_WEB_CONFIG_ID_HEAT)
			return StarsThermoModeSettings.HEAT;
		return null;
	}
	
	public static int getThermSeasonWebConfigID(StarsThermoModeSettings mode) {
		if (mode.getType() == StarsThermoModeSettings.COOL_TYPE)
			return YUK_WEB_CONFIG_ID_COOL;
		else if (mode.getType() == StarsThermoModeSettings.HEAT_TYPE)
			return YUK_WEB_CONFIG_ID_HEAT;
		return 0;
	}
	
	public static StarsThermoDaySettings getThermDaySetting(int towID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( towID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY)
			return StarsThermoDaySettings.WEEKDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKEND)
			return StarsThermoDaySettings.WEEKEND;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY)
			return StarsThermoDaySettings.MONDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY)
			return StarsThermoDaySettings.TUESDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY)
			return StarsThermoDaySettings.WEDNESDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY)
			return StarsThermoDaySettings.THURSDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY)
			return StarsThermoDaySettings.FRIDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY)
			return StarsThermoDaySettings.SATURDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY)
			return StarsThermoDaySettings.SUNDAY;
		
		return null;
	}
	
	public static int getThermSeasonEntryTOWID(StarsThermoDaySettings setting, LiteStarsEnergyCompany energyCompany) {
		if (setting.getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.WEEKEND_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKEND).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.MONDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_MONDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.TUESDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_TUESDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.WEDNESDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEDNESDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.THURSDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_THURSDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.FRIDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_FRIDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.SATURDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY).getEntryID();
		else if (setting.getType() == StarsThermoDaySettings.SUNDAY_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY).getEntryID();
			
		return 0;
	}
	
	public static StarsThermoModeSettings getThermModeSetting(int opStateID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( opStateID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT)
			return null;
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL)
			return StarsThermoModeSettings.COOL;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT)
			return StarsThermoModeSettings.HEAT;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF)
			return StarsThermoModeSettings.OFF;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_AUTO)
			return StarsThermoModeSettings.AUTO;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_EMERGENCY_HEAT)
			return StarsThermoModeSettings.EMGHEAT;
		
		return null;
	}
	
	public static int getThermOptionOpStateID(StarsThermoModeSettings setting, LiteStarsEnergyCompany energyCompany) {
		if (setting == null)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT).getEntryID();
		if (setting.getType() == StarsThermoModeSettings.COOL_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL).getEntryID();
		else if (setting.getType() == StarsThermoModeSettings.HEAT_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT).getEntryID();
		else if (setting.getType() == StarsThermoModeSettings.OFF_TYPE)
			return energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF).getEntryID();
		
		return 0;
	}
	
	public static StarsThermoFanSettings getThermFanSetting(int fanOpID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( fanOpID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT)
			return null;
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO)
			return StarsThermoFanSettings.AUTO;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON)
			return StarsThermoFanSettings.ON;
		else
			return null;
	}
	
	public static Integer getThermOptionFanOpID(StarsThermoFanSettings setting, LiteStarsEnergyCompany energyCompany) {
		if (setting == null)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT).getEntryID() );
		if (setting.getType() == StarsThermoFanSettings.AUTO_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO).getEntryID() );
		else if (setting.getType() == StarsThermoFanSettings.ON_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON).getEntryID() );
		else
			return null;
	}
	
	public static StarsThermostatTypes getThermostatType(int hwTypeID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( hwTypeID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT)
			return StarsThermostatTypes.EXPRESSSTAT;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
			return StarsThermostatTypes.ENERGYPRO;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT)
			return StarsThermostatTypes.COMMERCIAL;
		else
			return null;
	}
	
	public static Integer getLMHardwareTypeDefID(StarsThermostatTypes type) {
		if (type.getType() == StarsThermostatTypes.EXPRESSSTAT_TYPE)
			return new Integer(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT);
		else if (type.getType() == StarsThermostatTypes.ENERGYPRO_TYPE)
			return new Integer(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO);
		else if (type.getType() == StarsThermostatTypes.COMMERCIAL_TYPE)
			return new Integer(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT);
		else
			return null;
	}
	
	public static StarsLoginStatus getLoginStatus(String status) {
		if (status.equalsIgnoreCase( UserUtils.STATUS_ENABLED ))
			return StarsLoginStatus.ENABLED;
		else if (status.equalsIgnoreCase( UserUtils.STATUS_DISABLED ))
			return StarsLoginStatus.DISABLED;
		else
			return null;
	}
	
	public static String getUserStatus(StarsLoginStatus status) {
		if (status.getType() == StarsLoginStatus.ENABLED_TYPE)
			return UserUtils.STATUS_ENABLED;
		else if (status.getType() == StarsLoginStatus.DISABLED_TYPE)
			return UserUtils.STATUS_DISABLED;
		else
			return null;
	}
	
	public static int getInventoryCategoryID(int deviceTypeID, LiteStarsEnergyCompany energyCompany) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( deviceTypeID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT)
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
	
	/**
	 * Get all the energy companies that belongs to (directly or indirectly)
	 * this energy company, including itself
	 */
	public static ArrayList getAllDescendants(LiteStarsEnergyCompany parent) {
		ArrayList descendants = new ArrayList();
		descendants.add( parent );
		
		for (int i = 0; i < parent.getChildren().size(); i++) {
			LiteStarsEnergyCompany child = (LiteStarsEnergyCompany) parent.getChildren().get(i);
			descendants.addAll( getAllDescendants(child) );
		}
		
		return descendants;
	}
	
	public static boolean isDefaultEnergyCompany(LiteStarsEnergyCompany company) {
		return company.getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID;
	}

}
