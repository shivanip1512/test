package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

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
import com.cannontech.stars.util.OptOutEventQueue;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
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

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
            
            StarsProgramOptOut optOut = new StarsProgramOptOut();
            
            if (req.getParameter("OptOutDate") != null) {
            	int startDateID = Integer.parseInt( req.getParameter("OptOutDate") );
            	if (startDateID == ServletUtils.getStarsCustListEntry(
            			selectionLists,
            			YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
            			YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TOMORROW
            			).getEntryID() )
            		optOut.setStartDateTime( com.cannontech.util.ServletUtil.getTomorrow() );
            	else if (startDateID == ServletUtils.getStarsCustListEntry(
            			selectionLists,
            			YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
            			YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_TODAY
            			).getEntryID() )
            		optOut.setPeriod( OPTOUT_TODAY );
            	else if (startDateID == ServletUtils.getStarsCustListEntry(
            			selectionLists,
            			YukonSelectionListDefs.YUK_LIST_NAME_OPT_OUT_PERIOD,
            			YukonListEntryTypes.YUK_DEF_ID_OPTOUT_PERIOD_REPEAT_LAST
            			).getEntryID() )
            		optOut.setPeriod( REPEAT_LAST );
            }
            
            if (optOut.getPeriod() != OPTOUT_TODAY && optOut.getPeriod() != REPEAT_LAST) {
	            int period = 1;
	            if (req.getParameter("OptOutPeriod") != null) {
		            String periodStr = req.getParameter("OptOutPeriod");
		            if (periodStr != null)
		                try {
		                    period = Integer.parseInt(periodStr);
		                }
		                catch (NumberFormatException e) {}
		            if (period == 0) return null;
	            }
	            optOut.setPeriod( period );
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
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
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
            	
            	resp.setDescription( "Opt out command has been sent out successfully" );
            }
            else if (optOut.getPeriod() == OPTOUT_TODAY) {
            	int offHours = (int)((com.cannontech.util.ServletUtil.getTomorrow().getTime() - new Date().getTime()) * 0.001 / 3600 + 0.5);
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, offHours );
            	for (int i = 0; i < commands.length; i++)
            		ServerUtils.sendCommand( commands[i] );
            	
            	OptOutEventQueue.OptOutEvent event = new OptOutEventQueue.OptOutEvent();
            	event.setStartDateTime( com.cannontech.util.ServletUtil.getTomorrow().getTime() );
            	event.setPeriod( -1 );	// Reenable event
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
            }
            else if (optOut.getPeriod() == REPEAT_LAST) {
            	// Repeat the last opt out command
            	repeatLast( liteAcctInfo, energyCompany );
            	resp.setDescription( "The last opt out command has been resent" );
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
            	event.setPeriod( -1 );	// Reenable event
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
            }

        	respOper.setStarsProgramOptOutResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
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
			
			if (resp.getDescription() != null) {
				session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
				return 0;
			}
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
            // Update program history
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
            
            if (ServerUtils.isOperator( user )) {
				Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
				DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntry(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL),
						DeviceStatus.class );
            	
	            // Update hardware history
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

            commands[i] = "putconfig xcom serial " + liteHw.getManufactureSerialNumber() +
            			" service out temp offhours " + String.valueOf(offHours) + routeStr;
        }
        
        return commands;
	}
	
	private void repeatLast(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany) throws Exception {
		/* If we find an opt out event under the same account in memory,
		 * we don't need to resend the command (since it hasn't been sent out yet).
		 */
		OptOutEventQueue.OptOutEvent event = energyCompany.getOptOutEventQueue().findEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
		if (event != null) return;
		
		/* Check to see if the last program event is opt out,
		 * and it hasn't expired yet (the last entry in program event table is future activation)
		 */
		boolean isLastEventOptOut = true;
		LiteLMProgramEvent lastEvent = null;
		int futureActEntryID = energyCompany.getYukonListEntry( YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION ).getEntryID();
		
		for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
			ArrayList progHist = liteProg.getProgramHistory();
			if (progHist.size() == 0) {
				isLastEventOptOut = false;
				break;
			}
			
			LiteLMProgramEvent liteEvent = (LiteLMProgramEvent) progHist.get( progHist.size() - 1 );
			if (liteEvent.getActionID() != futureActEntryID) {
				isLastEventOptOut = false;
				break;
			}
			lastEvent = liteEvent;
		}
		
		if (isLastEventOptOut) {
			int offHours = (int) ((lastEvent.getEventDateTime() - new Date().getTime()) * 0.001 / 3600);
			if (offHours > 0) {
				String[] commands = getOptOutCommands( liteAcctInfo, energyCompany, offHours );
            	for (int i = 0; i < commands.length; i++)
            		ServerUtils.sendCommand( commands[i] );
			}
		}
	}
	
	private static StarsProgramOptOutResponse processOptOut(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramOptOut optOut)
	{
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
		if (optOut.getPeriod() == OPTOUT_TODAY) {
			reenableDate = com.cannontech.util.ServletUtil.getTomorrow();
		}
		else {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime( optOutDate );
	        cal.add( Calendar.DATE, optOut.getPeriod() );
	        reenableDate = cal.getTime();
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
        
    		ServerUtils.removeLMCustomEvents( liteHw.getLmHardwareHistory(), futureActEntryID.intValue() );
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
            	
            	for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
                	LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
                	if (liteProg.getLmProgram().getProgramID() == programID.intValue()) {
                		ServerUtils.replaceLMCustomEvents( liteProg.getProgramHistory(), futureActEntryID.intValue(), actCompEntryID.intValue() );
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
				
				for (int j = 0; j < liteAcctInfo.getLmPrograms().size(); j++) {
					LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(j);
					if (liteProg.getLmProgram().getProgramID() != event1.getLMProgramEvent().getLMProgramID().intValue()) continue;
					
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
