package com.cannontech.stars.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.RoleTypes;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
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
    
    public static final String DEFAULT_PROPERTY_FILE = "/default.config.properties";
    
    public static final String ADMIN_EMAIL_ADDRESS = "admin_email_address";
    public static final String OPTOUT_NOTIFICATION_RECIPIENTS = "optout_notification_recipients";
    public static final String DEFAULT_TIME_ZONE = "default_time_zone";
    public static final String SWITCH_COMMAND_FILE = "switch_command_file";
    public static final String OPTOUT_COMMAND_FILE = "optout_command_file";
    public static final String CUSTOMER_GROUP_NAME = "customer_group_name";
    
    public static final String[] ALL_SETTINGS_KEYS = {
    	ADMIN_EMAIL_ADDRESS,
    	OPTOUT_NOTIFICATION_RECIPIENTS,
    	DEFAULT_TIME_ZONE,
    	SWITCH_COMMAND_FILE,
    	OPTOUT_COMMAND_FILE,
    	CUSTOMER_GROUP_NAME,
    };
    
    public static final String DEFAULT_ADMIN_EMAIL_ADDRESS = "info@cannontech.com";
    public static final String DEFAULT_BATCH_COMMAND_FOLDER = "c:\\yukon\\batch_command";
    
	
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
	
	public static void replaceLMCustomEvents(ArrayList custEventHist, int oldActionID, int newActionID) {
		try {
			ArrayList eventToBeRemoved = new ArrayList();
			
			for (int i = 0; i < custEventHist.size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
				if (liteEvent.getActionID() == oldActionID) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
							
					if (liteEvent.getEventDateTime() < new Date().getTime()) {
						// Future activation time earlier than current time, change the entry to "Activation Completed"
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.RETRIEVE, eventDB ).execute();
						eventDB.setActionID( new Integer(newActionID) );
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
								
						liteEvent.setActionID( newActionID );
					}
					else {
						// Future activation time not reached yet, delete the entry
						Transaction.createTransaction( Transaction.DELETE, event ).execute();
						eventToBeRemoved.add( liteEvent );
					}
				}
			}
			
			for (int i = 0; i < eventToBeRemoved.size(); i++)
				custEventHist.remove( eventToBeRemoved.get(i) );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeLMCustomEvents(ArrayList custEventHist, int actionID) {
		try {
			ArrayList eventToBeRemoved = new ArrayList();
			
			for (int i = 0; i < custEventHist.size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
				if (liteEvent.getActionID() == actionID) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					Transaction.createTransaction( Transaction.DELETE, event ).execute();
					
					eventToBeRemoved.add( liteEvent );
				}
			}
			
			for (int i = 0; i < eventToBeRemoved.size(); i++)
				custEventHist.remove( eventToBeRemoved.get(i) );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getDeviceStatus(ArrayList hwHist) {
		for (int i = hwHist.size() - 1; i >= 0; i--) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) hwHist.get(i);
			YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
			
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_INSTALL)
				return YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL;
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION)
				return YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL;
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
				return YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
		}
		
		return YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL;
	}
	
	public static boolean isInService(ArrayList progHist) {
		for (int i = progHist.size() - 1; i >= 0 ; i--) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) progHist.get(i);
			YukonListEntry entry = YukonListFuncs.getYukonListEntry( liteEvent.getActionID() );
			
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP)
				return true;
			if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ||
				entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION)
				return false;
		}
		
		return false;
	}
	
	public static void sendEmailMsg(String from, String[] to, String[] cc, String subject, String text) throws Exception {
		CtiProperties props = CtiProperties.getInstance();
		Session session = Session.getDefaultInstance( props, null );
		
		if (from == null) from = DEFAULT_ADMIN_EMAIL_ADDRESS;
		
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
	
	public static String formatDate(Date date) {
		return dateFormat.format( date );
	}
	
	public static StarsThermoDaySettings getThermDaySetting(int towID) {
		YukonListEntry entry = YukonListFuncs.getYukonListEntry( towID );
		
		if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY)
			return StarsThermoDaySettings.WEEKDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKEND)
			return StarsThermoDaySettings.WEEKEND;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY)
			return StarsThermoDaySettings.SATURDAY;
		else if (entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY)
			return StarsThermoDaySettings.SUNDAY;
		else
			return null;
	}
	
	public static Integer getThermSeasonEntryTOWID(StarsThermoDaySettings setting, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany(energyCompanyID);
		
		if (setting.getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY).getEntryID() );
		else if (setting.getType() == StarsThermoDaySettings.WEEKEND_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKEND).getEntryID() );
		else if (setting.getType() == StarsThermoDaySettings.SATURDAY_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_SATURDAY).getEntryID() );
		else if (setting.getType() == StarsThermoDaySettings.SUNDAY_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_TOW_SUNDAY).getEntryID() );
		else
			return null;
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
		else
			return null;
	}
	
	public static Integer getThermOptionOpStateID(StarsThermoModeSettings setting, int energyCompanyID) {
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany(energyCompanyID);
		
		if (setting == null)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT).getEntryID() );
		if (setting.getType() == StarsThermoModeSettings.COOL_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_COOL).getEntryID() );
		else if (setting.getType() == StarsThermoModeSettings.HEAT_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_HEAT).getEntryID() );
		else if (setting.getType() == StarsThermoModeSettings.OFF_TYPE)
			return new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_OFF).getEntryID() );
		else
			return null;
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
		return (AuthFuncs.getRoleValue(user.getYukonUser(), RoleTypes.WEB_OPERATOR) != null);
	}
	
	public static boolean isResidentialCustomer(StarsYukonUser user) {
		return (AuthFuncs.getRoleValue(user.getYukonUser(), RoleTypes.WEB_RESIDENTIAL_CUSTOMER) != null);
	}
	
	public static boolean isCICustomer(StarsYukonUser user) {
		return (AuthFuncs.getRoleValue(user.getYukonUser(), RoleTypes.WEB_CICUSTOMER) != null);
	}
	
	public static void handleDBChange(com.cannontech.database.data.lite.LiteBase lite, int typeOfChange) {
		DBChangeMsg msg = null;
		
		if (lite.getLiteType() == LiteTypes.STARS_CUST_ACCOUNT_INFO) {
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
		
		DefaultDatabaseCache.getInstance().handleDBChangeMessage( msg );
		
		com.cannontech.message.util.ClientConnection conn = SOAPServer.getInstance().getClientConnection();
		if (conn == null) {
			CTILogger.error( "Cannot get dispatch client connection" );
			return;
		}
    	conn.write( msg );
	}
	
	public static boolean callNumberExists(String callNo, int energyCompanyID) throws com.cannontech.common.util.CommandExecutionException {
		String sql = "SELECT CallID FROM CallReportBase call, ECToCallReportMapping map "
				   + "WHERE CallNumber = '" + callNo + "' AND call.CallID = map.CallReportID AND map.EnergyCompanyID = " + energyCompanyID;
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		stmt.execute();
		return (stmt.getRowCount() > 0);
	}
	
	public static boolean orderNumberExists(String orderNo, int energyCompanyID) throws com.cannontech.common.util.CommandExecutionException {
		String sql = "SELECT OrderID FROM WorkOrderBase o, ECToWorkOrderMapping map "
				   + "WHERE OrderNumber = '" + orderNo + "' AND o.OrderID = map.WorkOrderID AND map.EnergyCompanyID = " + energyCompanyID;
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
		
		stmt.execute();
		return (stmt.getRowCount() > 0);
	}
	
	public static Date translateDate(long time) {
		if (time < VERY_EARLY_TIME) return null;
		return new Date(time);
	}

}
