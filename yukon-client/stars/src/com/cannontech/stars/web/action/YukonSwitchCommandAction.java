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
import com.cannontech.stars.xml.serialize.StarsCustAccountInfo;
import com.cannontech.stars.xml.serialize.StarsDisableService;
import com.cannontech.stars.xml.serialize.StarsEnableService;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
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
			StarsCustAccountInfo accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
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
			
            if (operation.getStarsSwitchCommandResponse() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            	
            // Update hardware history
            StarsLMHardwareHistory hwHist = operation.getStarsSwitchCommandResponse().getStarsLMHardwareHistory();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsCustAccountInfo accountInfo = null;
			if (operator != null)
				accountInfo = (StarsCustAccountInfo) operator.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
			else
				accountInfo = (StarsCustAccountInfo) session.getAttribute("CUSTOMER_ACCOUNT_INFORMATION");
				
			StarsInventories inventories = accountInfo.getStarsInventories();
			for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
				StarsLMHardware hw = inventories.getStarsLMHardware(i);
				hw.setStarsLMHardwareHistory( hwHist );
			}
			
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
            
            /* This part is for consumer login, must be removed later */
            Integer energyCompanyID = (Integer) session.getAttribute("ENERGY_COMPANY_ID");
            StarsOperator operator = null;
            com.cannontech.database.data.stars.customer.CustomerAccount account = null;

            if (energyCompanyID == null) {
                operator = (StarsOperator) session.getAttribute("OPERATOR");
                if (operator != null)
                	account = (com.cannontech.database.data.stars.customer.CustomerAccount)
                			operator.getAttribute("CUSTOMER_ACCOUNT");
            }
            else {
            	account = (com.cannontech.database.data.stars.customer.CustomerAccount)
                		session.getAttribute("CUSTOMER_ACCOUNT");
            }
            
            if (account == null) {
                StarsFailure failure = new StarsFailure();
                failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
                failure.setDescription("Session invalidated, please login again");
                respOper.setStarsFailure( failure );
                return SOAPUtil.buildSOAPMessage( respOper );
            }

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
            
            Hashtable selectionList = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );
            StarsCustSelectionList actionList = (StarsCustSelectionList) selectionList.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION );
            StarsCustSelectionList custEventList = (StarsCustSelectionList) selectionList.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT );
            
            // Get list entry IDs
            Integer hwEventEntryID = null;
            Integer progEventEntryID = null;
            Integer tempTermEntryID = null;
            Integer futureActEntryID = null;
            Integer actCompEntryID = null;
            
            for (int i = 0; i < actionList.getStarsSelectionListEntryCount(); i++) {
            	StarsSelectionListEntry entry = actionList.getStarsSelectionListEntry(i);
            	if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT ))
            		hwEventEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMPROGRAMEVENT ))
            		progEventEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_TEMPTERMINATION ))
            		tempTermEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_FUTUREACTIVATION ))
            		futureActEntryID = new Integer( entry.getEntryID() );
            	else if (entry.getYukonDefinition().equalsIgnoreCase( com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_COMPLETED ))
            		actCompEntryID = new Integer( entry.getEntryID() );
            }
            
            if (command.getStarsDisableService() != null) {
                StarsDisableService service = command.getStarsDisableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service out serial " + service.getSerialNumber(i);
                    sendCommand(cmd, conn);
                }
                
    			com.cannontech.database.data.company.EnergyCompanyBase energyCompany = new com.cannontech.database.data.company.EnergyCompanyBase();
        		if (operator == null)
        			energyCompany.setEnergyCompanyID( energyCompanyID );
        		else
            		energyCompany.setEnergyCompanyID( new Integer((int) operator.getEnergyCompanyID()) );
                
                Vector invVct = account.getInventoryVector();
                for (int j = 0; j < invVct.size(); j++) {
                	com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
                			(com.cannontech.database.data.stars.hardware.LMHardwareBase) invVct.elementAt(j);
                	com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
                			
                	if (hwDB.getManufacturerSerialNumber().equalsIgnoreCase( service.getSerialNumber(j) )) {
                		com.cannontech.database.data.multi.MultiDBPersistent multiDB =
                				new com.cannontech.database.data.multi.MultiDBPersistent();
                				
                		com.cannontech.database.data.stars.event.LMHardwareEvent event =
                				new com.cannontech.database.data.stars.event.LMHardwareEvent();
                		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
                		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
                		
                		eventDB.setInventoryID( hwDB.getInventoryID() );
                		eventBase.setEventTypeID( hwEventEntryID );
                		eventBase.setActionID( tempTermEntryID );
                		eventBase.setEventDateTime( new Date() );
                		
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
					                
					            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
					            		new com.cannontech.database.data.stars.event.LMProgramEvent();
					            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
					            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
					            
					            eventDB1.setLMProgramID( program.getPAObjectID() );
					            eventDB1.setAccountID( account.getCustomerAccount().getAccountID() );
					            eventBase1.setEventTypeID( progEventEntryID );
					            eventBase1.setActionID( tempTermEntryID );
					            eventBase1.setEventDateTime( new Date() );
					            
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
                		
						StarsLMHardwareHistory hwHist = com.cannontech.database.data.stars.event.LMHardwareEvent.getStarsLMHardwareHistory( hwDB.getInventoryID() );
						cmdResp.setStarsLMHardwareHistory( hwHist );
						
                		break;
                	}
                }
            }
            else if (command.getStarsEnableService() != null) {
                StarsEnableService service = command.getStarsEnableService();

                for (int i = 0; i < service.getSerialNumberCount(); i++) {
                    String cmd = "putconfig service in serial " + service.getSerialNumber(i);
                    sendCommand(cmd, conn);
                }
                
    			com.cannontech.database.data.company.EnergyCompanyBase energyCompany = new com.cannontech.database.data.company.EnergyCompanyBase();
        		if (operator == null)
        			energyCompany.setEnergyCompanyID( energyCompanyID );
        		else
            		energyCompany.setEnergyCompanyID( new Integer((int) operator.getEnergyCompanyID()) );
                
                Vector invVct = account.getInventoryVector();
                for (int j = 0; j < invVct.size(); j++) {
                	com.cannontech.database.data.stars.hardware.LMHardwareBase hw =
                			(com.cannontech.database.data.stars.hardware.LMHardwareBase) invVct.elementAt(j);
                	com.cannontech.database.db.stars.hardware.LMHardwareBase hwDB = hw.getLMHardwareBase();
                			
                	if (hwDB.getManufacturerSerialNumber().equalsIgnoreCase( service.getSerialNumber(j) )) {
                		// Determine to update the last entry, or insert a new entry into the database
                		com.cannontech.database.data.stars.event.LMHardwareEvent lastHwEvent =
	                			com.cannontech.database.data.stars.event.LMHardwareEvent.getLastLMHardwareEvent( hwDB.getInventoryID() );
                		boolean update = false;
	                	if (lastHwEvent != null && lastHwEvent.getEventType().getEntryID().intValue() == futureActEntryID.intValue())
	                		update = true;
	                		
                		if (update) {
                			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = lastHwEvent.getLMCustomerEventBase();
                			eventBase.setActionID( actCompEntryID );
	                		eventBase.setEventDateTime( new Date() );
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
	                		eventBase.setEventDateTime( new Date() );
	                		
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
					            
					            com.cannontech.database.data.stars.event.LMProgramEvent lastProgEvent =
					            		com.cannontech.database.data.stars.event.LMProgramEvent.getLastProgramEvent( account.getCustomerAccount().getAccountID(), program.getPAObjectID() );
					            
					            if (update && lastProgEvent != null) {
		                			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = lastProgEvent.getLMCustomerEventBase();
						            eventBase.setActionID( actCompEntryID );
						            eventBase.setEventDateTime( new Date() );
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
						            eventBase.setEventDateTime( new Date() );
						            
						            event.setEnergyCompanyBase( energyCompany );
			                		Transaction.createTransaction( Transaction.INSERT, event ).execute();
			                	}
			                }
		                }
                		
						StarsLMHardwareHistory hwHist = com.cannontech.database.data.stars.event.LMHardwareEvent.getStarsLMHardwareHistory( hwDB.getInventoryID() );
						cmdResp.setStarsLMHardwareHistory( hwHist );
						
						break;
                	}
                }
            }

            respOper.setStarsSwitchCommandResponse( cmdResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void sendCommand(String command, ClientConnection conn)
    {
        com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
            new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

        conn.write(req);

        logger.info( "YukonSwitchCommandAction: Sent command to PIL: " + command );
    }
}