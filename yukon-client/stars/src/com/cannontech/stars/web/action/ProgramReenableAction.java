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
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
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
            if (req.getParameter("OptOutDate") != null)
            	reEnable.setContent( "1" );	// Add any string here to indicate "OptOutDate" field is not empty

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
            
            StarsProgramReenable reEnable = reqOper.getStarsProgramReenable();
			String[] commands = getReenableCommands( liteAcctInfo, energyCompany );
        	for (int i = 0; i < commands.length; i++)
        		ServerUtils.sendCommand( commands[i] );
			StarsProgramReenableResponse resp = processReenable( liteAcctInfo, energyCompany, reEnable );
			
            if (reEnable.getContent() != null)
            	energyCompany.getOptOutEventQueue().removeEvent( liteAcctInfo.getCustomerAccount().getAccountID() );

            respOper.setStarsProgramReenableResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot reenable the programs") );
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
            			program.setStatus( ServletUtils.IN_SERVICE );
            		}
            	}
            }
        	ServletUtils.removeProgramHistory( accountInfo.getStarsCustomerAccount().getAccountID() );
            
            if (ServerUtils.isOperator( user )) {
				Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
				DeviceStatus hwStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntry(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL),
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
	
	public static String[] getReenableCommands(LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany)
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

            commands[i] = "putconfig xcom service in temp serial " + liteHw.getManufactureSerialNumber() + routeStr;
        }
        
        return commands;
	}
	
	private static StarsProgramReenableResponse processReenable(
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramReenable reEnable)
	{
		StarsProgramReenableResponse resp = new StarsProgramReenableResponse();
        
        Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer progEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM).getEntryID() );
        Integer futureActEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID() );
        Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );

        Date now = new Date();	// Current date, all customer events will use exactly the same date
        
        // List of hardware IDs to be reenabled
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
        	
    		// if device status is not temporarily unavailable, then do nothing
    		if (liteHw.getDeviceStatus() != YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL) continue;
			
			com.cannontech.database.data.multi.MultiDBPersistent multiDB = new com.cannontech.database.data.multi.MultiDBPersistent();
			
			ServerUtils.removeLMCustomEvents( liteHw.getLmHardwareHistory(), futureActEntryID.intValue() );
    		
    		// Add "Activation Completed" to hardware events
    		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( invID );
    		eventBase.setEventTypeID( hwEventEntryID );
    		eventBase.setActionID( actCompEntryID );
    		eventBase.setEventDateTime( now );
    		
    		event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
            multiDB.getDBPersistentVector().addElement( event );
    		
            // Add "Activation Completed" to program events
    		for (int j = 0; j < liteAcctInfo.getAppliances().size(); j++) {
            	LiteStarsAppliance appliance = (LiteStarsAppliance) liteAcctInfo.getAppliances().get(j);
                if (appliance.getInventoryID() != liteHw.getInventoryID()) continue;
                
	            if (appliance.getLmProgramID() == 0) continue;
            	Integer programID = new Integer( appliance.getLmProgramID() );
            	
            	LiteStarsLMProgram liteProg = null;
            	for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
                	LiteStarsLMProgram lProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
                	if (lProg.getLmProgram().getProgramID() == programID.intValue()) {
                		liteProg = lProg;
                		break;
                	}
            	}
            	
            	// If program is already in service, do nothing
        		if (liteProg == null || liteProg.isInService()) continue;
        		
        		ServerUtils.removeLMCustomEvents( liteProg.getProgramHistory(), futureActEntryID.intValue() );
	            
	            com.cannontech.database.data.stars.event.LMProgramEvent event1 =
	            		new com.cannontech.database.data.stars.event.LMProgramEvent();
	            com.cannontech.database.db.stars.event.LMProgramEvent eventDB1 = event1.getLMProgramEvent();
	            com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase1 = event1.getLMCustomerEventBase();
	            
	            eventDB1.setLMProgramID( programID );
	            eventDB1.setAccountID( new Integer(liteAcctInfo.getCustomerAccount().getAccountID()) );
	            eventBase1.setEventTypeID( progEventEntryID );
	            eventBase1.setActionID( actCompEntryID );
	            eventBase1.setEventDateTime( now );
	            
	            event1.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
	            multiDB.getDBPersistentVector().addElement( event1 );
            }
    		
    		try {
	    		multiDB = (com.cannontech.database.data.multi.MultiDBPersistent)
	    				Transaction.createTransaction( Transaction.INSERT, multiDB ).execute();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    			continue;
    		}
    				
			// Update lite objects and create response
			// The first event is a hardware event
			event = (com.cannontech.database.data.stars.event.LMHardwareEvent) multiDB.getDBPersistentVector().get(0);
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteHw.getLmHardwareHistory().add( liteEvent );
			liteHw.setDeviceStatus( YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL );
			
			StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
			hwHist.setInventoryID( liteHw.getInventoryID() );
			for (int k = 0; k < liteHw.getLmHardwareHistory().size(); k++) {
				liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(k);
				StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
				StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
				hwHist.addStarsLMHardwareEvent( starsEvent );
			}
			resp.addStarsLMHardwareHistory( hwHist );
			
			// The rest of the events are program events
			for (int eventNo = 1; eventNo < multiDB.getDBPersistentVector().size(); eventNo++) {
				com.cannontech.database.data.stars.event.LMProgramEvent event1 =
						(com.cannontech.database.data.stars.event.LMProgramEvent) multiDB.getDBPersistentVector().get(eventNo);
				liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event1 );
				
				for (int k = 0; k < liteAcctInfo.getLmPrograms().size(); k++) {
					LiteStarsLMProgram liteProg = (LiteStarsLMProgram) liteAcctInfo.getLmPrograms().get(k);
					if (liteProg.getLmProgram().getProgramID() != event1.getLMProgramEvent().getLMProgramID().intValue()) continue;
					liteProg.getProgramHistory().add( liteEvent );
					liteProg.setInService( true );
					
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
			LiteStarsCustAccountInformation liteAcctInfo, LiteStarsEnergyCompany energyCompany, StarsProgramReenable reEnable)
	{
		StarsProgramReenableResponse resp = processReenable( liteAcctInfo, energyCompany, reEnable );
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
