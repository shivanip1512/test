package com.cannontech.stars.util;

import java.util.*;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.serialize.*;
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
	
	public static void updateServiceCompanies(LiteStarsCustAccountInformation liteAcctInfo, Integer energyCompanyID) {
		ArrayList companyList = new ArrayList();
		liteAcctInfo.setServiceCompanies( companyList );
		
		for (int i = 0; i < liteAcctInfo.getInventories().size(); i++) {
			Integer invID = (Integer) liteAcctInfo.getInventories().get(i);
			LiteLMHardwareBase liteHw = SOAPServer.getLMHardware( energyCompanyID, invID, true );
			Integer companyID = new Integer( liteHw.getInstallationCompanyID() );
			if (!companyList.contains( companyID ))
				companyList.add( companyID );
		}
	}
}
