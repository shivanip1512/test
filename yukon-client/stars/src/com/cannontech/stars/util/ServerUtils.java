package com.cannontech.stars.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.cannontech.common.constants.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;
import com.cannontech.stars.xml.StarsCustListEntryFactory;

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
        
        CTILogger.debug( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
	
	public static void processFutureActivation(ArrayList custEventHist, int futureActEntryID, int actCompEntryID) {
		try {
			ArrayList eventToBeRemoved = new ArrayList();
			
			for (int i = 0; i < custEventHist.size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
				if (liteEvent.getActionID() == futureActEntryID) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
							
					if (liteEvent.getEventDateTime() < new Date().getTime()) {
						// Future activation time earlier than current time, change the entry to "Activation Completed"
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.RETRIEVE, eventDB ).execute();
						eventDB.setActionID( new Integer(actCompEntryID) );
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
								
						liteEvent.setActionID( actCompEntryID );
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
	
	public static void removeFutureActivation(ArrayList custEventHist, int futureActEntryID) {
		try {
			ArrayList eventToBeRemoved = new ArrayList();
			
			for (int i = 0; i < custEventHist.size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
				if (liteEvent.getActionID() == futureActEntryID) {
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
	
	public static boolean isInService(ArrayList progHist, int futureActEntryID, int actCompEntryID) {
		for (int i = progHist.size() - 1; i >= 0 ; i--) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) progHist.get(i);
			if (liteEvent.getActionID() == futureActEntryID)
				return false;
			if (liteEvent.getActionID() == actCompEntryID)
				return true;
		}
		
		return false;
	}
	
	public static void updateServiceCompanies(LiteStarsCustAccountInformation liteAcctInfo, int energyCompanyID) {
		ArrayList companyList = new ArrayList();
		liteAcctInfo.setServiceCompanies( companyList );
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			Integer invID = (Integer) liteAcctInfo.getInventories().get(i);
			LiteLMHardwareBase liteHw = SOAPServer.getEnergyCompany(energyCompanyID).getLMHardware( invID.intValue(), true );
			Integer companyID = new Integer( liteHw.getInstallationCompanyID() );
			if (!companyList.contains( companyID ))
				companyList.add( companyID );
		}
	}
	
	public static void sendEmailMsg(String from, String[] to, String[] cc, String subject, String text) throws Exception {
		Properties props = new Properties();
		props.load( ServerUtils.class.getResourceAsStream("/config.properties") );
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

}
