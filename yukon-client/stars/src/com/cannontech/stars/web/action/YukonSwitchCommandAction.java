package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.util.task.SendConfigCommandTask;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsConfig;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDisableService;
import com.cannontech.stars.xml.serialize.StarsEnableService;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsYukonSwitchCommand;
import com.cannontech.stars.xml.serialize.StarsYukonSwitchCommandResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;

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

			if (action.equalsIgnoreCase("DisableLMHardware")) {
				if (invID < 0) return null;
				StarsDisableService service = new StarsDisableService();
				service.setInventoryID( invID );
				command.setStarsDisableService( service );
			}
			else if (action.equalsIgnoreCase("EnableLMHardware")) {
				if (invID < 0) return null;
				StarsEnableService service = new StarsEnableService();
				service.setInventoryID( invID );
				command.setStarsEnableService( service );
			}
			else if (action.equalsIgnoreCase("UpdateLMHardwareConfig")) {
				if (invID < 0) return null;
				StarsConfig service = new StarsConfig();
				service.setInventoryID( invID );
				command.setStarsConfig( service );
			}

			StarsOperation operation = new StarsOperation();
			operation.setStarsYukonSwitchCommand( command );
            
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
            
			StarsYukonSwitchCommand command = reqOper.getStarsYukonSwitchCommand();
			StarsYukonSwitchCommandResponse cmdResp = new StarsYukonSwitchCommandResponse();
            
			try {
				LiteStarsLMHardware liteHw = null;
				String action = null;
				
				if (command.getStarsDisableService() != null) {
					int invID = command.getStarsDisableService().getInventoryID();
					liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
					sendDisableCommand( energyCompany, liteHw );
					action = ActivityLogActions.HARDWARE_DISABLE_ACTION;
				}
				else if (command.getStarsEnableService() != null) {
					int invID = command.getStarsEnableService().getInventoryID();
					liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
					sendEnableCommand( energyCompany, liteHw );
					action = ActivityLogActions.HARDWARE_ENABLE_ACTION;
				}
				else if (command.getStarsConfig() != null) {
					int invID = command.getStarsConfig().getInventoryID();
					liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
					sendConfigCommand( energyCompany, liteHw, true );
					action = ActivityLogActions.HARDWARE_CONFIGURATION_ACTION;
				}
				
				ActivityLogger.logEvent( user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(),
						liteAcctInfo.getCustomer().getCustomerID(), action, "Serial #:" + liteHw.getManufacturerSerialNumber() );
				
				StarsInventory starsInv = StarsLiteFactory.createStarsInventory( liteHw, energyCompany );
				cmdResp.setStarsInventory( starsInv );
			}
			catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			
			respOper.setStarsYukonSwitchCommandResponse( cmdResp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send the switch command") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
        
		return null;
	}

	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}

			StarsYukonSwitchCommandResponse resp = operation.getStarsYukonSwitchCommandResponse();
			if (resp == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			parseResponse( accountInfo, resp.getStarsInventory() );
			
			StarsYukonSwitchCommand command = SOAPUtil.parseSOAPMsgForOperation( reqMsg ).getStarsYukonSwitchCommand();
			if (command.getStarsEnableService() != null)
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Enable command sent out successfully" );
			else if (command.getStarsDisableService() != null)
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Disable command sent out successfully" );
			else if (command.getStarsConfig() != null)
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Hardware configuration sent out successfully" );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
    
	public static void sendDisableCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw) throws WebClientException {
		if (liteHw.getManufacturerSerialNumber().trim().length() == 0)
			throw new WebClientException( "The manufacturer serial # of the hardware cannot be empty" );
        
		Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
		Integer termEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID() );
		java.util.Date now = new java.util.Date();
		
		int routeID = liteHw.getRouteID();
		if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
		
		String cmd = "putconfig service out serial " + liteHw.getManufacturerSerialNumber();
        
		com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		synchronized (yc) {
			yc.setRouteID( routeID );
			yc.setCommand( cmd );
			yc.handleSerialNumber();
		}
		
		// Add "Termination" to hardware events
		try {
			com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
			com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
			
			eventDB.setInventoryID( new Integer(liteHw.getInventoryID()) );
			eventBase.setEventTypeID( hwEventEntryID );
			eventBase.setActionID( termEntryID );
			eventBase.setEventDateTime( new Date() );
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			
			event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
					Transaction.createTransaction( Transaction.INSERT, event ).execute();
			
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteHw.getInventoryHistory().add( liteEvent );
			liteHw.updateDeviceStatus();
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
    
	public static void sendEnableCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw) throws WebClientException {
		if (liteHw.getManufacturerSerialNumber().length() == 0)
			throw new WebClientException( "The manufacturer serial # of the hardware cannot be empty" );
        
		Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
		Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
		java.util.Date now = new java.util.Date();
		
		int routeID = liteHw.getRouteID();
		if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
		
		String cmd = "putconfig service in serial " + liteHw.getManufacturerSerialNumber();
        
		com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		synchronized (yc) {
			yc.setRouteID( routeID );
			yc.setCommand( cmd );
			yc.handleSerialNumber();
		}
		
		// Add "Activation Completed" to hardware events
		try {
			com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
			com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
			
			eventDB.setInventoryID( new Integer(liteHw.getInventoryID()) );
			eventBase.setEventTypeID( hwEventEntryID );
			eventBase.setActionID( actCompEntryID );
			eventBase.setEventDateTime( new Date() );
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			
			event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
					Transaction.createTransaction( Transaction.INSERT, event ).execute();
			
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteHw.getInventoryHistory().add( liteEvent );
			liteHw.updateDeviceStatus();
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}
	
	public static void sendConfigCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw, boolean forceInService) throws WebClientException {
		if (liteHw.getManufacturerSerialNumber().length() == 0)
			throw new WebClientException( "The manufacturer serial # of the hardware cannot be empty" );
        
		Integer invID = new Integer( liteHw.getInventoryID() );
		Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
		Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
		Integer configEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID() );
		java.util.Date now = new java.util.Date();
        
        int routeID = liteHw.getRouteID();
        if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
        
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] configs =
				com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getALLHardwareConfigs( invID );
		
		if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL || forceInService) {
			if (configs.length > 0) {
				// Send an in service command followed by a config command
				new Thread( new SendConfigCommandTask(energyCompany, liteHw) ).start();
			}
			else {
				// Only send an in service command
				String cmd = "putconfig service in serial " + liteHw.getManufacturerSerialNumber();
		        
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( routeID );
					yc.setCommand( cmd );
					yc.handleSerialNumber();
				}
			}
			
			// Add "Activation Completed" to hardware events
			try {
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
				
				eventDB.setInventoryID( invID );
				eventBase.setEventTypeID( hwEventEntryID );
				eventBase.setActionID( actCompEntryID );
				eventBase.setEventDateTime( now );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getInventoryHistory().add( liteEvent );
				liteHw.updateDeviceStatus();
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		else {
			// Only send a config command
			for (int i = 0; i < configs.length; i++) {
				if (configs[i].getAddressingGroupID().intValue() == 0) continue;
				
				String groupName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( configs[i].getAddressingGroupID().intValue() );
				String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " template '" + groupName + "'";
	            
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( routeID );
					yc.setCommand( cmd );
					yc.handleSerialNumber();
				}
			}
		}
		
		if (configs.length > 0) {
			// Add "Config" to hardware events
			try {
				com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
				com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
				com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
				
				eventDB.setInventoryID( invID );
				eventBase.setEventTypeID( hwEventEntryID );
				eventBase.setActionID( configEntryID );
				eventBase.setEventDateTime( now );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getInventoryHistory().add( liteEvent );
			}
			catch (TransactionException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	public static void parseResponse(StarsCustAccountInformation starsAcctInfo, StarsInventory starsInv) {
		StarsInventories inventories = starsAcctInfo.getStarsInventories();
		
		for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
			StarsInventory inv = inventories.getStarsInventory(i);
			if (inv.getInventoryID() == starsInv.getInventoryID()) {
				inventories.setStarsInventory(i, starsInv);
				break;
			}
		}
	}
}