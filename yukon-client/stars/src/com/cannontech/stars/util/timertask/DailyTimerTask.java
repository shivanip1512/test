package com.cannontech.stars.util.timertask;

import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.stars.CustomerSelectionList;
import com.cannontech.database.db.stars.CustomerListEntry;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DailyTimerTask extends StarsTimerTask {
	
	private static final long TIMER_PERIOD = 1000 * 60 * 60 * 24;	// Run every day
	
	private static final long INITIAL_DELAY = 1000 * 60 * 5;	// Start initial running 5 mins after initialization
	
	/**
	 * @see com.cannontech.stars.util.timer.StarsTimer#getTimerPeriod()
	 */
	public long getTimerPeriod() {
		return TIMER_PERIOD;
	}

	/**
	 * @see com.cannontech.stars.util.timer.StarsTimer#getInitialDelay()
	 */
	public long getInitialDelay() {
		return INITIAL_DELAY;
	}

	/**
	 * @see com.cannontech.stars.util.timer.StarsTimer#getNextScheduledTime()
	 */
	public Date getNextScheduledTime() {
		return com.cannontech.util.ServletUtil.getTommorow();	// Run at every midnight
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.info( "*** Daily timer task start ***" );
		
		try {
			EnergyCompany[] companies = SOAPServer.getAllEnergyCompanies();
			if (companies == null) return;
			
			for (int i = 0; i < companies.length; i++) {
				Hashtable selectionLists = SOAPServer.getAllSelectionLists( companies[i].getEnergyCompanyID() );
				if (selectionLists == null) continue;
				
				int reenableActionID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
						CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION ).getEntryID();
				int completeActionID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
						CustomerListEntry.YUKONDEF_ACT_COMPLETED ).getEntryID();
				int programEventID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
						CustomerListEntry.YUKONDEF_LMPROGRAMEVENT ).getEntryID();
				int hardwareEventID = StarsCustListEntryFactory.getStarsCustListEntry(
						(LiteCustomerSelectionList) selectionLists.get(CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
						CustomerListEntry.YUKONDEF_LMHARDWAREEVENT ).getEntryID();
				
				com.cannontech.database.db.stars.event.LMCustomerEventBase[] hwEvents =
						com.cannontech.database.db.stars.event.LMCustomerEventBase.getAllCustomerEvents( hardwareEventID, reenableActionID );
				com.cannontech.database.db.stars.event.LMCustomerEventBase[] progEvents =
						com.cannontech.database.db.stars.event.LMCustomerEventBase.getAllCustomerEvents( programEventID, reenableActionID );
						
				for (int j = 0; j < hwEvents.length; j++) {
					if (hwEvents[j].getEventDateTime().before( new Date() )) {
						// Send yukon switch command to enable the LM hardware
						com.cannontech.database.db.stars.event.LMHardwareEvent hwEvent = new com.cannontech.database.db.stars.event.LMHardwareEvent();
						hwEvent.setEventID( hwEvents[i].getEventID() );
						hwEvent = (com.cannontech.database.db.stars.event.LMHardwareEvent)
								Transaction.createTransaction( Transaction.RETRIEVE, hwEvent ).execute();
								
						com.cannontech.database.db.stars.hardware.LMHardwareBase hw = new com.cannontech.database.db.stars.hardware.LMHardwareBase();
						hw.setInventoryID( hwEvent.getInventoryID() );
						hw = (com.cannontech.database.db.stars.hardware.LMHardwareBase)
								Transaction.createTransaction( Transaction.RETRIEVE, hw ).execute();
								
						String cmd = "putconfig service in serial " + hw.getManufacturerSerialNumber();
						ServerUtils.sendCommand( cmd, ServerUtils.getClientConnection() );
						
						CTILogger.debug( "*** Send service in command to serial " + hw.getManufacturerSerialNumber() );
						
						// Remove "Future Activation", and add "Activation Completed" to hardware events
						com.cannontech.database.data.stars.event.LMHardwareEvent event1 = new com.cannontech.database.data.stars.event.LMHardwareEvent();
						event1.setEventID( hwEvents[j].getEventID() );
						Transaction.createTransaction( Transaction.DELETE, event1 ).execute();
						
						hwEvents[j].setActionID( new Integer(completeActionID) );
						hwEvents[j].setEventDateTime( new Date() );
						
						com.cannontech.database.data.stars.event.LMHardwareEvent event2 = new com.cannontech.database.data.stars.event.LMHardwareEvent();
						event2.setLMCustomerEventBase( hwEvents[j] );
						event2.setLmHardwareEvent( hwEvent );
						event2.setEventID( null );
						event2.setEnergyCompanyID( companies[i].getEnergyCompanyID() );
						event2 = (com.cannontech.database.data.stars.event.LMHardwareEvent)
								Transaction.createTransaction( Transaction.INSERT, event2 ).execute();
						
						// Update the lite object
						LiteLMHardware liteHw = SOAPServer.getLMHardware( companies[i].getEnergyCompanyID(), hw.getInventoryID(), false );
						if (liteHw != null) {
							ArrayList hwHist = liteHw.getLmHardwareHistory();
							if (hwHist != null) {
								hwHist.remove( StarsLiteFactory.createLite(event1) );
								hwHist.add( StarsLiteFactory.createLite(event2) );
							}
						}
					}
				}
						
				for (int j = 0; j < progEvents.length; j++) {
					Date now = new Date();
					if (progEvents[j].getEventDateTime().before( now )) {
						progEvents[j].setActionID( new Integer(completeActionID) );
						progEvents[j].setEventDateTime( now );
						progEvents[j] = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
								Transaction.createTransaction( Transaction.UPDATE, progEvents[j] ).execute();
						
						// Update the lite object
						com.cannontech.database.db.stars.event.LMProgramEvent progEvent = new com.cannontech.database.db.stars.event.LMProgramEvent();
						progEvent.setEventID( progEvents[j].getEventID() );
						progEvent = (com.cannontech.database.db.stars.event.LMProgramEvent)
								Transaction.createTransaction( Transaction.RETRIEVE, progEvent ).execute();
								
						ArrayList liteAcctInfoList = SOAPServer.getAllCustAccountInformation( companies[i].getEnergyCompanyID() );
						for (int k = 0; k < liteAcctInfoList.size(); k++) {
							LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) liteAcctInfoList.get(k);
							if (liteAcctInfo.getCustomerAccount().getAccountID() != progEvent.getAccountID().intValue()) continue;
							
							ArrayList programs = liteAcctInfo.getLmPrograms();
							if (programs == null) break;
							
							for (int l = 0; l < programs.size(); l++) {
								LiteStarsLMProgram liteProg = (LiteStarsLMProgram) programs.get(l);
								if (liteProg.getLmProgramID() != progEvent.getLMProgramID().intValue()) continue;
								
								ArrayList progHist = liteProg.getProgramHistory();
								if (progHist == null) break;
								
								for (int m = progHist.size() - 1; m >= 0; m--) {
									LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) progHist.get(m);
									if (liteEvent.getEventID() == progEvent.getEventID().intValue()) {
										StarsLiteFactory.setLiteLMCustomerEvent( liteEvent, progEvents[j] );
										break;
									}
								}
								break;
							}
							break;
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		CTILogger.info( "*** Daily timer task stop ***" );
	}

}
