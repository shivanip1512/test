package com.cannontech.stars.web.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsAppliances;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDisableService;
import com.cannontech.stars.xml.serialize.StarsEnableService;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsYukonSwitchCommand;
import com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.stars.xml.util.XMLUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class YukonSwitchCommandAction implements ActionBase {

    public YukonSwitchCommandAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			if (operator == null && user == null) return null;
			
			StarsCustAccountInformation accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInformation) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
            if (accountInfo == null) return null;

            String action = req.getParameter("action");
            StarsYukonSwitchCommand command = new StarsYukonSwitchCommand();

            if (action.equalsIgnoreCase("DisableService")) {
                String periodStr = req.getParameter("OptOutPeriod");
                int period = 0;
                if (periodStr != null)
                    try {
                        period = Integer.parseInt(periodStr);
                    }
                    catch (NumberFormatException e) {}

                if (period > 0) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.DATE, period);

                    StarsDisableService service = new StarsDisableService();
                    service.setReEnableDateTime( now.getTime() );

                    StarsInventories inventories = accountInfo.getStarsInventories();
                    for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
                        StarsLMHardware hardware = inventories.getStarsLMHardware(i);
                        service.addSerialNumber( hardware.getManufactureSerialNumber() );
                    }

                    command.setStarsDisableService( service );
                }
            }
            else if (action.equalsIgnoreCase("EnableService")) {
                Calendar now = Calendar.getInstance();

                StarsEnableService service = new StarsEnableService();
                service.setEnableDateTime( now.getTime() );

                StarsInventories inventories = accountInfo.getStarsInventories();
                for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
                    StarsLMHardware hardware = inventories.getStarsLMHardware(i);
                    service.addSerialNumber( hardware.getManufactureSerialNumber() );
                }

                command.setStarsEnableService( service );
            }

            StarsOperation operation = new StarsOperation();
            operation.setStarsYukonSwitchCommand( command );
            
            return SOAPUtil.buildSOAPMessage( operation );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();

			StarsYukonSwitchCommandResponse resp = operation.getStarsYukonSwitchCommandResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			StarsCustAccountInformation accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInformation) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
				
			StarsInventories inventories = accountInfo.getStarsInventories();
			StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
            	
            // Update hardware history
            for (int i = 0; i < resp.getStarsLMHardwareHistoryCount(); i++) {
	            StarsLMHardwareHistory hwHist = resp.getStarsLMHardwareHistory(i);
	            
				for (int j = 0; j < inventories.getStarsLMHardwareCount(); j++) {
					StarsLMHardware hw = inventories.getStarsLMHardware(j);
					if (hw.getInventoryID() == hwHist.getInventoryID())
						hw.setStarsLMHardwareHistory( hwHist );
				}
            }
            
            // Update program history
            for (int i = 0; i < resp.getStarsLMProgramHistoryCount(); i++) {
            	StarsLMProgramHistory progHist = resp.getStarsLMProgramHistory(i);
            	
            	for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
            		StarsLMProgram program = programs.getStarsLMProgram(j);
            		if (program.getProgramID() == progHist.getProgramID())
            			program.setStarsLMProgramHistory( progHist );
            	}
            }
            
            if (operator != null)
	            operator.removeAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "PROGRAM_HISTORY" );
	        else
	        	user.removeAttribute( ServletUtils.TRANSIENT_ATT_LEADING + "PROGRAM_HISTORY" );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            StarsUser user = (StarsUser) session.getAttribute("USER");
            if (operator == null && user == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
                
			Integer energyCompanyID = null;
    		if (operator != null)
        		energyCompanyID = new Integer((int) operator.getEnergyCompanyID());
        	else
        		energyCompanyID = new Integer(user.getEnergyCompanyID());
            
        	LiteStarsCustAccountInformation liteAcctInfo = null;
        	if (operator != null)
        		liteAcctInfo = (LiteStarsCustAccountInformation) operator.getAttribute( "CUSTOMER_ACCOUNT_INFORMATION" );
        	else
        		liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( "CUSTOMER_ACCOUNT_INFORMATION" );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}

            ClientConnection conn = ServerUtils.getClientConnection();
            if (conn == null) {
                CTILogger.debug( "YukonSwitchCommandAction: Failed to retrieve a connection" );
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send Yukon switch command") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StarsYukonSwitchCommand command = reqOper.getStarsYukonSwitchCommand();
            StarsYukonSwitchCommandResponse cmdResp = new StarsYukonSwitchCommandResponse();
            
            // Get list entry IDs
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

            // Current date, all LM customer events will use exactly the same date
            Date now = new Date();
            
            if (command.getStarsDisableService() != null) {
                StarsDisableService service = command.getStarsDisableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service out serial " + service.getSerialNumber(i);
                    ServerUtils.sendCommand(cmd, conn);
                
                	ArrayList inventories = liteAcctInfo.getInventories();
                	for (int j = 0; j < inventories.size(); j++) {
                		Integer invID = (Integer) inventories.get(j);
                		LiteLMHardware liteHw = SOAPServer.getLMHardware( energyCompanyID, invID, true );
	                			
	                	if (liteHw.getManufactureSerialNumber().equalsIgnoreCase( service.getSerialNumber(i) )) {
	                		// Check to see if the LM hardware is already out of service, if it is, then skip the table updates
	                		ArrayList events = liteHw.getLmHardwareHistory();
	                		//if (!isInService(events)) break;
	                		
	                		com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
	                				
	                		// Add "Temp Opt Out" and "Future Activation" events to the LM hardware history
	                		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
	                		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
	                		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
	                		
	                		eventDB.setInventoryID( invID );
	                		eventBase.setEventTypeID( hwEventEntryID );
	                		eventBase.setActionID( tempTermEntryID );
	                		eventBase.setEventDateTime( now );
	                		
	                		event.setEnergyCompanyID( energyCompanyID );
	                		multiDB.getDBPersistentVector().addElement( event );
							
	                		event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
	                		eventDB = event.getLMHardwareEvent();
	                		eventBase = event.getLMCustomerEventBase();
	                		
	                		eventDB.setInventoryID( invID );
	                		eventBase.setEventTypeID( hwEventEntryID );
	                		eventBase.setActionID( futureActEntryID );
	                		eventBase.setEventDateTime( service.getReEnableDateTime() );
	                		
	                		event.setEnergyCompanyID( energyCompanyID );
	                		multiDB.getDBPersistentVector().addElement( event );
	                		
				            // Add "Temp Opt Out" and "Future Activation" events to the history of all affected LM programs
	                		ArrayList appliances = liteAcctInfo.getAppliances();
	                		for (int k = 0; k < appliances.size(); k++) {
			                	StarsAppliance appliance = (StarsAppliance) appliances.get(k);
				                
				                if (appliance.getInventoryID() == liteHw.getInventoryID()) {
						            if (appliance.getLmProgramID() == 0) continue;
				                	Integer programID = new Integer( appliance.getLmProgramID() );
						            
						            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
						            		new com.cannontech.database.data.stars.event.LMProgramEvent();
						            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
						            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
						            
						            eventDB1.setLMProgramID( programID );
						            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
						            eventBase1.setEventTypeID( progEventEntryID );
						            eventBase1.setActionID( tempTermEntryID );
						            eventBase1.setEventDateTime( now );
						            
						            event1.setEnergyCompanyID( energyCompanyID );
						            multiDB.getDBPersistentVector().addElement( event1 );
						            
						            event1 = new com.cannontech.database.data.stars.event.LMProgramEvent();
						            eventDB1 = event1.getLMProgramEvent();
						            eventBase1 = event1.getLMCustomerEventBase();
						            
						            eventDB1.setLMProgramID( programID );
						            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
						            eventBase1.setEventTypeID( progEventEntryID );
						            eventBase1.setActionID( futureActEntryID );
						            eventBase1.setEventDateTime( service.getReEnableDateTime() );
						            
						            event1.setEnergyCompanyID( energyCompanyID );
						            multiDB.getDBPersistentVector().addElement( event1 );
				                }
			                }
	                		
	                		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
	                				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
							
							// Update lite objects and create response
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
								StarsLMHardwareEvent starsEvent = (StarsLMHardwareEvent) StarsLiteFactory.createStarsLMCustomerEvent( liteEvent, StarsLMHardwareEvent.class, selectionLists );
								hwHist.addStarsLMHardwareEvent( starsEvent );
							}
							cmdResp.addStarsLMHardwareHistory( hwHist );
							
							for (; eventNo < multiDB.getDBPersistentVector().size(); eventNo += 2) {
								com.cannontech.database.data.stars.event.LMProgramEvent event1 =
										(com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo);
								LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
								
								ArrayList programs = liteAcctInfo.getLmPrograms();
								for (int k = 0; k < programs.size(); k++) {
									LiteStarsLMProgram liteProg = (LiteStarsLMProgram) programs.get(k);
									
									if (liteProg.getLmProgramID() == event1.getLMProgramEvent().getLMProgramID().intValue()) {
										liteProg.getProgramHistory().add( liteEvent );
										
										event1 = (com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo+1);
										liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
										liteProg.getProgramHistory().add( liteEvent );
										
										StarsLMProgramHistory progHist = new StarsLMProgramHistory();
										progHist.setProgramID( liteProg.getLmProgramID() );
										for (int l = 0; l < liteProg.getProgramHistory().size(); l++) {
											liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(k);
											StarsLMProgramEvent starsEvent = (StarsLMProgramEvent) StarsLiteFactory.createStarsLMCustomerEvent( liteEvent, StarsLMProgramEvent.class, selectionLists );
											progHist.addStarsLMProgramEvent( starsEvent );
										}
										cmdResp.addStarsLMProgramHistory( progHist );
										
										break;
									}
								}
							}
							
	                		break;
	                	}
	                }
                }
            }
            else if (command.getStarsEnableService() != null) {
                StarsEnableService service = command.getStarsEnableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service in serial " + service.getSerialNumber(i);
                    ServerUtils.sendCommand(cmd, conn);
                
                	ArrayList inventories = liteAcctInfo.getInventories();
                	for (int j = 0; j < inventories.size(); j++) {
                		Integer invID = (Integer) inventories.get(j);
                		LiteLMHardware liteHw = SOAPServer.getLMHardware( energyCompanyID, invID, true );
	                			
	                	if (liteHw.getManufactureSerialNumber().equalsIgnoreCase( service.getSerialNumber(i) )) {
	                		// Check to see if the LM hardware is still in service, if it is, then skip the table updates
	                		ArrayList events = liteHw.getLmHardwareHistory();
	                		//if (isInService(events)) break;
	                		
	                		// Check whether to update the last entry, or add a new entry into the database
	                		boolean update = false;
	                		LiteLMCustomerEvent lastHwEvent = null;
	                		if (events != null && events.size() > 0) {
	                			lastHwEvent = (LiteLMCustomerEvent) events.get( events.size() - 1 );
			                	if (lastHwEvent.getActionID() == futureActEntryID.intValue())
			                		update = true;
	                		}
		                	
		                	// Update or add a new "Activation Completed" LM hardware event, update the lite objects
	                		if (update) {
	                			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase =
	                					(com.cannontech.database.db.stars.event.LMCustomerEventBase) StarsLiteFactory.createDBPersistent( lastHwEvent );
	                			eventBase = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
	                					Transaction.createTransaction( Transaction.RETRIEVE, eventBase ).execute();
	                					
	                			eventBase.setActionID( actCompEntryID );
		                		eventBase.setEventDateTime( now );
		                		eventBase = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
		                				Transaction.createTransaction( Transaction.UPDATE, eventBase ).execute();
		                		
		                		StarsLiteFactory.setLiteLMCustomerEvent( lastHwEvent, eventBase );
	                		}
	                		else {	
		                		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
		                		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
		                		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
		                		
		                		eventDB.setInventoryID( invID );
		                		eventBase.setEventTypeID( hwEventEntryID );
		                		eventBase.setActionID( actCompEntryID );
		                		eventBase.setEventDateTime( now );
	                			event.setEnergyCompanyID( energyCompanyID );
	                			
		                		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
		                				Transaction.createTransaction( Transaction.INSERT, event ).execute();
		                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
		                		events.add( liteEvent );
	                		}
							
							// Create response
							StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
							hwHist.setInventoryID( liteHw.getInventoryID() );
							for (int k = 0; k < liteHw.getLmHardwareHistory().size(); k++) {
								LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(k);
								StarsLMHardwareEvent starsEvent = (StarsLMHardwareEvent) StarsLiteFactory.createStarsLMCustomerEvent( liteEvent, StarsLMHardwareEvent.class, selectionLists );
								hwHist.addStarsLMHardwareEvent( starsEvent );
							}
							cmdResp.addStarsLMHardwareHistory( hwHist );
	                		
		                	// Update or add a new "Activation Completed" event for all the affected LM programs
	                		ArrayList appliances = liteAcctInfo.getAppliances();
	                		for (int k = 0; k < appliances.size(); k++) {
			                	StarsAppliance appliance = (StarsAppliance) appliances.get(k);
				                
				                if (appliance.getInventoryID() == liteHw.getInventoryID()) {
						            if (appliance.getLmProgramID() == 0) continue;
				                	Integer programID = new Integer( appliance.getLmProgramID() );
						            
						            LiteStarsLMProgram liteProgram = null;
									ArrayList programs = liteAcctInfo.getLmPrograms();
									for (int l = 0; l < programs.size(); l++) {
										liteProgram = (LiteStarsLMProgram) programs.get(l);
										if (liteProgram.getLmProgramID() == appliance.getLmProgramID())
											break;
									}
									
									if (liteProgram == null) continue;	// Shouldn't happen
									
									ArrayList progHist = liteProgram.getProgramHistory();
										
						            if (update && progHist.size() > 0) {
						            	LiteLMCustomerEvent lastProgEvent = (LiteLMCustomerEvent) progHist.get( progHist.size() - 1);
			                			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase =
			                					(com.cannontech.database.db.stars.event.LMCustomerEventBase) StarsLiteFactory.createDBPersistent( lastProgEvent );
			                			eventBase = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
			                					Transaction.createTransaction( Transaction.RETRIEVE, eventBase ).execute();
			                					
							            eventBase.setActionID( actCompEntryID );
							            eventBase.setEventDateTime( now );
					                	eventBase = (com.cannontech.database.db.stars.event.LMCustomerEventBase)
					                			Transaction.createTransaction( Transaction.UPDATE, eventBase ).execute();
					                			
					                	StarsLiteFactory.setLiteLMCustomerEvent( lastProgEvent, eventBase );
						            }
						            else {
							            com.cannontech.database.data.stars.event.LMProgramEvent event =
							            		new com.cannontech.database.data.stars.event.LMProgramEvent();
							            com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
							            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
							            
							            eventDB.setLMProgramID( new Integer(appliance.getLmProgramID()) );
							            eventDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
							            eventBase.setEventTypeID( progEventEntryID );
							            eventBase.setActionID( actCompEntryID );
							            eventBase.setEventDateTime( now );
							            event.setEnergyCompanyID( energyCompanyID );
							            
				                		event = (com.cannontech.database.data.stars.event.LMProgramEvent)
				                				Transaction.createTransaction( Transaction.INSERT, event ).execute();
				                		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				                		progHist.add( liteEvent );
				                	}
										
									StarsLMProgramHistory starsProgHist = new StarsLMProgramHistory();
									starsProgHist.setProgramID( liteProgram.getLmProgramID() );
									for (int l = 0; l < liteProgram.getProgramHistory().size(); l++) {
										LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteProgram.getProgramHistory().get(k);
										StarsLMProgramEvent starsEvent = (StarsLMProgramEvent) StarsLiteFactory.createStarsLMCustomerEvent( liteEvent, StarsLMProgramEvent.class, selectionLists );
										starsProgHist.addStarsLMProgramEvent( starsEvent );
									}
									cmdResp.addStarsLMProgramHistory( starsProgHist );
				                }
			                }
							
							break;
	                	}
	                }
                }
            }

            respOper.setStarsYukonSwitchCommandResponse( cmdResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    boolean isInService(Integer energyCompanyID, ArrayList events) {
    	if (events == null) return false;
    	
    	Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
        int tempTermEntryID = StarsCustListEntryFactory.getStarsCustListEntry(
        		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
        		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION ).getEntryID();
        int futureActEntryID = StarsCustListEntryFactory.getStarsCustListEntry(
        		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
        		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION ).getEntryID();
        int actCompEntryID = StarsCustListEntryFactory.getStarsCustListEntry(
        		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
        		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED ).getEntryID();
    	
    	for (int i = events.size() - 1; i >= 0; i--) {
    		LiteLMCustomerEvent event = (LiteLMCustomerEvent) events.get(i);
    		if (event.getActionID() == actCompEntryID)
    			return true;
    		else if (event.getActionID() == futureActEntryID || event.getActionID() == tempTermEntryID)
    			return false;
    	}
    	return false;
    }
}