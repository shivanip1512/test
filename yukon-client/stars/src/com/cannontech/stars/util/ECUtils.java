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
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.StarsWebConfig;
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
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_1000 ||
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
	
	public static String getPublishedProgramName(LiteLMProgram liteProg, LiteStarsEnergyCompany energyCompany) {
		String progName = liteProg.getProgramName();
		
		StarsWebConfig config = energyCompany.getStarsWebConfig( liteProg.getWebSettingsID() );
		if (config != null) {
			String[] dispNames = ServerUtils.splitString( config.getAlternateDisplayName(), "," );
			if (dispNames.length > 0 && dispNames[0].length() > 0)
				progName = dispNames[0];
		}
		
		return progName;
	}

}
