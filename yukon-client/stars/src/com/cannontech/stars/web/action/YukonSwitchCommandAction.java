package com.cannontech.stars.web.action;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.DeviceStatus;
import com.cannontech.stars.xml.serialize.StarsConfig;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDisableService;
import com.cannontech.stars.xml.serialize.StarsEnableService;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsLMHardware;
import com.cannontech.stars.xml.serialize.StarsLMHardwareEvent;
import com.cannontech.stars.xml.serialize.StarsLMHardwareHistory;
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
            else if (action.equalsIgnoreCase("Config")) {
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
            e.printStackTrace();
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
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
                
			int energyCompanyID = user.getEnergyCompanyID();
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            StarsYukonSwitchCommand command = reqOper.getStarsYukonSwitchCommand();
            StarsYukonSwitchCommandResponse cmdResp = new StarsYukonSwitchCommandResponse();
            
            // Get list entry IDs
            Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
            Integer tempTermEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TEMP_TERMINATION).getEntryID() );
            Integer futureActEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION).getEntryID() );
            Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
            Integer configEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID() );
            		
			String routeStr = (energyCompany == null) ? "" : " select route id " + String.valueOf(energyCompany.getDefaultRouteID());
            
            if (command.getStarsDisableService() != null) {
                StarsDisableService service = command.getStarsDisableService();
                Integer invID = new Integer( service.getInventoryID() );
        		LiteLMHardwareBase liteHw = energyCompany.getLMHardware( invID.intValue(), true );
        		
        		if (liteHw == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the hardware to be disabled") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}
        		if (liteHw.getManufactureSerialNumber().trim().length() == 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The manufacturer serial # of the hardware cannot be empty") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}

                String cmd = "putconfig service out serial " + liteHw.getManufactureSerialNumber() + routeStr;
                ServerUtils.sendCommand( cmd );
        		
        		// Add "Temp Opt Out" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( tempTermEntryID );
        		eventBase.setEventDateTime( new Date() );
        		
        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
        		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
        				Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				// Update lite object and create response
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
				cmdResp.setStarsLMHardwareHistory( hwHist );
            }
            else if (command.getStarsEnableService() != null) {
                StarsEnableService service = command.getStarsEnableService();
                Integer invID = new Integer( service.getInventoryID() );
        		LiteLMHardwareBase liteHw = energyCompany.getLMHardware( invID.intValue(), true );
        		
        		if (liteHw == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the hardware to be enabled") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}
        		if (liteHw.getManufactureSerialNumber().trim().length() == 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The manufacturer serial # of the hardware cannot be empty") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}

                String cmd = "putconfig service in serial " + liteHw.getManufactureSerialNumber() + routeStr;
                ServerUtils.sendCommand( cmd );
        		
        		// Add "Activation Completed" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( actCompEntryID );
        		eventBase.setEventDateTime( new Date() );
        		
        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
        		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
        				Transaction.createTransaction( Transaction.INSERT, event ).execute();
				
				// Update lite object and create response
				LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
				liteHw.getLmHardwareHistory().add( liteEvent );
				liteHw.setDeviceStatus( YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL );
				
				StarsLMHardwareHistory hwHist = new StarsLMHardwareHistory();
				hwHist.setInventoryID( liteHw.getInventoryID() );
				for (int k = 0; k < liteHw.getLmHardwareHistory().size(); k++) {
					liteEvent = (LiteLMCustomerEvent) liteHw.getLmHardwareHistory().get(k);
					StarsLMHardwareEvent starsEvent = new StarsLMHardwareEvent();
					StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
					hwHist.addStarsLMHardwareEvent( starsEvent );
				}
				cmdResp.setStarsLMHardwareHistory( hwHist );
            }
            else if (command.getStarsConfig() != null) {
                StarsConfig service = command.getStarsConfig();
                Integer invID = new Integer( service.getInventoryID() );
        		LiteLMHardwareBase liteHw = energyCompany.getLMHardware( invID.intValue(), true );
        		
        		if (liteHw == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the hardware to be enabled") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}
        		if (liteHw.getManufactureSerialNumber().trim().length() == 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "The manufacturer serial # of the hardware cannot be empty") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}
        		
        		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] configs =
        				com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getALLHardwareConfigs( invID );
        		if (configs == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find the hardware configuration information") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}
        		
        		ArrayList cmdList = new ArrayList();
        		for (int i = 0; i < configs.length; i++) {
        			if (configs[i].getAddressingGroupID().intValue() == 0) continue;
        			
        			String groupName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( configs[i].getAddressingGroupID().intValue() );
	                String cmd = "putconfig serial " + liteHw.getManufactureSerialNumber() + " template '" + groupName + "'" + routeStr;
	                cmdList.add( cmd );
        		}
        		String[] commands = new String[ cmdList.size() ];
        		cmdList.toArray( commands );
                sendSwitchCommand( user, commands );
	            
	            // Add "Config" to hardware events
        		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
        		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
        		
        		eventDB.setInventoryID( invID );
        		eventBase.setEventTypeID( hwEventEntryID );
        		eventBase.setActionID( configEntryID );
        		eventBase.setEventDateTime( new Date() );
        		
        		event.setEnergyCompanyID( new Integer(energyCompanyID) );
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
					StarsLiteFactory.setStarsLMCustomerEvent( starsEvent, liteEvent );
					hwHist.addStarsLMHardwareEvent( starsEvent );
				}
				cmdResp.setStarsLMHardwareHistory( hwHist );
            }

            respOper.setStarsYukonSwitchCommandResponse( cmdResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot complete the switch command") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			DeviceStatus availStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntry(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL),
						DeviceStatus.class );
			DeviceStatus unavailStatus = (DeviceStatus) StarsFactory.newStarsCustListEntry(
						ServletUtils.getStarsCustListEntry(
							selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_TEMP_UNAVAIL),
						DeviceStatus.class );
			
            // Update hardware history
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsLMHardwareHistory hwHist = resp.getStarsLMHardwareHistory();
			
			StarsInventories inventories = accountInfo.getStarsInventories();
			for (int j = 0; j < inventories.getStarsLMHardwareCount(); j++) {
				StarsLMHardware hw = inventories.getStarsLMHardware(j);
				if (hw.getInventoryID() == hwHist.getInventoryID()) {
					if (reqOper.getStarsYukonSwitchCommand().getStarsEnableService() != null) {
						hw.setDeviceStatus( availStatus );
					}
					else if (reqOper.getStarsYukonSwitchCommand().getStarsDisableService() != null) {
						hw.setDeviceStatus( unavailStatus );
					}
					else if (reqOper.getStarsYukonSwitchCommand().getStarsConfig() != null) {
						/* If hardware history is empty, set status to available, otherwise unchanged */
						if (hw.getStarsLMHardwareHistory() == null || hw.getStarsLMHardwareHistory().getStarsLMHardwareEventCount() == 0)
							hw.setDeviceStatus( availStatus );
					}
					
					hw.setStarsLMHardwareHistory( hwHist );
				}
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
    
    private void sendSwitchCommand(StarsYukonUser user, String command) throws IOException {
    	sendSwitchCommand( user, new String[] { command } );
    }
    
    private void sendSwitchCommand(StarsYukonUser user, String[] commands) throws IOException {
    	if (true) {
    		for (int i = 0; i < commands.length; i++)
    			ServerUtils.sendCommand( commands[i] );
    	}
    	else {
    		String batchCmdFile = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() )
    				.getEnergyCompanySetting( EnergyCompanyRole.SWITCH_COMMAND_FILE );
    		if (batchCmdFile != null) {
				File f = new File( batchCmdFile );
				if (!f.exists()) {
					File dir = new File( f.getParent() );
					if (!dir.exists()) dir.mkdirs();
					f.createNewFile();
				}
	    		PrintWriter fw = new PrintWriter( new FileWriter(f), true );
    			for (int i = 0; i < commands.length; i++)
    				fw.println( commands[i] );
    			fw.close();
    		}
    	}
    }
}