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
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
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
			if (operator == null) return null;

            String action = req.getParameter("action");
            StarsYukonSwitchCommand command = new StarsYukonSwitchCommand();
            
            int invID = -1;
            String invIDStr = req.getParameter( "InvID" );
            if (invIDStr != null) {
            	try {
            		invID = Integer.parseInt( invIDStr );
            	}
            	catch (NumberFormatException nfe) {}
            }

            if (action.equalsIgnoreCase("DisableService")) {
            	if (invID < 0) return null;
                StarsDisableService service = new StarsDisableService();
                service.setInventoryID( invID );
                command.setStarsDisableService( service );
            }
            else if (action.equalsIgnoreCase("EnableService")) {
            	if (invID < 0) return null;
                StarsEnableService service = new StarsEnableService();
                service.setInventoryID( invID );
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

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
            StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) operator.getAttribute( "CUSTOMER_ACCOUNT_INFORMATION" );
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
                
			Integer energyCompanyID = new Integer((int) operator.getEnergyCompanyID());
            
            StarsYukonSwitchCommand command = reqOper.getStarsYukonSwitchCommand();
            StarsYukonSwitchCommandResponse cmdResp = new StarsYukonSwitchCommandResponse();
            
            // Get list entry IDs
            Hashtable selectionLists = SOAPServer.getAllSelectionLists( energyCompanyID );
            
            Integer hwEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT)
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
            
            if (command.getStarsDisableService() != null) {
                StarsDisableService service = command.getStarsDisableService();
                Integer invID = new Integer( service.getInventoryID() );
        		LiteLMHardware liteHw = SOAPServer.getLMHardware( energyCompanyID, invID, true );
        		
        		if (liteHw == null) {
	            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the LM hardware to be disabled") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}

                String cmd = "putconfig service out serial " + liteHw.getManufactureSerialNumber();
                ServerUtils.sendCommand(cmd, conn);
        		
        		// Add "Temp Opt Out" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( tempTermEntryID );
        		eventBase.setEventDateTime( new Date() );
        		
        		event.setEnergyCompanyID( energyCompanyID );
        		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
        				Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				// Update lite object and create response
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
				cmdResp.setStarsLMHardwareHistory( hwHist );
            }
            else if (command.getStarsEnableService() != null) {
                StarsEnableService service = command.getStarsEnableService();
                Integer invID = new Integer( service.getInventoryID() );
        		LiteLMHardware liteHw = SOAPServer.getLMHardware( energyCompanyID, invID, true );
        		
        		if (liteHw == null) {
	            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the LM hardware to be enabled") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}

                String cmd = "putconfig service in serial " + liteHw.getManufactureSerialNumber();
                ServerUtils.sendCommand(cmd, conn);
        		
        		// Add "Activation Completed" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( actCompEntryID );
        		eventBase.setEventDateTime( new Date() );
        		
        		event.setEnergyCompanyID( energyCompanyID );
        		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
        				Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				// Update lite object and create response
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
				cmdResp.setStarsLMHardwareHistory( hwHist );
            }

            respOper.setStarsYukonSwitchCommandResponse( cmdResp );
            return SOAPUtil.buildSOAPMessage( respOper );
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
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) operator.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + "CUSTOMER_ACCOUNT_INFORMATION");
			
			Integer energyCompanyID = new Integer( (int) operator.getEnergyCompanyID() );
			Hashtable selectionLists = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LISTS" );
			DeviceStatus hwStatus = null;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			if (reqOper.getStarsYukonSwitchCommand().getStarsEnableService() != null) {
				hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
						StarsCustListEntryFactory.getStarsCustListEntry(
							(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS),
							com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_AVAIL),
						DeviceStatus.class );
			}
			else {
				hwStatus = (DeviceStatus) StarsCustListEntryFactory.newStarsCustListEntry(
						StarsCustListEntryFactory.getStarsCustListEntry(
							(StarsCustSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_DEVICESTATUS),
							com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_DEVSTAT_TEMPUNAVAIL),
						DeviceStatus.class );
			}
				
            // Update hardware history
            StarsLMHardwareHistory hwHist = resp.getStarsLMHardwareHistory();
			StarsInventories inventories = accountInfo.getStarsInventories();
			for (int j = 0; j < inventories.getStarsLMHardwareCount(); j++) {
				StarsLMHardware hw = inventories.getStarsLMHardware(j);
				if (hw.getInventoryID() == hwHist.getInventoryID()) {
					hw.setStarsLMHardwareHistory( hwHist );
					hw.setDeviceStatus( hwStatus );
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