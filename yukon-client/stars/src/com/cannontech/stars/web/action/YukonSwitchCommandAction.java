package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
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
		
		String cmd = "putconfig service out serial " + liteHw.getManufacturerSerialNumber();
		
		int routeID = liteHw.getRouteID();
		if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
        
        ServerUtils.sendSerialCommand( cmd, routeID );
		
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
		
		String cmd = "putconfig service in serial " + liteHw.getManufacturerSerialNumber();
		
		int routeID = liteHw.getRouteID();
		if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
		
		ServerUtils.sendSerialCommand( cmd, routeID );
		
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
	
	public static void sendConfigCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw, boolean forceInService)
		throws WebClientException
	{
		if (liteHw.getManufacturerSerialNumber().length() == 0)
			throw new WebClientException( "The manufacturer serial # of the hardware cannot be empty" );
		
		Integer invID = new Integer( liteHw.getInventoryID() );
		Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
		Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
		Integer configEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID() );
		java.util.Date now = new java.util.Date();
        
        final int routeID = (liteHw.getRouteID() > 0)?
        		liteHw.getRouteID() : energyCompany.getDefaultRouteID();
        
		if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL || forceInService) {
			// Send an in service command first
			String cmd = "putconfig service in serial " + liteHw.getManufacturerSerialNumber();
			ServerUtils.sendSerialCommand( cmd, routeID );
			
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
			
			final String[] cfgCmds = getConfigCommands( liteHw, energyCompany );
			if (cfgCmds == null) return;
			
			// Send the config command a while later
			TimerTask sendCfgTask = new TimerTask() {
				public void run() {
					for (int i = 0; i < cfgCmds.length; i++)
						ServerUtils.sendSerialCommand( cfgCmds[i], routeID );
					CTILogger.info( "*** Config command sent ***" );
				}
			};
			
			new Timer().schedule( sendCfgTask, 5 * 1000 );
			CTILogger.info( "*** Send config command a while later ***" );
		}
		else {
			// Only send the config command
			String[] cfgCmds = getConfigCommands( liteHw, energyCompany );
			if (cfgCmds == null) return;
			
			for (int i = 0; i < cfgCmds.length; i++)
				ServerUtils.sendSerialCommand( cfgCmds[i], routeID );
		}
		
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
	
	private static String[] getConfigCommands(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany) {
		String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
		boolean useHardwareAddressing = (trackHwAddr != null) && Boolean.valueOf(trackHwAddr).booleanValue();
		
		ArrayList commands = new ArrayList();
		
		if (useHardwareAddressing) {
			if (liteHw.getLMConfiguration() == null)
				return null;
			
			if (liteHw.getLMConfiguration().getExpressCom() != null) {
				String program = null;
				String splinter = null;
				String load = null;
				String[] programs = liteHw.getLMConfiguration().getExpressCom().getProgram().split( "," );
				String[] splinters = liteHw.getLMConfiguration().getExpressCom().getSplinter().split( "," );
				
				for (int loadNo = 1; loadNo <= 8; loadNo++) {
					int prog = 0;
					if (programs.length >= loadNo && programs[loadNo-1].length() > 0)
						prog = Integer.parseInt( programs[loadNo-1] );
					int splt = 0;
					if (splinters.length >= loadNo && splinters[loadNo-1].length() > 0)
						splt = Integer.parseInt( splinters[loadNo-1] );
					
					if (prog > 0 || splt > 0) {
						if (program == null)
							program = String.valueOf( prog );
						else
							program += "," + String.valueOf( prog );
						if (splinter == null)
							splinter = String.valueOf( splt );
						else
							splinter += "," + String.valueOf( splt );
						if (load == null)
							load = String.valueOf( loadNo );
						else
							load += "," + String.valueOf( loadNo );
					}
				}
				
				String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " xcom assign" +
						" s " + liteHw.getLMConfiguration().getExpressCom().getServiceProvider() +
						" g " + liteHw.getLMConfiguration().getExpressCom().getGEO() +
						" b " + liteHw.getLMConfiguration().getExpressCom().getSubstation() +
						" f " + liteHw.getLMConfiguration().getExpressCom().getFeeder() +
						" z " + liteHw.getLMConfiguration().getExpressCom().getZip() +
						" u " + liteHw.getLMConfiguration().getExpressCom().getUserAddress();
				if (load != null)
					cmd += " p " + program + " r " + splinter + " load " + load;
				commands.add( cmd );
			}
			else if (liteHw.getLMConfiguration().getVersaCom() != null) {
				String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " vcom assign" +
						" u " + liteHw.getLMConfiguration().getVersaCom().getUtilityID() +
						" s " + liteHw.getLMConfiguration().getVersaCom().getSection() +
						" c 0x" + Integer.toHexString( liteHw.getLMConfiguration().getVersaCom().getClassAddress() ) +
						" d 0x" + Integer.toHexString( liteHw.getLMConfiguration().getVersaCom().getDivisionAddress() );
				commands.add( cmd );
			}
			else if (liteHw.getLMConfiguration().getSA205() != null) {
				String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " sa205 assign" +
						" 1," + liteHw.getLMConfiguration().getSA205().getSlot1() +
						",2," + liteHw.getLMConfiguration().getSA205().getSlot2() +
						",3," + liteHw.getLMConfiguration().getSA205().getSlot3() +
						",4," + liteHw.getLMConfiguration().getSA205().getSlot4() +
						",5," + liteHw.getLMConfiguration().getSA205().getSlot5() +
						",6," + liteHw.getLMConfiguration().getSA205().getSlot6();
				commands.add( cmd );
			}
			else if (liteHw.getLMConfiguration().getSA305() != null) {
				//TODO: command to be defined yet
			}
			else {
				return null;
			}
		}
		else {
			if (liteHw.getAccountID() > 0) {
				LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( liteHw.getAccountID(), true );
				for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
					LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
					if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getAddressingGroupID() > 0) {
						String groupName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( liteApp.getAddressingGroupID() );
						String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " template '" + groupName + "'";
						commands.add( cmd );
					}
				}
			}
		}
		
		if (commands.size() == 0) return null;
		
		String[] cfgCmds = new String[ commands.size() ];
		commands.toArray( cfgCmds );
		return cfgCmds;
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