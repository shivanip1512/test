/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.stars.LiteLMConfiguration;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.stars.web.StarsYukonUser;
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
	
	public static final int SA205_UNUSED_ADDR = 3909;
	
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
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000 ||
			entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_1000 ||
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
	
	public static boolean isExpressCom(int devTypeID) {
		int devTypeDefID = YukonListFuncs.getYukonListEntry( devTypeID ).getYukonDefID();
		return (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_XCOM
				|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT
				|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_COMM_EXPRESSSTAT
				|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO);
	}
	
	public static boolean isVersaCom(int devTypeID) {
		int devTypeDefID = YukonListFuncs.getYukonListEntry( devTypeID ).getYukonDefID();
		return (devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_5000_VCOM
				|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_4000
				|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_3000
				|| devTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_LCR_2000);
	}
	
	public static boolean isSA205(int devTypeID) {
		return YukonListFuncs.getYukonListEntry( devTypeID ).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205;
	}
	
	public static boolean isSA305(int devTypeID) {
		return YukonListFuncs.getYukonListEntry( devTypeID ).getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA305;
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
	
	/**
	 * @return Whether company1 is a descendant of company2
	 */
	public static boolean isDescendantOf(LiteStarsEnergyCompany company1, LiteStarsEnergyCompany company2) {
		if (company1.equals( company2 )) return false;
		ArrayList descendants = getAllDescendants( company2 );
		return descendants.contains( company1 );
	}
	
	public static boolean isDefaultEnergyCompany(LiteStarsEnergyCompany company) {
		return company.getLiteID() == SOAPServer.DEFAULT_ENERGY_COMPANY_ID;
	}

	public static String getNotification(LiteContactNotification liteNotif) {
		String notification = (liteNotif == null)? null : liteNotif.getNotification();
		return ServerUtils.forceNotNull(notification);
	}

	public static String formatName(LiteContact liteContact) {
		StringBuffer name = new StringBuffer();
		
		String firstName = ServerUtils.forceNotNone( liteContact.getContFirstName() ).trim();
		if (firstName.length() > 0)
			name.append( firstName );
		
		String lastName = ServerUtils.forceNotNone( liteContact.getContLastName() ).trim();
		if (lastName.length() > 0)
			name.append(" ").append( lastName );
		
		return name.toString();
	}

	public static String getPublishedProgramName(LiteLMProgramWebPublishing liteProg) {
		String progName = CtiUtilities.STRING_NONE;
		
		if (liteProg.getDeviceID() > 0)
			progName = PAOFuncs.getYukonPAOName( liteProg.getDeviceID() );
		
		LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( liteProg.getWebSettingsID() );
		if (liteConfig != null) {
			String[] dispNames = ServerUtils.splitString( liteConfig.getAlternateDisplayName(), "," );
			if (dispNames.length > 0 && dispNames[0].length() > 0)
				progName = dispNames[0];
		}
		
		return progName;
	}

	public static boolean isOperator(StarsYukonUser user) {
		return !isResidentialCustomer(user) &&
				EnergyCompanyFuncs.getEnergyCompany( user.getYukonUser() ) != null;
	}

	public static boolean isResidentialCustomer(StarsYukonUser user) {
		return AuthFuncs.checkRole(user.getYukonUser(), ResidentialCustomerRole.ROLEID) != null;
	}
	
	/**
	 * Based on the hardware addressing and relay number, get all the load groups
	 * that could control the corresponding load.
	 */
	public static int[] getControllableGroupIDs(LiteLMConfiguration liteCfg, int relayNo) {
		if (relayNo <= 0) return new int[0];
		
		ArrayList groupIDs = new ArrayList();
		
		try {
			if (liteCfg.getExpressCom() != null) {
				String sql = "SELECT LMGroupID, addr1.Address, addr2.Address, addr3.Address, ZipCodeAddress, UDAddress, addr4.Address, SplinterAddress, AddressUsage, RelayUsage"
						+ " FROM LMGroupExpresscom, LMGroupExpresscomAddress addr1, LMGroupExpresscomAddress addr2, LMGroupExpresscomAddress addr3, LMGroupExpresscomAddress addr4, LMGroupExpresscomAddress addr5"
						+ " WHERE SerialNumber = 0 AND ServiceProviderID = addr5.AddressID AND addr5.Address = " + liteCfg.getExpressCom().getServiceProvider()
						+ " AND GeoID = addr1.AddressID AND SubstationID = addr2.AddressID AND FeederID = addr3.AddressID AND ProgramID = addr4.AddressID";
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					int geoAddress = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
					int substationAddress = ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue();
					int feederAddress = ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue();
					int zipCodeAddress = ((java.math.BigDecimal) stmt.getRow(i)[4]).intValue();
					int udAddress = ((java.math.BigDecimal) stmt.getRow(i)[5]).intValue();
					int programAddress = ((java.math.BigDecimal) stmt.getRow(i)[6]).intValue();
					int splinterAddress = ((java.math.BigDecimal) stmt.getRow(i)[7]).intValue();
					String addressUsage = (String) stmt.getRow(i)[8];
					String relayUsage = (String) stmt.getRow(i)[9];
					
					if (addressUsage.indexOf("G") >= 0 && liteCfg.getExpressCom().getGEO() != geoAddress) continue;
					if (addressUsage.indexOf("B") >= 0 && liteCfg.getExpressCom().getSubstation() != substationAddress) continue;
					if (addressUsage.indexOf("F") >= 0 && liteCfg.getExpressCom().getFeeder() != feederAddress) continue;
					if (addressUsage.indexOf("Z") >= 0 && liteCfg.getExpressCom().getZip() != zipCodeAddress) continue;
					if (addressUsage.indexOf("U") >= 0 && liteCfg.getExpressCom().getUserAddress() != udAddress) continue;
					if (addressUsage.indexOf("L") >= 0) {
						if (relayUsage.indexOf( Character.forDigit(relayNo, 10) ) < 0) continue;
					}
					else {
						if (addressUsage.indexOf("P") >= 0) {
							int program = 0;
							String[] programs = liteCfg.getExpressCom().getProgram().split(",");
							if (programs.length >= relayNo && programs[relayNo-1].length() > 0)
								program = Integer.parseInt( programs[relayNo-1] );
							if (program != programAddress) continue;
						}
						if (addressUsage.indexOf("R") >= 0) {
							int splinter = 0;
							String[] splinters = liteCfg.getExpressCom().getSplinter().split(",");
							if (splinters.length >= relayNo && splinters[relayNo-1].length() > 0)
								splinter = Integer.parseInt( splinters[relayNo-1] );
							if (splinter != splinterAddress) continue;
						}
					}
					
					groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getVersaCom() != null) {
				String sql = "SELECT DeviceID, SectionAddress, classAddress, divisionAddress, AddressUsage, RelayUsage"
						+ " FROM LMGroupVersacom WHERE SerialAddress = 0"
						+ " AND UtilityAddress = " + liteCfg.getVersaCom().getUtilityID();
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					int sectionAddress = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
					int classAddress = ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue();
					int divisionAddress = ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue();
					String addressUsage = (String) stmt.getRow(i)[4];
					String relayUsage = (String) stmt.getRow(i)[5];
					
					if (addressUsage.indexOf("S") >= 0 && liteCfg.getVersaCom().getSection() != sectionAddress) continue;
					if (addressUsage.indexOf("C") >= 0 && liteCfg.getVersaCom().getClassAddress() != classAddress) continue;
					if (addressUsage.indexOf("D") >= 0 && liteCfg.getVersaCom().getDivisionAddress() != divisionAddress) continue;
					if (relayUsage.indexOf( Character.forDigit(relayNo, 10) ) < 0) continue;
					
					groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getSA205() != null) {
				String sql = "SELECT GroupID, OperationalAddress FROM LMGroupSA205105 WHERE ";
				if (relayNo == 1)
					sql += "LoadNumber='Load 1' OR LoadNumber='Load 1,2' OR LoadNumber='Load 1,2,3' OR LoadNumber='Load 1,2,3,4'";
				else if (relayNo == 2)
					sql += "LoadNumber='Load 2' OR LoadNumber='Load 1,2' OR LoadNumber='Load 1,2,3' OR LoadNumber='Load 1,2,3,4'";
				else if (relayNo == 3)
					sql += "LoadNumber='Load 3' OR LoadNumber='Load 1,2,3' OR LoadNumber='Load 1,2,3,4'";
				else if (relayNo == 4)
					sql += "LoadNumber='Load 4' OR LoadNumber='Load 1,2,3,4'";
				else
					return new int[0];
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					int operationalAddress = ((java.math.BigDecimal) stmt.getRow(i)[1]).intValue();
					
					if (operationalAddress == liteCfg.getSA205().getSlot1()
						|| operationalAddress == liteCfg.getSA205().getSlot2()
						|| operationalAddress == liteCfg.getSA205().getSlot3()
						|| operationalAddress == liteCfg.getSA205().getSlot4()
						|| operationalAddress == liteCfg.getSA205().getSlot5()
						|| operationalAddress == liteCfg.getSA205().getSlot6())
						groupIDs.add( new Integer(groupID) );
				}
			}
			else if (liteCfg.getSA305() != null) {
				String sql = "SELECT GroupID, AddressUsage, GroupAddress, DivisionAddress, SubstationAddress, LoadNumber"
						+ " FROM LMGroupSA305 WHERE AddressUsage <> 'U' AND LoadNumber <> ''"
						+ " AND UtilityAddress = " + liteCfg.getSA305().getUtility()
						+ " AND RateFamily = " + liteCfg.getSA305().getRateFamily()
						+ " AND RateMember = " + liteCfg.getSA305().getRateMember()
						+ " AND RateHierarchy = " + liteCfg.getSA305().getRateHierarchy();
				
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int groupID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					String addressUsage = (String) stmt.getRow(i)[1];
					int groupAddress = ((java.math.BigDecimal) stmt.getRow(i)[2]).intValue();
					int divisionAddress = ((java.math.BigDecimal) stmt.getRow(i)[3]).intValue();
					int substationAddress = ((java.math.BigDecimal) stmt.getRow(i)[4]).intValue();
					String loadNumber = (String) stmt.getRow(i)[5];
					
					if (addressUsage.indexOf("G") >= 0 && liteCfg.getSA305().getGroup() != groupAddress) continue;
					if (addressUsage.indexOf("D") >= 0 && liteCfg.getSA305().getDivision() != divisionAddress) continue;
					if (addressUsage.indexOf("S") >= 0 && liteCfg.getSA305().getSubstation() != substationAddress) continue;
					if (loadNumber.indexOf( Character.forDigit(relayNo, 10) ) < 0) continue;
					
					groupIDs.add( new Integer(groupID) );
				}
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			return null;
		}
		
		int[] ids = new int[ groupIDs.size() ];
		for (int i = 0; i < groupIDs.size(); i++)
			ids[i] = ((Integer) groupIDs.get(i)).intValue();
		return ids;
	}
	
	public static ArrayList getLMHardwareInRange(LiteStarsEnergyCompany energyCompany, int devTypeID, Integer snFrom, Integer snTo) {
		ArrayList hwList = new ArrayList();
		
		ArrayList inventory = energyCompany.loadAllInventory();
		synchronized (inventory) {
			for (int i = 0; i < inventory.size(); i++) {
				if (!(inventory.get(i) instanceof LiteStarsLMHardware)) continue;
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) inventory.get(i);
				
				if (liteHw.getLmHardwareTypeID() != devTypeID) continue;
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
