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
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteLMProgramEvent;
import com.cannontech.database.data.lite.stars.LiteStarsAppliance;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
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
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
            	StarsCustListEntry entryTomorrow = ServletUtils.getStarsCustListEntry(
            			selectionLists,
            			YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
            			YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TOMORROW );
            	StarsCustListEntry entryToday = ServletUtils.getStarsCustListEntry(
            			selectionLists,
            			YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
            			YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TODAY );
            	StarsCustListEntry entryRepeatLast = ServletUtils.getStarsCustListEntry(
            			selectionLists,
            			YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
            			YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_REPEAT_LAST );
            			
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
            	
            	resp.setDescription( "The " + energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_NOUN) + " event has been scheduled" );
            }
            else if (optOut.getPeriod() == OPTOUT_TODAY) {
            	int offHours = (int)((ServletUtil.getTomorrow(tz).getTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, offHours );
            	for (int i = 0; i < commands.length; i++)
            		ServerUtils.sendCommand( commands[i] );
            	
            	OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
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
			        resp.setDescription( ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_NOUN)) + " command has been sent out successfully" );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_PAST) );
            }
            else if (optOut.getPeriod() == REPEAT_LAST) {
            	// Repeat the last opt out command
            	if (repeatLast( liteAcctInfo, energyCompany ))
	            	resp.setDescription( "The last " + energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_NOUN) + " command has been resent" );
	            else
	            	resp.setDescription( ERROR_MSG_LABEL + "There is no active " + energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_NOUN) + " event, command not sent" );
            }
            else {
            	// Send opt out commands immediately, and store the reenable command in memory
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, optOut.getPeriod() * 24 );
            	for (int i = 0; i < commands.length; i++)
            		ServerUtils.sendCommand( commands[i] );
            	
            	OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
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
			        resp.setDescription( ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_NOUN)) + " command has been sent out successfully" );
			    else
			        resp.setDescription( "Your programs have been " + energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_PAST) );
            }

        	respOper.setStarsProgramOptOutResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot " + energyCompany.getEnergyCompanySetting(EnergyCompanyRole.OPT_OUT_VERB) + " the programs") );
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
	
	private String[] getOptOutCommands(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, int offHours)
	throws Exception {
        // List of hardware IDs to be disabled
        ArrayList hwIDList = new ArrayList();
        for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
        	LiteStarsLMProgram program = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
        	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
        		LiteStarsAppliance appliance = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
        		if (appliance.getLmProgramID() == program.getLmProgram().getProgramID()) {
        			Integer hardwareID = new Integer( appliance.getInventoryID() );
        			if (!hwIDList.contains( hardwareID )) hwIDList.add( hardwareID );
        			break;
        		}
        	}
        }
		
		String routeStr = (energyCompany == null) ? "" : " select route id " + String.valueOf(energyCompany.getDefaultRouteID());
		String[] commands = new String[ hwIDList.size() ];

        for (int i = 0; i < hwIDList.size(); i++) {
        	Integer invID = (Integer) hwIDList.get(i);
        	LiteLMHardwareBase liteHw = energyCompany.getLMHardware( invID.intValue(), true );
        	
    		if (liteHw.getManufactureSerialNumber().trim().length() == 0)
    			throw new Exception( "The manufacturer serial # of the hardware cannot be empty" );

            commands[i] = "putconfig serial " + liteHw.getManufactureSerialNumber() +
            			" service out temp offhours " + String.valueOf(offHours) + routeStr;
        }
        
        return commands;
	}
	
	boolean repeatLast(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany) throws Exception {
		/* We will only resend the command if there is no opt out event in the event queue
		 * (otherwise the command hasn't been sent out yet), and there is a reenable event
		 * (the opt out event is still active)
		 */
		OptOutEventQueue queue = energyCompany.getOptOutEventQueue();
		OptOutEventQueue.OptOutEvent e1 = queue.findOptOutEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
		OptOutEventQueue.OptOutEvent e2 = queue.findReenableEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
		if (e1 != null || e2 == null) return false;
		
		int offHours = (int) ((e2.getStartDateTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
		String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, offHours );
    	for (int i = 0; i < commands.length; i++)
    		ServerUtils.sendCommand( commands[i] );
    		
    	return true;
	}
	
	private static StarsProgramOptOutResponse processOptOut(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramOptOut optOut)
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
        
        // List of hardware IDs to be disabled
        ArrayList hwIDList = new ArrayList();
        for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
        	LiteStarsLMProgram program = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
        	for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
        		LiteStarsAppliance appliance = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
        		if (appliance.getLmProgramID() == program.getLmProgram().getProgramID()) {
        			Integer hardwareID = new Integer( appliance.getInventoryID() );
        			if (!hwIDList.contains( hardwareID )) hwIDList.add( hardwareID );
        			break;
        		}
        	}
        }

        for (int i = 0; i < hwIDList.size(); i++) {
        	Integer invID = (Integer) hwIDList.get(i);
        	LiteLMHardwareBase liteHw = energyCompany.getLMHardware( invID.intValue(), true );
        
    		ServerUtils.removeFutureActivationEvents( liteHw.getLmHardwareHistory(), energyCompany );
    		com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
    		
    		// Add "Temp Opt Out" and "Future Activation" to hardware events
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
    		
            // Add "Temp Opt Out" and "Future Activation" to program events
    		for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            	LiteStarsAppliance appliance = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
                if (appliance.getInventoryID() != liteHw.getInventoryID()) continue;
                
	            if (appliance.getLmProgramID() == 0) continue;
            	Integer programID = new Integer( appliance.getLmProgramID() );
            	
            	LiteStarsLMProgram liteProg = ServerUtils.getLMProgram( liteAcctInfo, programID.intValue() );
            	if (liteProg != null)
            		ServerUtils.removeFutureActivationEvents( liteProg.getProgramHistory(), energyCompany );
	            
	            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
	            		new com.cannontech.database.data.stars.event.LMProgramEvent();
	            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
	            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
	            
	            eventDB1.setLMProgramID( programID );
	            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
	            eventBase1.setEventTypeID( progEventEntryID );
	            eventBase1.setActionID( tempTermEntryID );
	            eventBase1.setEventDateTime( optOutDate );
	            
	            event1.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
	            multiDB.getDBPersistentVector().addElement( event1 );
	            
	            event1 = new com.cannontech.database.data.stars.event.LMProgramEvent();
	            eventDB1 = event1.getLMProgramEvent();
	            eventBase1 = event1.getLMCustomerEventBase();
	            
	            eventDB1.setLMProgramID( programID );
	            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
	            eventBase1.setEventTypeID( progEventEntryID );
	            eventBase1.setActionID( futureActEntryID );
	            eventBase1.setEventDateTime( reenableDate );
	            
	            event1.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
	            multiDB.getDBPersistentVector().addElement( event1 );
            }
    		
    		try {
	    		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
	    				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
    		}
    		catch (com.cannontech.database.TransactionException e) {
    			e.printStackTrace();
    			continue;
    		}
			
			// Update lite objects and create response
			// The first two events are hardware events
			int eventNo = 0;
			for (eventNo = 0; eventNo < 2; eventNo++) {
				event = (com.cannontech.database.data.stars.event.LMHardwareEvent) multiDB.getDBPersistentVector().get(eventNo);
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getLmHardwareHistory().add( liteEvent );
				liteHw.setDeviceStatus( YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL );
			}
			
			StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
			hwHist.setInventoryID( liteHw.getInventoryID() );
			for (int j = 0; j < liteHw.getLmHardwareHistory().size(); j++) {
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(j);
				StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
				StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
				hwHist.addStarsLMHardwareEvent( starsEvent );
			}
			resp.addStarsLMHardwareHistory( hwHist );
			
			// The rest of the events are program events in pairs
			for (; eventNo < multiDB.getDBPersistentVector().size(); eventNo += 2) {
				com.cannontech.database.data.stars.event.LMProgramEvent event1 =
						(com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo);
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
				
				LiteStarsLMProgram liteProg = ServerUtils.getLMProgram( liteAcctInfo, event1.getLMProgramEvent().getLMProgramID().intValue() );
				if (liteProg != null) {
					liteProg.getProgramHistory().add( liteEvent );
					event1 = (com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo+1);
					liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
					liteProg.getProgramHistory().add( liteEvent );
					liteProg.setInService( false );
					
					StarsLMProgramHistory progHist = new StarsLMProgramHistory();
					progHist.setProgramID( liteProg.getLmProgram().getProgramID() );
					for (int l = 0; l < liteProg.getProgramHistory().size(); l++) {
						liteEvent = (LiteLMCustomerEvent) liteProg.getProgramHistory().get(l);
						StarsLMProgramEvent starsEvent = new StarsLMProgramEvent();
						StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
						progHist.addStarsLMProgramEvent( starsEvent );
					}
					resp.addStarsLMProgramHistory( progHist );
				}
			}
        }
        
        return resp;
	}
	
	public static void updateCustAccountInfo(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramOptOut optOut)
	{
		StarsProgramOptOutResponse resp = processOptOut( liteAcctInfo, energyCompany, optOut );
		if (resp == null) return;
		
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
