package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
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
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustListEntry;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
import com.cannontech.stars.xml.serialize.StarsLMProgram;
import com.cannontech.stars.xml.serialize.StarsLMProgramEvent;
import com.cannontech.stars.xml.serialize.StarsLMProgramHistory;
import com.cannontech.stars.xml.serialize.StarsLMPrograms;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsProgramOptOut;
import com.cannontech.stars.xml.serialize.StarsProgramOptOutResponse;
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
public class ProgramOptOutAction implements ActionBase {
	
	public static final int REPEAT_LAST = -1;
	public static final int OPTOUT_TODAY = -2;
	
	public static final String ERROR_MSG_LABEL = "ERROR:";

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
					user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
            
            StarsProgramOptOut optOut = new StarsProgramOptOut();
            
            int period = 0;
            if (req.getParameter("OptOutPeriod") != null) {
	            String periodStr = req.getParameter("OptOutPeriod");
	            if (periodStr != null)
	                try {
	                    period = Integer.parseInt(periodStr);
	                }
	                catch (NumberFormatException e) {}
            }
            if (period == 0) return null;
            
            if (period < 0) {	// -period is the number of days to be opted out
            	optOut.setPeriod( Math.abs(period) );
            }
            else {	// This is a special entry, e.g. "Today"
            	String optOutPrdListName = (selectionLists.get(YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD) != null)?
						YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD : YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD_CUS;
				
            	StarsCustListEntry entryTomorrow = ServletUtils.getStarsCustListEntry(
            			selectionLists, optOutPrdListName, YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TOMORROW );
            	StarsCustListEntry entryToday = ServletUtils.getStarsCustListEntry(
            			selectionLists, optOutPrdListName, YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TODAY );
            	StarsCustListEntry entryRepeatLast = ServletUtils.getStarsCustListEntry(
            			selectionLists, optOutPrdListName, YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_REPEAT_LAST );
            			
            	if (entryTomorrow != null && period == entryTomorrow.getEntryID() )
            	{
            		java.util.TimeZone tz = java.util.TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
            		optOut.setStartDateTime( ServletUtil.getTomorrow(tz) );
            		optOut.setPeriod( 1 );
            	}
            	else if (entryToday != null && period == entryToday.getEntryID() )
            	{
            		optOut.setPeriod( OPTOUT_TODAY );
            	}
            	else if (entryRepeatLast != null && period == entryRepeatLast.getEntryID() )
            	{
            		optOut.setPeriod( REPEAT_LAST );
            	}
            }

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
        LiteStarsEnergyCompany energyCompany = null;
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
			
            int energyCompanyID = user.getEnergyCompanyID();
        	energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
			TimeZone tz = TimeZone.getTimeZone( energyCompany.getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
			
            StarsProgramOptOut optOut = reqOper.getStarsProgramOptOut();
            StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
			
            if (optOut.getStartDateTime() != null) {
            	// Store the opt out commands in memory for execution later
            	OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
            	event.setEnergyCompanyID( energyCompanyID );
            	event.setStartDateTime( optOut.getStartDateTime().getTime() );
            	event.setPeriod( optOut.getPeriod() );
            	event.setAccountID( liteAcctInfo.getCustomerAccount().getAccountID() );
            	
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, optOut.getPeriod() * 24 );
            	StringBuffer cmd = new StringBuffer();
            	if (commands.length > 0) {
            		cmd.append( commands[0] );
            		for (int i = 1; i < commands.length; i++)
            			cmd.append( "," ).append( commands[i] );
            	}
            	event.setCommand( cmd.toString() );
            	energyCompany.getOptOutEventQueue().addEvent( event );
            	
            	resp.setDescription( "The " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event has been scheduled" );
            }
            else if (optOut.getPeriod() == OPTOUT_TODAY) {
            	int offHours = (int)((ServletUtil.getTomorrow(tz).getTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, offHours );
			
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( energyCompany.getDefaultRouteID() );
					for (int i = 0; i < commands.length; i++) {
						yc.setCommandFileName( commands[i] );
						yc.handleSerialNumber();
					}
				}
            	
            	OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
            	event.setEnergyCompanyID( energyCompanyID );
            	event.setStartDateTime( ServletUtil.getTomorrow(tz).getTime() );
            	event.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
            	event.setAccountID( liteAcctInfo.getCustomerAccount().getAccountID() );
            	
				commands = ProgramReenableAction.getReenableCommands( liteAcctInfo, energyCompany );
            	StringBuffer cmd = new StringBuffer();
            	if (commands.length > 0) {
            		cmd.append( commands[0] );
            		for (int i = 1; i < commands.length; i++)
            			cmd.append( "," ).append( commands[i] );
            	}
            	event.setCommand( cmd.toString() );
            	energyCompany.getOptOutEventQueue().addEvent( event );
            	
            	resp = processOptOut( liteAcctInfo, energyCompany, optOut );
		        if (ServerUtils.isOperator( user ))
			        resp.setDescription( ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " command has been sent out successfully" );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) );
            }
            else if (optOut.getPeriod() == REPEAT_LAST) {
				/* We will only resend the command if there is no opt out event in the event queue
				 * (otherwise the command hasn't been sent out yet), and there is a reenable event
				 * (the opt out event is still active)
				 */
				OptOutEventQueue queue = energyCompany.getOptOutEventQueue();
				OptOutEventQueue.OptOutEvent e1 = queue.findOptOutEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
				OptOutEventQueue.OptOutEvent e2 = queue.findReenableEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
				
				if (e1 == null || e2 != null) {
					int offHours = (int) ((e2.getStartDateTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
					String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, offHours );
					
					com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
					synchronized (yc) {
						yc.setRouteID( energyCompany.getDefaultRouteID() );
						for (int i = 0; i < commands.length; i++) {
							yc.setCommandFileName( commands[i] );
							yc.handleSerialNumber();
						}
					}
					
					resp.setDescription( "The last " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " command has been resent" );
				}
				else {
					resp.setDescription( ERROR_MSG_LABEL + "There is no active " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event, command not sent" );
				}
            }
            else {
            	// Send opt out commands immediately, and store the reenable command in memory
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, optOut.getPeriod() * 24 );
				
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( energyCompany.getDefaultRouteID() );
					for (int i = 0; i < commands.length; i++) {
						yc.setCommandFileName( commands[i] );
						yc.handleSerialNumber();
					}
				}
            	
            	OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
            	event.setEnergyCompanyID( energyCompanyID );
				Calendar cal = Calendar.getInstance();
				cal.add( Calendar.DATE, optOut.getPeriod() );
            	event.setStartDateTime( cal.getTime().getTime() );
            	event.setPeriod( OptOutEventQueue.PERIOD_REENABLE );
            	event.setAccountID( liteAcctInfo.getCustomerAccount().getAccountID() );
            	
				commands = ProgramReenableAction.getReenableCommands( liteAcctInfo, energyCompany );
            	StringBuffer cmd = new StringBuffer();
            	if (commands.length > 0) {
            		cmd.append( commands[0] );
            		for (int i = 1; i < commands.length; i++)
            			cmd.append( "," ).append( commands[i] );
            	}
            	event.setCommand( cmd.toString() );
            	energyCompany.getOptOutEventQueue().addEvent( event );
            	
            	resp = processOptOut( liteAcctInfo, energyCompany, optOut );
		        if (ServerUtils.isOperator( user ))
			        resp.setDescription( ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN)) + " command has been sent out successfully" );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_PAST) );
            }

        	respOper.setStarsProgramOptOutResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_VERB) + " the programs") );
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
			
			if (resp.getDescription() != null) {
				if (resp.getDescription().startsWith( ERROR_MSG_LABEL ))
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, resp.getDescription().substring(ERROR_MSG_LABEL.length()) );
				else
					session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
			}
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
            // Update program history
            if (resp.getStarsLMProgramHistoryCount() > 0) {
				StarsLMPrograms programs = accountInfo.getStarsLMPrograms();
	            for (int i = 0; i < resp.getStarsLMProgramHistoryCount(); i++) {
	            	StarsLMProgramHistory progHist = resp.getStarsLMProgramHistory(i);
	            	
	            	for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
	            		StarsLMProgram program = programs.getStarsLMProgram(j);
	            		if (program.getProgramID() == progHist.getProgramID()) {
	            			program.setStarsLMProgramHistory( progHist );
	            			program.setStatus( ServletUtils.OUT_OF_SERVICE );
	            		}
	            	}
	            }
	            ServletUtils.removeProgramHistory( accountInfo.getStarsCustomerAccount().getAccountID() );
            }
            
            // Update hardware history
            if (resp.getStarsLMHardwareHistoryCount() > 0 && accountInfo.getStarsInventories() != null) {
				Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
				DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntry(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL),
						DeviceStatus.class );
            	
				StarsInventories inventories = accountInfo.getStarsInventories();
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
	
	/**
	 * Return the list of hardware IDs to be disabled
	 */
	private static ArrayList getHardwareIDs(LiteStarsCustAccountInformation liteAcctInfo) {
        ArrayList hwIDList = new ArrayList();
        
        for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
        	LiteStarsLMProgram program = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
        	
        	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
        		LiteStarsAppliance appliance = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
        		
        		if (appliance.getLmProgramID() == program.getLmProgram().getProgramID()) {
        			if (appliance.getInventoryID() > 0) {
	        			Integer hardwareID = new Integer( appliance.getInventoryID() );
	        			if (!hwIDList.contains( hardwareID )) hwIDList.add( hardwareID );
        			}
        			
        			break;
        		}
        	}
        }
        
        return hwIDList;
	}
	
	private static String[] getOptOutCommands(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, int offHours)
	throws Exception {
        ArrayList hwIDList = getHardwareIDs( liteAcctInfo );
		String[] commands = new String[ hwIDList.size() ];

        for (int i = 0; i < hwIDList.size(); i++) {
        	Integer invID = (Integer) hwIDList.get(i);
        	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID.intValue(), true );
        	
    		if (liteHw.getManufactureSerialNumber().trim().length() == 0)
    			throw new Exception( "The manufacturer serial # of the hardware cannot be empty" );
            
            commands[i] = "putconfig serial " + liteHw.getManufactureSerialNumber() +
            			" service out temp offhours " + String.valueOf(offHours);
        }
        
        return commands;
	}
	
	private static StarsProgramOptOutResponse processOptOut(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramOptOut optOut)
			throws com.cannontech.database.TransactionException
	{
		TimeZone tz = TimeZone.getTimeZone( energyCompany.getEnergyCompanySetting(EnergyCompanyRole.DEFAULT_TIME_ZONE) );
		StarsProgramOptOutResponse resp = new StarsProgramOptOutResponse();
		
        Integer hwEventEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer progEventEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
        Integer tempTermEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION).getEntryID() );
        Integer futureActEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID() );
        Integer actCompEntryID = new Integer(
        		energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
		
		Date optOutDate = optOut.getStartDateTime();
		if (optOutDate == null) optOutDate = new Date();
		
		Date reenableDate = null;
		if (optOut.getPeriod() > 0) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime( optOutDate );
	        cal.add( Calendar.DATE, optOut.getPeriod() );
	        reenableDate = cal.getTime();
		}
		else if (optOut.getPeriod() == OPTOUT_TODAY) {
			reenableDate = ServletUtil.getTomorrow(tz);
		}
        
        ArrayList hwIDList = getHardwareIDs( liteAcctInfo );
        for (int i = 0; i < hwIDList.size(); i++) {
        	Integer invID = (Integer) hwIDList.get(i);
        	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID.intValue(), true );
        
    		// Add "Temp Opt Out" and "Future Activation" to hardware events
			ECUtils.removeFutureActivationEvents( liteHw.getInventoryHistory(), energyCompany );
			com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
    		
    		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( invID );
    		eventBase.setEventTypeID( hwEventEntryID );
    		eventBase.setActionID( tempTermEntryID );
    		eventBase.setEventDateTime( optOutDate );
    		
    		event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
    		multiDB.getDBPersistentVector().addElement( event );
			
    		event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		eventDB = event.getLMHardwareEvent();
    		eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( invID );
    		eventBase.setEventTypeID( hwEventEntryID );
    		eventBase.setActionID( futureActEntryID );
    		eventBase.setEventDateTime( reenableDate );
    		
    		event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
    		multiDB.getDBPersistentVector().addElement( event );
    		
    		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
    				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
    		
			// Update lite objects and create response
			for (int j = 0; j < multiDB.getDBPersistentVector().size(); j++) {
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent) multiDB.getDBPersistentVector().get(j);
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getInventoryHistory().add( liteEvent );
			}
			liteHw.updateDeviceStatus();
			
			StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
			hwHist.setInventoryID( liteHw.getInventoryID() );
			for (int j = 0; j < liteHw.getInventoryHistory().size(); j++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getInventoryHistory().get(j);
				StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
				StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
				hwHist.addStarsLMHardwareEvent( starsEvent );
			}
			resp.addStarsLMHardwareHistory( hwHist );
        }
		
		for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
			Integer programID = new Integer(liteProg.getLmProgram().getProgramID());
			
	        // Add "Temp Opt Out" and "Future Activation" to program events
			com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
            
            com.cannontech.database.data.stars.event.LMProgramEvent event =
            		new com.cannontech.database.data.stars.event.LMProgramEvent();
            com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
            
            eventDB.setLMProgramID( programID );
            eventDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
            eventBase.setEventTypeID( progEventEntryID );
            eventBase.setActionID( tempTermEntryID );
            eventBase.setEventDateTime( optOutDate );
            
            event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
            multiDB.getDBPersistentVector().addElement( event );
            
            event = new com.cannontech.database.data.stars.event.LMProgramEvent();
            eventDB = event.getLMProgramEvent();
            eventBase = event.getLMCustomerEventBase();
            
            eventDB.setLMProgramID( programID );
            eventDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
            eventBase.setEventTypeID( progEventEntryID );
            eventBase.setActionID( futureActEntryID );
            eventBase.setEventDateTime( reenableDate );
            
            event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
            multiDB.getDBPersistentVector().addElement( event );
            
    		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
    				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
			
			// Update lite objects and create response
			for (int j = 0; j < multiDB.getDBPersistentVector().size(); j++) {
				event = (com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(j);
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteProg.getProgramHistory().add( liteEvent );
			}
			liteProg.updateProgramStatus();
			
			StarsLMProgramHistory progHist = new StarsLMProgramHistory();
			progHist.setProgramID( liteProg.getLmProgram().getProgramID() );
			for (int j = 0; j < liteProg.getProgramHistory().size(); j++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(j);
				StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
				StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
				progHist.addStarsLMProgramEvent( starsEvent );
			}
			resp.addStarsLMProgramHistory( progHist );
        }
        
        return resp;
	}
	
	public static void updateCustAccountInfo(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramOptOut optOut)
	{
		StarsProgramOptOutResponse resp = null;
		try {
			resp = processOptOut( liteAcctInfo, energyCompany, optOut );
		}
		catch (com.cannontech.database.TransactionException e) {
			e.printStackTrace();
			return;
		}
		
		StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
				energyCompany.getStarsCustAccountInformation( liteAcctInfo.getAccountID() );
		if (starsAcctInfo != null) {
			StarsInventories inventories = starsAcctInfo.getStarsInventories();
			StarsLMPrograms programs = starsAcctInfo.getStarsLMPrograms();
			
	        for (int i = 0; i < resp.getStarsLMProgramHistoryCount(); i++) {
	        	StarsLMProgramHistory progHist = resp.getStarsLMProgramHistory(i);
	        	
	        	for (int j = 0; j < programs.getStarsLMProgramCount(); j++) {
	        		StarsLMProgram program = programs.getStarsLMProgram(j);
	        		if (program.getProgramID() == progHist.getProgramID()) {
	        			program.setStarsLMProgramHistory( progHist );
	        			program.setStatus( ServletUtils.OUT_OF_SERVICE );
	        		}
	        	}
	        }
	        ServletUtils.removeProgramHistory( starsAcctInfo.getStarsCustomerAccount().getAccountID() );
	        
	        if (inventories != null) {
				DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL),
						DeviceStatus.class );
						
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
		}
	}

}
