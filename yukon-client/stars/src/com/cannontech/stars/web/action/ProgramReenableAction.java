package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.util.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProgramReenableAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			if (user == null) return null;

            StarsProgramReenable reEnable = new StarsProgramReenable();

            StarsOperation operation = new StarsOperation();
            operation.setStarsProgramReenable( reEnable );
            
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
			StarsProgramReenableResponse resp = new StarsProgramReenableResponse();
			
            // Get list entry IDs
            int energyCompanyID = user.getEnergyCompanyID();
            Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            
            Integer hwEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT)
            		.getEntryID() );
            Integer progEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMPROGRAMEVENT)
            		.getEntryID() );
            Integer futureActEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION)
            		.getEntryID() );
            Integer actCompEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED)
            		.getEntryID() );

            Date now = new Date();	// Current date, all customer events will use exactly the same date
            
            // List of hardware IDs to be reenabled
            ArrayList hwIDList = new ArrayList();
            for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
            	LiteStarsLMProgram program = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
            	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            		StarsAppliance appliance = (StarsAppliance) liteAcctInfo.getAppliances().get(j);
            		if (appliance.getLmProgramID() == program.getProgramID()) {
            			Integer hardwareID = new Integer( appliance.getInventoryID() );
            			if (!hwIDList.contains( hardwareID )) hwIDList.add( hardwareID );
            			break;
            		}
            	}
            }
			
			LiteStarsEnergyCompany company = SOAPServer.getEnergyCompany( energyCompanyID );
			String routeStr = (company == null) ? "" : " select route id " + String.valueOf(company.getRouteID());

            for (int i = 0; i < hwIDList.size(); i++) {
            	Integer invID = (Integer) hwIDList.get(i);
            	LiteLMHardwareBase liteHw = SOAPServer.getLMHardware( energyCompanyID, invID.intValue(), true );

                String cmd = "putconfig service in serial " + liteHw.getManufactureSerialNumber() + routeStr;
                ServerUtils.sendCommand( cmd );
    			
    			ServerUtils.removeFutureActivation( liteHw.getLmHardwareHistory(), futureActEntryID );
        		com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
        		
        		// Add "Activation Completed" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( actCompEntryID );
        		eventBase.setEventDateTime( now );
        		
        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
        		multiDB.getDBPersistentVector().addElement( event );
        		
	            // Add "Activation Completed" to program events
        		for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
                	StarsAppliance appliance = (StarsAppliance) liteAcctInfo.getAppliances().get(j);
	                if (appliance.getInventoryID() != liteHw.getInventoryID()) continue;
	                
		            if (appliance.getLmProgramID() == 0) continue;
                	Integer programID = new Integer( appliance.getLmProgramID() );
                	
                	LiteStarsLMProgram liteProg = null;
                	for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
	                	LiteStarsLMProgram lProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
	                	if (lProg.getProgramID() == programID.intValue()) {
	                		liteProg = lProg;
	                		break;
	                	}
                	}
                	
                	// If program is already in service, do nothing
            		if (liteProg == null || ServerUtils.isInService( liteProg.getProgramHistory(), futureActEntryID, actCompEntryID ))
            			continue;
            			
            		ServerUtils.removeFutureActivation( liteProg.getProgramHistory(), futureActEntryID );
		            
		            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
		            		new com.cannontech.database.data.stars.event.LMProgramEvent();
		            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
		            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
		            
		            eventDB1.setLMProgramID( programID );
		            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
		            eventBase1.setEventTypeID( progEventEntryID );
		            eventBase1.setActionID( actCompEntryID );
		            eventBase1.setEventDateTime( now );
		            
		            event1.setEnergyCompanyID( new Integer(energyCompanyID) );
		            multiDB.getDBPersistentVector().addElement( event1 );
                }
        		
        		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
        				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
        				
				// Update lite objects and create response
				// The first event is a hardware event
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent) multiDB.getDBPersistentVector().get(0);
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getLmHardwareHistory().add( liteEvent );
				
				StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
				hwHist.setInventoryID( liteHw.getInventoryID() );
				for (int k = 0; k < liteHw.getLmHardwareHistory().size(); k++) {
					liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(k);
					StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
					StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent, selectionLists );
					hwHist.addStarsLMHardwareEvent( starsEvent );
				}
				resp.addStarsLMHardwareHistory( hwHist );
				
				// The rest of the events are program events
				for (int eventNo = 1; eventNo < multiDB.getDBPersistentVector().size(); eventNo++) {
					com.cannontech.database.data.stars.event.LMProgramEvent event1 =
							(com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo);
					liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
					
					for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
						LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
						if (liteProg.getProgramID() != event1.getLMProgramEvent().getLMProgramID().intValue()) continue;
						liteProg.getProgramHistory().add( liteEvent );
						
						StarsLMProgramHistory progHist = new StarsLMProgramHistory();
						progHist.setProgramID( liteProg.getProgramID() );
						for (int l = 0; l < liteProg.getProgramHistory().size(); l++) {
							liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(l);
							StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
							StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent, selectionLists );
							progHist.addStarsLMProgramEvent( starsEvent );
						}
						resp.addStarsLMProgramHistory( progHist );
					}
				}
            }

            respOper.setStarsProgramReenableResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot reenable the programs") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
            }
        }

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}

			StarsProgramReenableResponse resp = operation.getStarsProgramReenableResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				
			StarsInventories inventories = accountInfo.getStarsInventories();
			StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
            
            // Update program history
            for (int i = 0; i < resp.getStarsLMProgramHistoryCount(); i++) {
            	StarsLMProgramHistory progHist = resp.getStarsLMProgramHistory(i);
            	
            	for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
            		StarsLMProgram program = programs.getStarsLMProgram(j);
            		if (program.getProgramID() == progHist.getProgramID()) {
            			program.setStarsLMProgramHistory( progHist );
            			program.setStatus( "In Service" );
            		}
            	}
            }
            
        	user.removeAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "LM_PROGRAM_HISTORY" );
            
            if (ServerUtils.isOperator( user )) {
				Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
					
				DeviceStatus hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
						StarsCustListEntryFactory.getStarsCustListEntry(
							(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS),
							com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_AVAIL),
						DeviceStatus.class );
            	
	            // Update hardware history
	            for (int i = 0; i < resp.getStarsLMHardwareHistoryCount(); i++) {
		            StarsLMHardwareHistory hwHist = resp.getStarsLMHardwareHistory(i);
		            
					for (int j = 0; j < inventories.getStarsLMHardwareCount(); j++) {
						StarsLMHardware hw = inventories.getStarsLMHardware(j);
						if (hw.getInventoryID() == hwHist.getInventoryID()) {
							hw.setStarsLMHardwareHistory( hwHist );
							hw.setDeviceStatus( hwStatus );
						}
					}
	            }
            }
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
