package com.cannontech.stars.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.stars.LiteAddress;
import com.cannontech.database.data.lite.stars.LiteCustomerContact;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.types.StarsThermoDaySettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ServerUtils {

    // Increment this for every message
    private static long userMessageIDCounter = 1;
    
    private static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM-dd-yy HH:mm");
    
    // If date in database is earlier than this, than the date is actually empty
    private static long VERY_EARLY_TIME = 1000 * 3600 * 24;

	public static final String CTI_NUMBER = "CTI#";
	

    
	
    public static void sendCommand(String command)
    {
    	com.cannontech.message.porter.ClientConnection conn = SOAPServer.getInstance().getPILConnection();
    	if (conn == null) {
			CTILogger.error( "Cannot get PIL client connection" );
			return;
		}
		
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );
        conn.write( req );
        
        CTILogger.debug( "Sent command to PIL: " + command );
    }
    
    public static void saveCommands(String fileName, String[] commands) throws IOException {
    	if (fileName == null) return;
    	
		File f = new File( fileName );
		if (!f.exists()) {
			File dir = new File( f.getParent() );
			if (!dir.exists()) dir.mkdirs();
			f.createNewFile();
		}
		
		PrintWriter fw = null;
		try {
			fw = new PrintWriter( new FileWriter(f, true) );
			for (int i = 0; i < commands.length; i++)
				fw.println( commands[i] );
		}
		finally {
			if (fw != null) fw.close();
		}
    }
	
	public static void removeFutureActivationEvents(ArrayList custEventHist, LiteStarsEnergyCompany energyCompany) {
		int futureActID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		int termID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION ).getEntryID();
		
		try {
			for (int i = custEventHist.size() - 1; i >= 0; i--) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
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
	
	public static void sendEmailMsg(String from, String[] to, String[] cc, String subject, String text) throws Exception {
		CtiProperties props = CtiProperties.getInstance();
		Session session = Session.getDefaultInstance( props, null );
		
		Message emailMsg = new MimeMessage( session );
		emailMsg.setFrom( new InternetAddress(from) );
		
		Address[] toAddrs = new Address[ to.length ];
		for (int i = 0; i < to.length; i++)
			toAddrs[i] = new InternetAddress( to[i] );
		emailMsg.setRecipients( Message.RecipientType.TO, toAddrs );
		
		if (cc != null) {
			Address[] ccAddrs = new Address[ cc.length ];
			for (int i = 0; i < to.length; i++)
				ccAddrs[i] = new InternetAddress( cc[i] );
			emailMsg.setRecipients( Message.RecipientType.CC, ccAddrs );
		}
		
		emailMsg.setSubject( subject );
		emailMsg.setSentDate( new Date() );
		emailMsg.setText( text );
		
		Transport.send( emailMsg );
	}
	
	public static String formatDate(Date date, TimeZone tz) {
		if (tz != null)
			dateFormat.setTimeZone( tz );
		else
			dateFormat.setTimeZone( TimeZone.getDefault() );
		return dateFormat.format( date );
	}
	
	public static StarsThermoModeSettings getThermSeasonMode(int configID) {
		if (configID == SOAPServer.YUK_WEB_CONFIG_ID_COOL)
			return StarsThermoModeSettings.COOL;
		else if (configID == SOAPServer.YUK_WEB_CONFIG_ID_HEAT)
			return StarsThermoModeSettings.HEAT;
		return null;
	}
	
	public static int getThermSeasonWebConfigID(StarsThermoModeSettings mode) {
		if (mode.getType() == StarsThermoModeSettings.COOL_TYPE)
			return SOAPServer.YUK_WEB_CONFIG_ID_COOL;
		else if (mode.getType() == StarsThermoModeSettings.HEAT_TYPE)
			return SOAPServer.YUK_WEB_CONFIG_ID_HEAT;
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
	
	public static int getThermSeasonEntryTOWID(StarsThermoDaySettings setting, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany(energyCompanyID);
		
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
	
	public static int getThermOptionOpStateID(StarsThermoModeSettings setting, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany(energyCompanyID);
		
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
		
		if (fanOpID == YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT)
			return null;
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO)
			return StarsThermoFanSettings.AUTO;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON)
			return StarsThermoFanSettings.ON;
		else
			return null;
	}
	
	public static Integer getThermOptionFanOpID(StarsThermoFanSettings setting, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany(energyCompanyID);
		
		if (setting == null)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT).getEntryID() );
		if (setting.getType() == StarsThermoFanSettings.AUTO_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_AUTO).getEntryID() );
		else if (setting.getType() == StarsThermoFanSettings.ON_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_ON).getEntryID() );
		else
			return null;
	}
	
	public static boolean isOperator(StarsYukonUser user) {
		return (AuthFuncs.checkRole(user.getYukonUser(), ConsumerInfoRole.ROLEID) != null);
	}
	
	public static boolean isResidentialCustomer(StarsYukonUser user) {
		return (AuthFuncs.checkRole(user.getYukonUser(), ResidentialCustomerRole.ROLEID) != null);
	}
	
	public static void handleDBChange(com.cannontech.database.data.lite.LiteBase lite, int typeOfChange) {
		DBChangeMsg msg = null;
		
		if (lite == null) {
			msg = new DBChangeMsg( 0, Integer.MAX_VALUE, "", "", typeOfChange );
		}
		else if (lite.getLiteType() == LiteTypes.STARS_CUST_ACCOUNT_INFO) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
				DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
				DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.YUKON_USER) {
	    	msg = new DBChangeMsg(
	    		lite.getLiteID(),
	    		DBChangeMsg.CHANGE_YUKON_USER_DB,
	    		DBChangeMsg.CAT_YUKON_USER,
	    		DBChangeMsg.CAT_YUKON_USER,
	    		typeOfChange
	    		);
		}
		else if (lite.getLiteType() == LiteTypes.CONTACT || lite.getLiteType() == LiteTypes.STARS_CUSTOMER_CONTACT) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_CONTACT_DB,
				DBChangeMsg.CAT_CUSTOMERCONTACT,
				DBChangeMsg.CAT_CUSTOMERCONTACT,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.YUKON_GROUP) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_YUKON_USER_DB,
				DBChangeMsg.CAT_YUKON_USER_GROUP,
				DBChangeMsg.CAT_YUKON_USER_GROUP,
				typeOfChange
				);
		}
		else if (lite.getLiteType() == LiteTypes.ENERGY_COMPANY || lite.getLiteType() == LiteTypes.ENERGY_COMPANY) {
			msg = new DBChangeMsg(
				lite.getLiteID(),
				DBChangeMsg.CHANGE_ENERGY_COMPANY_DB,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				DBChangeMsg.CAT_ENERGY_COMPANY,
				typeOfChange
				);
		}
		
		DefaultDatabaseCache.getInstance().handleDBChangeMessage( msg );
		
		com.cannontech.message.util.ClientConnection conn = SOAPServer.getInstance().getClientConnection();
		if (conn == null) {
			CTILogger.error( "Cannot get dispatch client connection" );
			return;
		}
    	conn.write( msg );
	}
	
	public static Date translateDate(long time) {
		if (time < VERY_EARLY_TIME) return null;
		return new Date(time);
	}

	public static String forceNotNull(String str) {
		return (str == null) ? "" : str.trim();
	}

	public static String forceNotNone(String str) {
		String str1 = forceNotNull(str);
		return (str1.equalsIgnoreCase("(none)")) ? "" : str1;
	}
	
	public static boolean hasTwoWayThermostat(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany) {
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			int invID = ((Integer) liteAcctInfo.getInventories().get(i)).intValue();
			LiteStarsLMHardware liteHw = energyCompany.getLMHardware( invID, true );
			if (liteHw.isTwoWayThermostat())
				return true;
		}
		return false;
	}
	
	public static String getFormattedName(LiteCustomerContact liteContact) {
		StringBuffer name = new StringBuffer();
		
		String firstName = forceNotNone( liteContact.getFirstName() ).trim();
		if (firstName.length() > 0)
			name.append( firstName );
		
		String lastName = forceNotNone( liteContact.getLastName() ).trim();
		if (lastName.length() > 0)
			name.append(" ").append( lastName );
			
		if (name.length() == 0) name.append("(none)");
		return name.toString();
	}
	
	public static String getOneLineAddress(LiteAddress liteAddr) {
		StringBuffer addr = new StringBuffer();
		
		String locationAddr1 = forceNotNone( liteAddr.getLocationAddress1() ).trim();
		if (locationAddr1.length() > 0)
			addr.append( locationAddr1 );
			
		String locationAddr2 = forceNotNone( liteAddr.getLocationAddress2() ).trim();
		if (locationAddr2.length() > 0) {
			if (addr.length() > 0) addr.append(", ");
			addr.append( locationAddr2 );
		}
		
		String cityName = forceNotNone( liteAddr.getCityName() ).trim();
		if (cityName.length() > 0) {
			if (addr.length() > 0) addr.append(", ");
			addr.append( cityName );
		}
		
		String stateCode = forceNotNone( liteAddr.getStateCode() ).trim();
		if (stateCode.length() > 0) {
			if (addr.length() > 0) addr.append(", ");
			addr.append( stateCode );
			
			String zipCode = forceNotNone( liteAddr.getZipCode() ).trim();
			if (zipCode.length() > 0)
				addr.append(" ").append( zipCode );
		}
		
		if (addr.length() == 0) addr.append("Address N/A");
		return addr.toString();
	}

}
