package com.cannontech.stars.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.stars.CustomerSelectionList;
import com.cannontech.database.db.stars.CustomerListEntry;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.message.porter.ClientConnection;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ServerUtils {
    
    private static PILConnectionServlet connContainer = null;

    // Increment this for every message
    private static long userMessageIDCounter = 1;
    
    private static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM-dd-yy HH:mm");

    public static void sendCommand(String command, ClientConnection conn)
    {
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

        conn.write(req);

        CTILogger.debug( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
    
    public static void setConnectionContainer(PILConnectionServlet servlet) {
    	connContainer = servlet;
    }
    
    public static ClientConnection getClientConnection() {
    	if (connContainer == null) return null;
    	return connContainer.getConnection();
    }
	
	public static void processFutureActivation(ArrayList custEventHist, Integer futureActEntryID, Integer actCompEntryID) {
		try {
			ArrayList eventToBeRemoved = new ArrayList();
			
			for (int i = 0; i < custEventHist.size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
				if (liteEvent.getActionID() == futureActEntryID.intValue()) {
					com.cannontech.database.data.stars.event.LMCustomerEventBase event = (com.cannontech.database.data.stars.event.LMCustomerEventBase)
							StarsLiteFactory.createDBPersistent( liteEvent );
					com.cannontech.database.db.stars.event.LMCustomerEventBase eventDB = event.getLMCustomerEventBase();
							
					if (liteEvent.getEventDateTime() < new Date().getTime()) {
						// Future activation time earlier than current time, change the entry to "Activation Completed"
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.RETRIEVE, eventDB ).execute();
						eventDB.setActionID( actCompEntryID );
						eventDB = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, eventDB ).execute();
								
						liteEvent.setActionID( actCompEntryID.intValue() );
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
	
	public static void removeFutureActivation(ArrayList custEventHist, Integer futureActEntryID) {
		try {
			ArrayList eventToBeRemoved = new ArrayList();
			
			for (int i = 0; i < custEventHist.size(); i++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) custEventHist.get(i);
				if (liteEvent.getActionID() == futureActEntryID.intValue()) {
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
	
	public static boolean isInService(ArrayList progHist, Integer futureActEntryID, Integer actCompEntryID) {
		for (int i = progHist.size() - 1; i >= 0 ; i--) {
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) progHist.get(i);
			if (liteEvent.getActionID() == futureActEntryID.intValue())
				return false;
			if (liteEvent.getActionID() == actCompEntryID.intValue())
				return true;
		}
		
		return false;
	}
	
	public static void updateServiceCompanies(LiteStarsCustAccountInformation liteAcctInfo, int energyCompanyID) {
		ArrayList companyList = new ArrayList();
		liteAcctInfo.setServiceCompanies( companyList );
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			Integer invID = (Integer) liteAcctInfo.getInventories().get(i);
			LiteLMHardwareBase liteHw = SOAPServer.getLMHardware( energyCompanyID, invID.intValue(), true );
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
	
	public static StarsThermoDaySettings getThermDaySetting(int towID, Hashtable selectionLists) {
		StarsSelectionListEntry entry = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_TIMEOFWEEK), towID );
		
		if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_TOW_WEEKDAY ))
			return StarsThermoDaySettings.WEEKDAY;
		else if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_TOW_WEEKEND ))
			return StarsThermoDaySettings.WEEKEND;
		else if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_TOW_SATURDAY ))
			return StarsThermoDaySettings.SATURDAY;
		else if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_TOW_SUNDAY ))
			return StarsThermoDaySettings.SUNDAY;
		else
			return null;
	}
	
	public static Integer getThermSeasonEntryTOWID(StarsThermoDaySettings setting, Hashtable selectionLists) {
		LiteCustomerSelectionList towList = (LiteCustomerSelectionList) selectionLists.get(
				CustomerSelectionList.LISTNAME_TIMEOFWEEK );
		
		if (setting.getType() == StarsThermoDaySettings.WEEKDAY_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(towList, CustomerListEntry.YUKONDEF_TOW_WEEKDAY).getEntryID() );
		else if (setting.getType() == StarsThermoDaySettings.WEEKEND_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(towList, CustomerListEntry.YUKONDEF_TOW_WEEKEND).getEntryID() );
		else if (setting.getType() == StarsThermoDaySettings.SATURDAY_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(towList, CustomerListEntry.YUKONDEF_TOW_SATURDAY).getEntryID() );
		else if (setting.getType() == StarsThermoDaySettings.SUNDAY_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(towList, CustomerListEntry.YUKONDEF_TOW_SUNDAY).getEntryID() );
		else
			return null;
	}
	
	public static StarsThermoModeSettings getThermModeSetting(int opStateID, Hashtable selectionLists) {
		StarsSelectionListEntry entry = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_THERMOSTATMODE), opStateID );
		
		if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_THERMMODE_COOL ))
			return StarsThermoModeSettings.COOL;
		else if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_THERMMODE_HEAT ))
			return StarsThermoModeSettings.HEAT;
		else if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_THERMMODE_OFF ))
			return StarsThermoModeSettings.OFF;
		else
			return null;
	}
	
	public static Integer getThermOptionOpStateID(StarsThermoModeSettings setting, Hashtable selectionLists) {
		LiteCustomerSelectionList modeList = (LiteCustomerSelectionList) selectionLists.get(
				CustomerSelectionList.LISTNAME_THERMOSTATMODE );
		
		if (setting.getType() == StarsThermoModeSettings.COOL_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(modeList, CustomerListEntry.YUKONDEF_THERMMODE_COOL).getEntryID() );
		else if (setting.getType() == StarsThermoModeSettings.HEAT_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(modeList, CustomerListEntry.YUKONDEF_THERMMODE_HEAT).getEntryID() );
		else if (setting.getType() == StarsThermoModeSettings.OFF_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(modeList, CustomerListEntry.YUKONDEF_THERMMODE_OFF).getEntryID() );
		else
			return null;
	}
	
	public static StarsThermoFanSettings getThermFanSetting(int fanOpID, Hashtable selectionLists) {
		StarsSelectionListEntry entry = StarsCustListEntryFactory.getStarsCustListEntry(
				(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_THERMFANSTATE), fanOpID );
		
		if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_FANSTATE_AUTO ))
			return StarsThermoFanSettings.AUTO;
		else if (entry.getYukonDefinition().equals( CustomerListEntry.YUKONDEF_FANSTATE_ON ))
			return StarsThermoFanSettings.ON;
		else
			return null;
	}
	
	public static Integer getThermOptionFanOpID(StarsThermoFanSettings setting, Hashtable selectionLists) {
		LiteCustomerSelectionList fanList = (LiteCustomerSelectionList) selectionLists.get(
				CustomerSelectionList.LISTNAME_THERMFANSTATE );
		
		if (setting.getType() == StarsThermoFanSettings.AUTO_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(fanList, CustomerListEntry.YUKONDEF_FANSTATE_AUTO).getEntryID() );
		else if (setting.getType() == StarsThermoFanSettings.ON_TYPE)
			return new Integer( StarsCustListEntryFactory.getStarsCustListEntry(fanList, CustomerListEntry.YUKONDEF_FANSTATE_ON).getEntryID() );
		else
			return null;
	}
}
