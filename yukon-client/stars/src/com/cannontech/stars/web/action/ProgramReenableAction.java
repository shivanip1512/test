package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteLMHardwareEvent;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.StarsAppliance;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramReenable;
import com.cannontech.stars.xml.serialize.StarsProgramReenableResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.util.ServletUtil;

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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;

			StarsProgramReenable reEnable = new StarsProgramReenable();
			if (req.getParameter("action").equalsIgnoreCase("CancelScheduledOptOut"))
				reEnable.setCancelScheduledOptOut( true );
            
			StarsOperation operation = new StarsOperation();
			operation.setStarsProgramReenable( reEnable );
            
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
		LiteStarsEnergyCompany energyCompany = null;
        
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
			
			energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
            
			StarsProgramReenable reenable = reqOper.getStarsProgramReenable();
			StarsProgramReenableResponse resp = new StarsProgramReenableResponse();
	        
			// Get the notification message to send later
			String notifMsg = null;
			String notifRecip = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.OPTOUT_NOTIFICATION_RECIPIENTS );
			if (notifRecip != null && notifRecip.trim().length() > 0)
				notifMsg = SendOptOutNotificationAction.getReenableNotifMessage( energyCompany, liteAcctInfo, reqOper );
			
			if (reenable.getCancelScheduledOptOut()) {
				// Cancel all the scheduled opt out events
				OptOutEventQueue.OptOutEvent[] events = OptOutEventQueue.getInstance().findOptOutEvents( liteAcctInfo.getAccountID() );
				if (events.length == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "There is no " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event to be canceled") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				for (int i = 0; i < events.length; i++)
					OptOutEventQueue.getInstance().removeEvent( events[i] );
				
				resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
				resp.setDescription( "The scheduled " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event has been canceled" );
				
				ActivityLogger.logEvent(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(),
						liteAcctInfo.getCustomer().getCustomerID(), ActivityLogActions.PROGRAM_CANCEL_SCHEDULED_ACTION, "" );
			}
			else {
				// Get all the hardwares to be reenabled
				ArrayList hardwares = null;
				if (reenable.hasInventoryID()) {
					hardwares = new ArrayList();
					hardwares.add( energyCompany.getInventory(reenable.getInventoryID(), true) );
				}
				else {
					hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, energyCompany );
					if (hardwares.size() == 0) {
						respOper.setStarsFailure( StarsFactory.newStarsFailure(
								StarsConstants.FAILURE_CODE_OPERATION_FAILED, "There is no hardware to be reenabled") );
						return SOAPUtil.buildSOAPMessage( respOper );
					}
				}
				
				for (int i = 0; i < hardwares.size(); i++) {
					LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
					
					String cmd = getReenableCommand( liteHw, energyCompany );
					int routeID = liteHw.getRouteID();
					if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
					ServerUtils.sendSerialCommand( cmd, routeID );
					
					StarsLMHardwareHistory hwHist = processReenable( liteHw, liteAcctInfo, energyCompany );
					resp.addStarsLMHardwareHistory( hwHist );
				}
				
				OptOutEventQueue.OptOutEvent[] events = OptOutEventQueue.getInstance().findReenableEvents( liteAcctInfo.getAccountID() );
				for (int i = 0; i < events.length; i++) {
					if (events[i].getInventoryID() == reenable.getInventoryID()) {
						OptOutEventQueue.getInstance().removeEvent( events[i] );
						break;
					}
				}
				
				resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
				resp.setDescription( ServletUtil.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE)) + " command has been sent out successfully." );
				
				String logMsg = "Serial #:" + ((LiteStarsLMHardware) hardwares.get(0)).getManufacturerSerialNumber();
				for (int i = 1; i < hardwares.size(); i++)
					logMsg += "," + ((LiteStarsLMHardware) hardwares.get(i)).getManufacturerSerialNumber();
				ActivityLogger.logEvent(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
						ActivityLogActions.PROGRAM_REENABLE_ACTION, logMsg );
			}
			
			// Send the notification message saved earlier
			if (notifMsg != null) {
				try {
					SendOptOutNotificationAction.sendNotification( notifMsg, energyCompany );
				}
				catch (Exception e) {
					CTILogger.error( e.getMessage(), e );
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to send the " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) + " notification.") );
				}
			}
			
			respOper.setStarsProgramReenableResponse( resp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Failed to " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) + " the programs.") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
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

			StarsProgramReenableResponse resp = operation.getStarsProgramReenableResponse();
			if (resp != null) {
				if (resp.getDescription() != null)
					session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
	            
				StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
						session.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
	            
				StarsYukonUser user = (StarsYukonUser) session.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
				LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
	            
				parseResponse( resp, accountInfo, energyCompany );
			}
            
			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private static StarsLMHardwareHistory processReenable(LiteStarsLMHardware liteHw, 
		LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
		throws com.cannontech.database.TransactionException
	{
		Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
		Integer progEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
		Integer futureActEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID() );
		Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
        
		Date now = new Date();	// Current date, all customer events will use exactly the same date
        
		ProgramOptOutAction.removeFutureActivationEvents( liteHw, liteAcctInfo, energyCompany );
        
		// Add "Activation Completed" to hardware events
		com.cannontech.database.data.stars.event.LMHardwareEvent hwEvent =
				new com.cannontech.database.data.stars.event.LMHardwareEvent();
		
		hwEvent.getLMHardwareEvent().setInventoryID( new Integer(liteHw.getInventoryID()) );
		hwEvent.getLMCustomerEventBase().setEventTypeID( hwEventEntryID );
		hwEvent.getLMCustomerEventBase().setActionID( actCompEntryID );
		hwEvent.getLMCustomerEventBase().setEventDateTime( now );
		hwEvent.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		
		hwEvent = (com.cannontech.database.data.stars.event.LMHardwareEvent)
				Transaction.createTransaction( Transaction.INSERT, hwEvent ).execute();
		
		// Update lite objects and create response
		LiteLMHardwareEvent liteHwEvent = (LiteLMHardwareEvent) StarsLiteFactory.createLite( hwEvent );
		liteHw.getInventoryHistory().add( liteHwEvent );
		liteHw.updateDeviceStatus();
		
		StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
		hwHist.setInventoryID( liteHw.getInventoryID() );
		
		for (int k = 0; k < liteHw.getInventoryHistory().size(); k++) {
			liteHwEvent = (LiteLMHardwareEvent) liteHw.getInventoryHistory().get(k);
			StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
			StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteHwEvent );
			hwHist.addStarsLMHardwareEvent( starsEvent );
		}
		
		// Add "Activation Completed" to program events
		for (int i = 0; i < liteAcctInfo.getAppliances().size(); i++) {
			LiteStarsAppliance liteApp = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(i);
			if (liteApp.getInventoryID() == liteHw.getInventoryID() && liteApp.getProgramID() > 0) {
				LiteStarsLMProgram liteProg = ProgramSignUpAction.getLMProgram( liteAcctInfo, liteApp.getProgramID() );
	        	
				// If program is already in service, do nothing
				if (liteProg.isInService()) continue;
	    		
				com.cannontech.database.data.stars.event.LMProgramEvent event =
						new com.cannontech.database.data.stars.event.LMProgramEvent();
	            
				event.getLMProgramEvent().setProgramID( new Integer(liteProg.getProgramID()) );
				event.getLMProgramEvent().setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
				event.getLMCustomerEventBase().setEventTypeID( progEventEntryID );
				event.getLMCustomerEventBase().setActionID( actCompEntryID );
				event.getLMCustomerEventBase().setEventDateTime( now );
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				
				event = (com.cannontech.database.data.stars.event.LMProgramEvent)
						Transaction.createTransaction( Transaction.INSERT, event ).execute();
	    		
				// Update lite objects and create response
				LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) StarsLiteFactory.createLite( event );
				liteAcctInfo.getProgramHistory().add( liteEvent );
				liteProg.updateProgramStatus( liteAcctInfo.getProgramHistory() );
			}
		}
        
		return hwHist;
	}
	
	private static void parseResponse(StarsProgramReenableResponse resp,
		StarsCustAccountInformation starsAcctInfo, LiteStarsEnergyCompany energyCompany)
	{
		StarsLMPrograms programs = starsAcctInfo.getStarsLMPrograms();
		if (resp.getStarsLMProgramHistory() != null)
			programs.setStarsLMProgramHistory( resp.getStarsLMProgramHistory() );
		
		StarsInventories inventories = starsAcctInfo.getStarsInventories();
		DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
				energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL),
				DeviceStatus.class );
		
		for (int i = 0; i < resp.getStarsLMHardwareHistoryCount(); i++) {
			StarsLMHardwareHistory hwHist = resp.getStarsLMHardwareHistory(i);
			
			// Update the hardware status
			for (int j = 0; j < inventories.getStarsInventoryCount(); j++) {
				StarsInventory inv = inventories.getStarsInventory(j);
				if (inv.getInventoryID() == hwHist.getInventoryID()) {
					inv.setStarsLMHardwareHistory( hwHist );
					inv.setDeviceStatus( hwStatus );
					break;
				}
			}
			
			// Update the corresponding program status
			for (int j = 0; j < starsAcctInfo.getStarsAppliances().getStarsApplianceCount(); j++) {
				StarsAppliance app = starsAcctInfo.getStarsAppliances().getStarsAppliance(j);
				if (app.getInventoryID() == hwHist.getInventoryID() && app.getProgramID() > 0) {
					for (int k = 0; k < programs.getStarsLMProgramCount(); k++) {
						if (programs.getStarsLMProgram(k).getProgramID() == app.getProgramID()) {
							programs.getStarsLMProgram(k).setStatus( ServletUtils.IN_SERVICE );
							break;
						}
					}
				}
			}
		}
	}
	
	public static String getReenableCommand(LiteStarsLMHardware liteHw, LiteStarsEnergyCompany energyCompany)
		throws WebClientException
	{
		if (liteHw.getManufacturerSerialNumber().trim().length() == 0)
			throw new WebClientException( "The serial # of the hardware cannot be empty" );
		
		String cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber();
		int hwConfigType = ECUtils.getHardwareConfigType( liteHw.getLmHardwareTypeID() );
		if (hwConfigType == ECUtils.HW_CONFIG_TYPE_VERSACOM) {
			cmd += " vcom service in temp";
		}
		else if (hwConfigType == ECUtils.HW_CONFIG_TYPE_EXPRESSCOM) {
			cmd += " xcom service in temp";
		}
		else if (hwConfigType == ECUtils.HW_CONFIG_TYPE_SA205) {
			cmd += " sa205 service out temp offhours 0";
		}
		else if (hwConfigType == ECUtils.HW_CONFIG_TYPE_SA305) {
			String trackHwAddr = energyCompany.getEnergyCompanySetting( EnergyCompanyRole.TRACK_HARDWARE_ADDRESSING );
			if (trackHwAddr == null || !Boolean.valueOf(trackHwAddr).booleanValue())
				throw new WebClientException("The utility ID of the SA305 switch is unknown");
			
			cmd += " sa305 utility " + liteHw.getLMConfiguration().getSA305().getUtility() + " override 0";
		}
		
		return cmd;
	}
	
	public static void handleReenableEvent(OptOutEventQueue.OptOutEvent event, LiteStarsEnergyCompany energyCompany) {
		LiteStarsCustAccountInformation liteAcctInfo = energyCompany.getCustAccountInformation( event.getAccountID(), true );
		
		ArrayList hardwares = new ArrayList();
		if (event.getInventoryID() != 0)
			hardwares.add( energyCompany.getInventory(event.getInventoryID(), true) );
		else
			hardwares = ProgramOptOutAction.getAffectedHardwares( liteAcctInfo, energyCompany );
		
		StarsProgramReenableResponse resp = new StarsProgramReenableResponse();
		
		for (int i = 0; i < hardwares.size(); i++) {
			LiteStarsLMHardware liteHw = (LiteStarsLMHardware) hardwares.get(i);
			try {
				resp.addStarsLMHardwareHistory( processReenable(liteHw, liteAcctInfo, energyCompany) );
			}
			catch (com.cannontech.database.TransactionException e) {
				CTILogger.error( e.getMessage(), e );
			}
		}
		
		resp.setStarsLMProgramHistory( StarsLiteFactory.createStarsLMProgramHistory(liteAcctInfo, energyCompany) );
		
		StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
				energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		if (starsAcctInfo != null)
			parseResponse( resp, starsAcctInfo, energyCompany );
	}

}
