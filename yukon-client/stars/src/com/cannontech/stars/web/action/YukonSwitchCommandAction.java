package com.cannontech.stars.web.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.StarsUser;
import com.cannontech.stars.web.util.CommonUtils;
import com.cannontech.stars.xml.serialize.StarsCustAccountInfo;
import com.cannontech.stars.xml.serialize.StarsDisableService;
import com.cannontech.stars.xml.serialize.StarsEnableService;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSwitchCommand;
import com.cannontech.stars.xml.serialize.StarsSwitchCommandResponse;
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

    private org.apache.commons.logging.Log logger = XMLUtil.getLogger( YukonSwitchCommandAction.class );

    // increment this for every message
    private static long userMessageIDCounter = 1;

    public YukonSwitchCommandAction() {
        super();
    }

    public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			if (operator == null && user == null) return null;
			
			StarsCustAccountInfo accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) user.getAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
            if (accountInfo == null) return null;

            String action = req.getParameter("action");
            StarsSwitchCommand command = new StarsSwitchCommand();

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
            operation.setStarsSwitchCommand( command );
            
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

			StarsSwitchCommandResponse resp = operation.getStarsSwitchCommandResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsUser user = (StarsUser) session.getAttribute("USER");
			StarsCustAccountInfo accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) user.getAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
				
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
	            operator.removeAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "PROGRAM_HISTORY" );
	        else
	        	user.removeAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "PROGRAM_HISTORY" );
			
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
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            com.cannontech.database.data.stars.customer.CustomerAccount account = null;
            if (operator != null)
            	account = (com.cannontech.database.data.stars.customer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            else
            	account = (com.cannontech.database.data.stars.customer.CustomerAccount) user.getAttribute("CUSTOMER_ACCOUNT");
                
			com.cannontech.database.data.company.EnergyCompanyBase energyCompany = new com.cannontech.database.data.company.EnergyCompanyBase();
    		if (operator != null)
        		energyCompany.setEnergyCompanyID( new Integer((int) operator.getEnergyCompanyID()) );
        	else
        		energyCompany.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );

            PILConnectionServlet connContainer = (PILConnectionServlet)
                    session.getAttribute( PILConnectionServlet.SERVLET_CONTEXT_ID );
            if (connContainer == null) {
                logger.error("YukonSwitchCommandAction: Failed to retrieve PILConnectionServlet from servlet context");

                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Failed to send Yukon switch command");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

            ClientConnection conn = connContainer.getConnection();
            if (conn == null) {
                logger.error( "YukonSwitchCommandAction: Failed to retrieve a connection" );

                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
                failure.setDescription("Failed to send Yukon switch command");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            StarsSwitchCommand command = reqOper.getStarsSwitchCommand();
            StarsSwitchCommandResponse cmdResp = new StarsSwitchCommandResponse();
            
            // Get list entry IDs
            Hashtable selectionLists = com.cannontech.stars.util.CommonUtils.getSelectionListTable(
            		energyCompany.getEnergyCompany().getEnergyCompanyID() );
            
            Integer hwEventEntryID = null;
            Integer progEventEntryID = null;
            Integer tempTermEntryID = null;
            Integer futureActEntryID = null;
            Integer actCompEntryID = null;
            
            StarsCustSelectionList custEventList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT );
            for (int i = 0; i < custEventList.getStarsSelectionListEntryCount(); i++) {
            	StarsSelectionListEntry entry = custEventList.getStarsSelectionListEntry(i);
            	if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT ))
            		hwEventEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMPROGRAMEVENT ))
            		progEventEntryID = new Integer( entry.getEntryID() );
            }
            
            StarsCustSelectionList actionList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION );
            for (int i = 0; i < actionList.getStarsSelectionListEntryCount(); i++) {
            	StarsSelectionListEntry entry = actionList.getStarsSelectionListEntry(i);
            	if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION ))
            		tempTermEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION ))
            		futureActEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED ))
            		actCompEntryID = new Integer( entry.getEntryID() );
            }
            
            Vector cmdHwVct = new Vector();	// Vector of all the hardwares controlled by the switch command
            Vector cmdProgVct = new Vector();	// Vector of all the programs controlled by the switch command
            Date now = new Date();
            
            if (command.getStarsDisableService() != null) {
                StarsDisableService service = command.getStarsDisableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service out serial " + service.getSerialNumber(i);
                    sendCommand(cmd, conn);
                
	                Vector invVct = account.getInventoryVector();
	                for (int j = 0; j < invVct.size(); j++) {
	                	com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
	                			(com.cannontech.database.data.stars.hardware.LMHardwareBase) invVct.elementAt(j);
	                	com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
	                			
	                	if (hwDB.getManufacturerSerialNumber().equalsIgnoreCase( service.getSerialNumber(i) )) {
	                		// Check to see if the LM hardware is already out of service, if it is, then skip the table updates
	                		com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
	                				com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( hwDB.getInventoryID() );
	                		if (!isInService(events)) break;
	                		
	                		cmdHwVct.addElement( hw );
	                		com.cannontech.database.data.multi.MultiDBPersistent multiDB =
	                				new com.cannontech.database.data.multi.MultiDBPersistent();
	                				
	                		// Insert "Temp Opt Out" and "Future Activation" events in the LMHardwareEvent table
	                		com.cannontech.database.data.stars.event.LMHardwareEvent event =
	                				new com.cannontech.database.data.stars.event.LMHardwareEvent();
	                		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
	                		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
	                		
	                		eventDB.setInventoryID( hwDB.getInventoryID() );
	                		eventBase.setEventTypeID( hwEventEntryID );
	                		eventBase.setActionID( tempTermEntryID );
	                		eventBase.setEventDateTime( now );
	                		
	                		event.setEnergyCompanyBase( energyCompany );
	                		multiDB.getDBPersistentVector().addElement( event );
							
	                		event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
	                		eventDB = event.getLMHardwareEvent();
	                		eventBase = event.getLMCustomerEventBase();
	                		
	                		eventDB.setInventoryID( hwDB.getInventoryID() );
	                		eventBase.setEventTypeID( hwEventEntryID );
	                		eventBase.setActionID( futureActEntryID );
	                		eventBase.setEventDateTime( service.getReEnableDateTime() );
	                		
	                		event.setEnergyCompanyBase( energyCompany );
	                		multiDB.getDBPersistentVector().addElement( event );
	                		
			                Vector appVct = account.getApplianceVector();
			                for (int k = 0; k < appVct.size(); k++) {
			                	com.cannontech.database.data.stars.appliance.ApplianceBase app =
			                			(com.cannontech.database.data.stars.appliance.ApplianceBase) appVct.elementAt(k);
				                com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config = app.getLMHardwareConfig();
				                
				                if (config.getInventoryID().intValue() == hwDB.getInventoryID().intValue()) {
				                	com.cannontech.database.data.device.lm.LMProgramBase program = app.getLMProgram();
						            if (program.getPAObjectID().intValue() == 0) continue;
						            cmdProgVct.addElement( program );
						            
						            // Insert "Temp Opt Out" and "Future Activation" events in the LMProgramEvent table
						            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
						            		new com.cannontech.database.data.stars.event.LMProgramEvent();
						            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
						            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
						            
						            eventDB1.setLMProgramID( program.getPAObjectID() );
						            eventDB1.setAccountID( account.getCustomerAccount().getAccountID() );
						            eventBase1.setEventTypeID( progEventEntryID );
						            eventBase1.setActionID( tempTermEntryID );
						            eventBase1.setEventDateTime( now );
						            
						            event1.setEnergyCompanyBase( energyCompany );
						            multiDB.getDBPersistentVector().addElement( event1 );
						            
						            event1 = new com.cannontech.database.data.stars.event.LMProgramEvent();
						            eventDB1 = event1.getLMProgramEvent();
						            eventBase1 = event1.getLMCustomerEventBase();
						            
						            eventDB1.setLMProgramID( program.getPAObjectID() );
						            eventDB1.setAccountID( account.getCustomerAccount().getAccountID() );
						            eventBase1.setEventTypeID( progEventEntryID );
						            eventBase1.setActionID( futureActEntryID );
						            eventBase1.setEventDateTime( service.getReEnableDateTime() );
						            
						            event1.setEnergyCompanyBase( energyCompany );
						            multiDB.getDBPersistentVector().addElement( event1 );
				                }
			                }
	                		
	                		Transaction transaction = Transaction.createTransaction( Transaction.INSERT, multiDB );
	                		transaction.execute();
							
	                		break;
	                	}
	                }
                }
            }
            else if (command.getStarsEnableService() != null) {
                StarsEnableService service = command.getStarsEnableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service in serial " + service.getSerialNumber(i);
                    sendCommand(cmd, conn);
                
	                Vector invVct = account.getInventoryVector();
	                for (int j = 0; j < invVct.size(); j++) {
	                	com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
	                			(com.cannontech.database.data.stars.hardware.LMHardwareBase) invVct.elementAt(j);
	                	com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
	                			
	                	if (hwDB.getManufacturerSerialNumber().equalsIgnoreCase( service.getSerialNumber(i) )) {
	                		// Check to see if the LM hardware is still in service, if it is, then skip the table updates
	                		com.cannontech.database.data.stars.event.LMHardwareEvent[] events =
	                				com.cannontech.database.data.stars.event.LMHardwareEvent.getAllLMHardwareEvents( hwDB.getInventoryID() );
	                		if (isInService(events)) break;
	                		
	                		cmdHwVct.addElement( hw );
	                		
	                		// Determine to update the last entry, or insert a new entry into the database
	                		boolean update = false;
	                		com.cannontech.database.data.stars.event.LMHardwareEvent lastHwEvent = null;
	                		if (events != null && events.length > 0) {
	                			lastHwEvent = events[events.length - 1];
			                	if (lastHwEvent.getAction().getEntryID().intValue() == futureActEntryID.intValue())
			                		update = true;
	                		}
		                		
	                		if (update) {
	                			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = lastHwEvent.getLMCustomerEventBase();
	                			eventBase.setActionID( actCompEntryID );
		                		eventBase.setEventDateTime( now );
		                		Transaction.createTransaction( Transaction.UPDATE, lastHwEvent ).execute();
	                		}
	                		else {	
		                		com.cannontech.database.data.stars.event.LMHardwareEvent event =
		                				new com.cannontech.database.data.stars.event.LMHardwareEvent();
		                		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
		                		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
		                		
		                		eventDB.setInventoryID( hwDB.getInventoryID() );
		                		eventBase.setEventTypeID( hwEventEntryID );
		                		eventBase.setActionID( actCompEntryID );
		                		eventBase.setEventDateTime( now );
		                		
	                			event.setEnergyCompanyBase( energyCompany );
		                		Transaction.createTransaction( Transaction.INSERT, event ).execute();
	                		}
	                		
			                Vector appVct = account.getApplianceVector();
			                for (int k = 0; k < appVct.size(); k++) {
			                	com.cannontech.database.data.stars.appliance.ApplianceBase app =
			                			(com.cannontech.database.data.stars.appliance.ApplianceBase) appVct.elementAt(k);
				                com.cannontech.database.db.stars.hardware.LMHardwareConfiguration config = app.getLMHardwareConfig();
				                
				                if (config.getInventoryID().intValue() == hwDB.getInventoryID().intValue()) {
				                	com.cannontech.database.data.device.lm.LMProgramBase program = app.getLMProgram();
						            if (program.getPAObjectID().intValue() == 0) continue;
						            cmdProgVct.addElement( program );
						            
						            com.cannontech.database.data.stars.event.LMProgramEvent lastProgEvent =
						            		com.cannontech.database.data.stars.event.LMProgramEvent.getLastProgramEvent( account.getCustomerAccount().getAccountID(), program.getPAObjectID() );
						            
						            if (update && lastProgEvent != null) {
			                			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = lastProgEvent.getLMCustomerEventBase();
							            eventBase.setActionID( actCompEntryID );
							            eventBase.setEventDateTime( now );
					                	Transaction.createTransaction( Transaction.UPDATE, lastProgEvent ).execute();
						            }
						            else {
							            com.cannontech.database.data.stars.event.LMProgramEvent event =
							            		new com.cannontech.database.data.stars.event.LMProgramEvent();
							            com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
							            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
							            
							            eventDB.setLMProgramID( program.getPAObjectID() );
							            eventDB.setAccountID( account.getCustomerAccount().getAccountID() );
							            eventBase.setEventTypeID( progEventEntryID );
							            eventBase.setActionID( actCompEntryID );
							            eventBase.setEventDateTime( now );
							            
							            event.setEnergyCompanyBase( energyCompany );
				                		Transaction.createTransaction( Transaction.INSERT, event ).execute();
				                	}
				                }
			                }
							
							break;
	                	}
	                }
                }
            }
            
            for (int i = 0; i < cmdHwVct.size(); i++) {
            	com.cannontech.database.data.stars.hardware.LMHardwareBase hw = (com.cannontech.database.data.stars.hardware.LMHardwareBase) cmdHwVct.get(i);
				StarsLMHardwareHistory hwHist = com.cannontech.database.data.stars.event.LMHardwareEvent.getStarsLMHardwareHistory( hw.getLMHardwareBase().getInventoryID() );
				hwHist.setInventoryID( hw.getLMHardwareBase().getInventoryID().intValue() );
				cmdResp.addStarsLMHardwareHistory( hwHist );
            }
            
            for (int i = 0; i < cmdProgVct.size(); i++) {
            	com.cannontech.database.data.device.lm.LMProgramBase program = (com.cannontech.database.data.device.lm.LMProgramBase) cmdProgVct.get(i);
	            StarsLMProgramHistory progHist = com.cannontech.database.data.stars.event.LMProgramEvent.getStarsLMProgramHistory(
	            		account.getCustomerAccount().getAccountID(), program.getPAObjectID() );
	            progHist.setProgramID( program.getPAObjectID().intValue() );
	            cmdResp.addStarsLMProgramHistory( progHist );
            }

            respOper.setStarsSwitchCommandResponse( cmdResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    void sendCommand(String command, ClientConnection conn)
    {
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

        conn.write(req);

        logger.info( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
    
    boolean isInService(com.cannontech.database.data.stars.event.LMHardwareEvent[] events) {
    	if (events == null) return false;
    	
    	for (int i = events.length - 1; i >= 0; i--) {
    		if (events[i].getAction().getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED ))
    			return true;
    		else if (events[i].getAction().getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION ))
    			return false;
    	}
    	return false;
    }
}