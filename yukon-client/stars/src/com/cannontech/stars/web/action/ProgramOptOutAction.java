package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.*;
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
public class ProgramOptOutAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			com.cannontech.stars.web.StarsUser user = (com.cannontech.stars.web.StarsUser) session.getAttribute("USER");
			if (operator == null && user == null) return null;
            
            int period = 0;
            String periodStr = req.getParameter("OptOutPeriod");
            if (periodStr != null)
                try {
                    period = Integer.parseInt(periodStr);
                }
                catch (NumberFormatException e) {}
            if (period == 0) return null;
            
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, period);

            StarsProgramOptOut optOut = new StarsProgramOptOut();
            optOut.setReenableDateTime( now.getTime() );

            StarsOperation operation = new StarsOperation();
            operation.setStarsProgramOptOut( optOut );
            
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
            
            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            com.cannontech.stars.web.StarsUser user = (com.cannontech.stars.web.StarsUser) session.getAttribute("USER");
            if (operator == null && user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = null;
        	if (operator != null)
        		liteAcctInfo = (LiteStarsCustAccountInformation) operator.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	else
        		liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}

            com.cannontech.message.porter.ClientConnection conn = ServerUtils.getClientConnection();
            if (conn == null) {
                CTILogger.debug( "ProgramOptOutAction: Failed to get a connection to porter" );
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send Yukon switch command") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
			StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
			
            // Get list entry IDs
            int energyCompanyID = (operator != null) ? (int) operator.getEnergyCompanyID() : user.getEnergyCompanyID();
            Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            
            Integer hwEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT)
            		.getEntryID() );
            Integer progEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMPROGRAMEVENT)
            		.getEntryID() );
            Integer tempTermEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION)
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
            Date reenableDate = reqOper.getStarsProgramOptOut().getReenableDateTime();
            
            // List of hardware IDs to be disabled
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

                String cmd = "putconfig service out serial " + liteHw.getManufactureSerialNumber() + routeStr;
                ServerUtils.sendCommand(cmd, conn);
            
        		ServerUtils.removeFutureActivation( liteHw.getLmHardwareHistory(), futureActEntryID );
        		com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
        		
        		// Add "Temp Opt Out" and "Future Activation" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( tempTermEntryID );
        		eventBase.setEventDateTime( now );
        		
        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
        		multiDB.getDBPersistentVector().addElement( event );
				
        		event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		eventDB = event.getLMHardwareEvent();
        		eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( futureActEntryID );
        		eventBase.setEventDateTime( reenableDate );
        		
        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
        		multiDB.getDBPersistentVector().addElement( event );
        		
	            // Add "Temp Opt Out" and "Future Activation" to program events
        		for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
                	StarsAppliance appliance = (StarsAppliance) liteAcctInfo.getAppliances().get(j);
	                if (appliance.getInventoryID() != liteHw.getInventoryID()) continue;
	                
		            if (appliance.getLmProgramID() == 0) continue;
                	Integer programID = new Integer( appliance.getLmProgramID() );
                	
                	for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
	                	LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
	                	if (liteProg.getProgramID() == programID.intValue()) {
	                		ServerUtils.processFutureActivation( liteProg.getProgramHistory(), futureActEntryID, actCompEntryID );
	                		break;
	                	}
                	}
		            
		            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
		            		new com.cannontech.database.data.stars.event.LMProgramEvent();
		            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
		            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
		            
		            eventDB1.setLMProgramID( programID );
		            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
		            eventBase1.setEventTypeID( progEventEntryID );
		            eventBase1.setActionID( tempTermEntryID );
		            eventBase1.setEventDateTime( now );
		            
		            event1.setEnergyCompanyID( new Integer(energyCompanyID) );
		            multiDB.getDBPersistentVector().addElement( event1 );
		            
		            event1 = new com.cannontech.database.data.stars.event.LMProgramEvent();
		            eventDB1 = event1.getLMProgramEvent();
		            eventBase1 = event1.getLMCustomerEventBase();
		            
		            eventDB1.setLMProgramID( programID );
		            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
		            eventBase1.setEventTypeID( progEventEntryID );
		            eventBase1.setActionID( futureActEntryID );
		            eventBase1.setEventDateTime( reenableDate );
		            
		            event1.setEnergyCompanyID( new Integer(energyCompanyID) );
		            multiDB.getDBPersistentVector().addElement( event1 );
                }
        		
        		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
        				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
				
				// Update lite objects and create response
				// The first two events are hardware events
				int eventNo = 0;
				for (eventNo = 0; eventNo < 2; eventNo++) {
					event = (com.cannontech.database.data.stars.event.LMHardwareEvent) multiDB.getDBPersistentVector().get(eventNo);
					LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
					liteHw.getLmHardwareHistory().add( liteEvent );
				}
				
				StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
				hwHist.setInventoryID( liteHw.getInventoryID() );
				for (int k = 0; k < liteHw.getLmHardwareHistory().size(); k++) {
					LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(k);
					StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
					StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent, selectionLists );
					hwHist.addStarsLMHardwareEvent( starsEvent );
				}
				resp.addStarsLMHardwareHistory( hwHist );
				
				// The rest of the events are program events in pairs
				for (; eventNo < multiDB.getDBPersistentVector().size(); eventNo += 2) {
					com.cannontech.database.data.stars.event.LMProgramEvent event1 =
							(com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo);
					LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
					
					for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
						LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
						if (liteProg.getProgramID() != event1.getLMProgramEvent().getLMProgramID().intValue()) continue;
						
						liteProg.getProgramHistory().add( liteEvent );
						
						event1 = (com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo+1);
						liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
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

            respOper.setStarsProgramOptOutResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot opt out the programs") );
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

			StarsProgramOptOutResponse resp = operation.getStarsProgramOptOutResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			com.cannontech.stars.web.StarsUser user = (com.cannontech.stars.web.StarsUser) session.getAttribute("USER");
			StarsCustAccountInformation accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInformation) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			else
				accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
				
			StarsInventories inventories = accountInfo.getStarsInventories();
			StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
            
            // Update program history
            for (int i = 0; i < resp.getStarsLMProgramHistoryCount(); i++) {
            	StarsLMProgramHistory progHist = resp.getStarsLMProgramHistory(i);
            	
            	for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
            		StarsLMProgram program = programs.getStarsLMProgram(j);
            		if (program.getProgramID() == progHist.getProgramID()) {
            			program.setStarsLMProgramHistory( progHist );
            			program.setStatus( "Out of Service" );
            		}
            	}
            }
            
            if (operator != null)
            	operator.removeAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "LM_PROGRAM_HISTORY" );
            else
            	user.removeAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "LM_PROGRAM_HISTORY" );
            
            if (operator != null) {
				Integer energyCompanyID = new Integer( (int) operator.getEnergyCompanyID() );
				Hashtable selectionLists = (Hashtable) operator.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
					
				DeviceStatus hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
						StarsCustListEntryFactory.getStarsCustListEntry(
							(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS),
							com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_TEMPUNAVAIL),
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
