package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

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
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.OptOutEventQueue;
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
import com.cannontech.stars.xml.serialize.StarsProgramReenable;
import com.cannontech.stars.xml.serialize.StarsProgramReenableResponse;
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
public class ProgramReenableAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;

            StarsProgramReenable reEnable = new StarsProgramReenable();
            StarsOperation operation = new StarsOperation();
            operation.setStarsProgramReenable( reEnable );
            
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
            
            StarsProgramReenable reenable = reqOper.getStarsProgramReenable();
			StarsProgramReenableResponse resp = new StarsProgramReenableResponse();
			
			String[] commands = getReenableCommands( liteAcctInfo, energyCompany );
			
			com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
			synchronized (yc) {
				yc.setRouteID( energyCompany.getDefaultRouteID() );
				for (int i = 0; i < commands.length; i++) {
					yc.setCommandFileName( commands[i] );
					yc.handleSerialNumber();
				}
			}
        	
        	// Send out reenable notification
        	SendOptOutNotificationAction.sendReenableNotification( energyCompany, liteAcctInfo, reqOper );
			
			String desc = ServletUtils.capitalize(energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE)) + " command has been sent out successfully";
			
			OptOutEventQueue queue = energyCompany.getOptOutEventQueue();
			OptOutEventQueue.OptOutEvent e1 = queue.findOptOutEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
			OptOutEventQueue.OptOutEvent e2 = queue.findReenableEvent( liteAcctInfo.getCustomerAccount().getAccountID() );
			queue.removeEvents( liteAcctInfo.getCustomerAccount().getAccountID() );
			
			if (e1 != null)
				desc += ", a scheduled " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN) + " event is canceled";
			//if (e2 != null)
				resp = processReenable( liteAcctInfo, energyCompany, reenable );
			resp.setDescription( desc );
			
            respOper.setStarsProgramReenableResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot " + energyCompany.getEnergyCompanySetting(ConsumerInfoRole.WEB_TEXT_REENABLE) + " the programs") );
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

			StarsProgramReenableResponse resp = operation.getStarsProgramReenableResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
            if (resp.getDescription() != null)
            	session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, resp.getDescription() );
            
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
	            			program.setStatus( ServletUtils.IN_SERVICE );
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
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL),
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
	 * Return the list of hardware IDs to be reenabled
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
	
	public static String[] getReenableCommands(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
	throws Exception {
        ArrayList hwIDList = getHardwareIDs( liteAcctInfo );
		String[] commands = new String[ hwIDList.size() ];

        for (int i = 0; i < hwIDList.size(); i++) {
        	Integer invID = (Integer) hwIDList.get(i);
        	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID.intValue(), true );
        	
    		if (liteHw.getManufactureSerialNumber().trim().length() == 0)
    			throw new Exception( "The manufacturer serial # of the hardware cannot be empty" );

            commands[i] = "putconfig service in temp serial " + liteHw.getManufactureSerialNumber();
        }
        
        return commands;
	}
	
	private static StarsProgramReenableResponse processReenable(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramReenable reEnable)
			throws com.cannontech.database.TransactionException
	{
		StarsProgramReenableResponse resp = new StarsProgramReenableResponse();
        
        Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer progEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
        Integer futureActEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID() );
        Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );

        Date now = new Date();	// Current date, all customer events will use exactly the same date
        
        ArrayList hwIDList = getHardwareIDs( liteAcctInfo );
        for (int i = 0; i < hwIDList.size(); i++) {
        	Integer invID = (Integer) hwIDList.get(i);
        	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID.intValue(), true );
			
    		// Add "Activation Completed" to hardware events
			ECUtils.removeFutureActivationEvents( liteHw.getInventoryHistory(), energyCompany );
    		
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
    		
			// Update lite objects and create response
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteHw.getInventoryHistory().add( liteEvent );
			liteHw.updateDeviceStatus();
			
			StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
			hwHist.setInventoryID( liteHw.getInventoryID() );
			for (int k = 0; k < liteHw.getInventoryHistory().size(); k++) {
				liteEvent = (LiteLMCustomerEvent) liteHw.getInventoryHistory().get(k);
				StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
				StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
				hwHist.addStarsLMHardwareEvent( starsEvent );
			}
			resp.addStarsLMHardwareHistory( hwHist );
        }
		
		for (int i = 0; i < liteAcctInfo.getLmPrograms().size(); i++) {
			LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(i);
        	
        	// If program is already in service, do nothing
    		if (liteProg.isInService()) continue;
    		
	        // Add "Activation Completed" to program events
			ECUtils.removeFutureActivationEvents( liteProg.getProgramHistory(), energyCompany );
            
            com.cannontech.database.data.stars.event.LMProgramEvent event =
            		new com.cannontech.database.data.stars.event.LMProgramEvent();
            com.cannontech.database.db.stars.event.LMProgramEvent eventDB = event.getLMProgramEvent();
            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
            
            eventDB.setLMProgramID( new Integer(liteProg.getLmProgram().getProgramID()) );
            eventDB.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
            eventBase.setEventTypeID( progEventEntryID );
            eventBase.setActionID( actCompEntryID );
            eventBase.setEventDateTime( now );
            
            event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
    		event = (com.cannontech.database.data.stars.event.LMProgramEvent)
    				Transaction.createTransaction( Transaction.INSERT, event ).execute();
    		
			// Update lite objects and create response
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteProg.getProgramHistory().add( liteEvent );
			liteProg.updateProgramStatus();
			
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
        
        return resp;
	}
	
	public static void updateCustAccountInfo(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramReenable reEnable)
	{
		StarsProgramReenableResponse resp = null;
		try {
			resp = processReenable( liteAcctInfo, energyCompany, reEnable );
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
            			program.setStatus( ServletUtils.IN_SERVICE );
            		}
            	}
            }
        	ServletUtils.removeProgramHistory( liteAcctInfo.getCustomerAccount().getAccountID() );
            
            if (inventories != null) {
				DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL),
						DeviceStatus.class );
            	
	            // Update hardware history
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
