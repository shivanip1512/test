package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.data.lite.stars.LiteLMCustomerEvent;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
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
import com.cannontech.stars.xml.serialize.StarsLMHardware;
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
            e.printStackTrace();
            session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
        }

        return null;
    }

    public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        StarsOperation respOper = new StarsOperation();
        java.sql.Connection conn = null;
        
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
                respOper.setStarsFailure( StarsFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
                
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            
            StarsYukonSwitchCommand command = reqOper.getStarsYukonSwitchCommand();
            StarsYukonSwitchCommandResponse cmdResp = new StarsYukonSwitchCommandResponse();
            
            if (command.getStarsDisableService() != null) {
            	int invID = command.getStarsDisableService().getInventoryID();
            	LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
            	sendDisableCommand(energyCompany, liteHw, conn);
				StarsLMHardware starsHw = StarsLiteFactory.createStarsLMHardware( liteHw, energyCompany );
            	cmdResp.setStarsLMHardware( starsHw );
            }
            else if (command.getStarsEnableService() != null) {
            	int invID = command.getStarsEnableService().getInventoryID();
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
            	sendEnableCommand(energyCompany, liteHw, conn);
				StarsLMHardware starsHw = StarsLiteFactory.createStarsLMHardware( liteHw, energyCompany );
            	cmdResp.setStarsLMHardware( starsHw );
            }
            else if (command.getStarsConfig() != null) {
                int invID = command.getStarsConfig().getInventoryID();
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invID, true );
                sendConfigCommand(energyCompany, liteHw, true, conn);
				StarsLMHardware starsHw = StarsLiteFactory.createStarsLMHardware( liteHw, energyCompany );
				cmdResp.setStarsLMHardware( starsHw );
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
        finally {
        	try {
        		if (conn != null) conn.close();
        	}
        	catch (java.sql.SQLException e) {}
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
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsLMHardware hardware = resp.getStarsLMHardware();
			
			StarsInventories inventories = accountInfo.getStarsInventories();
			for (int i = 0; i < inventories.getStarsLMHardwareCount(); i++) {
				StarsLMHardware hw = inventories.getStarsLMHardware(i);
				if (hw.getInventoryID() == hardware.getInventoryID()) {
					inventories.setStarsLMHardware(i, hardware);
					break;
				}
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
    }
    
	public static void sendDisableCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw, java.sql.Connection conn)
	throws java.sql.SQLException {
		if (liteHw.getManufactureSerialNumber().trim().length() == 0)
			throw new java.sql.SQLException( "The manufacturer serial # of the hardware cannot be empty" );
        
        Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer termEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_TERMINATION).getEntryID() );
        java.util.Date now = new java.util.Date();
		
        String cmd = "putconfig service out serial " + liteHw.getManufactureSerialNumber();
        
		com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		synchronized (yc) {
			yc.setRouteID( energyCompany.getDefaultRouteID() );
			yc.setCommand( cmd );
			yc.handleSerialNumber();
		}
		
		// Add "Termination" to hardware events
		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
		
		eventDB.setInventoryID( new Integer(liteHw.getInventoryID()) );
		eventBase.setEventTypeID( hwEventEntryID );
		eventBase.setActionID( termEntryID );
		eventBase.setEventDateTime( new Date() );
		
		event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		event.setDbConnection( conn );
		event.add();
		
		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
		liteHw.getInventoryHistory().add( liteEvent );
		liteHw.updateDeviceStatus();
	}
    
	public static void sendEnableCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw, java.sql.Connection conn)
	throws java.sql.SQLException {
		if (liteHw.getManufactureSerialNumber().trim().length() == 0)
			throw new java.sql.SQLException( "The manufacturer serial # of the hardware cannot be empty" );
        
        Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
        java.util.Date now = new java.util.Date();
		
        String cmd = "putconfig service in serial " + liteHw.getManufactureSerialNumber();
        
		com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		synchronized (yc) {
			yc.setRouteID( energyCompany.getDefaultRouteID() );
			yc.setCommand( cmd );
			yc.handleSerialNumber();
		}
		
		// Add "Activation Completed" to hardware events
		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
		
		eventDB.setInventoryID( new Integer(liteHw.getInventoryID()) );
		eventBase.setEventTypeID( hwEventEntryID );
		eventBase.setActionID( actCompEntryID );
		eventBase.setEventDateTime( new Date() );
		
		event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
		event.setDbConnection( conn );
		event.add();
		
		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
		liteHw.getInventoryHistory().add( liteEvent );
		liteHw.updateDeviceStatus();
	}
	
	public static void sendConfigCommand(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw, boolean forceInService, java.sql.Connection conn)
	throws java.sql.SQLException {
		if (liteHw.getManufactureSerialNumber().trim().length() == 0)
			throw new java.sql.SQLException( "The manufacturer serial # of the hardware cannot be empty" );
        
        Integer invID = new Integer( liteHw.getInventoryID() );
        Integer hwEventEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMHARDWARE).getEntryID() );
        Integer actCompEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_COMPLETED).getEntryID() );
        Integer configEntryID = new Integer( energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_CONFIG).getEntryID() );
        java.util.Date now = new java.util.Date();
        
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] configs =
				com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getALLHardwareConfigs( invID );
		
        if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL || forceInService) {
        	if (configs.length > 0) {
        		// Send an in service command followed by a config command
				new Thread( new SendConfigCommandTask(energyCompany, liteHw) ).start();
        	}
			else {
				// Only send an in service command
		        String cmd = "putconfig service in serial " + liteHw.getManufactureSerialNumber();
		        
		        com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
		        synchronized (yc) {
		        	yc.setRouteID( energyCompany.getDefaultRouteID() );
		        	yc.setCommand( cmd );
		        	yc.handleSerialNumber();
		        }
			}
			
			// Add "Activation Completed" to hardware events
			com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
			com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
			
			eventDB.setInventoryID( invID );
			eventBase.setEventTypeID( hwEventEntryID );
			eventBase.setActionID( actCompEntryID );
			eventBase.setEventDateTime( now );
			
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			event.setDbConnection( conn );
			event.add();
			
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteHw.getInventoryHistory().add( liteEvent );
			liteHw.updateDeviceStatus();
        }
        else {
        	// Only send a config command
			for (int i = 0; i < configs.length; i++) {
				if (configs[i].getAddressingGroupID().intValue() == 0) continue;
				
				String groupName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( configs[i].getAddressingGroupID().intValue() );
	            String cmd = "putconfig serial " + liteHw.getManufactureSerialNumber() + " template '" + groupName + "'";
	            
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( energyCompany.getDefaultRouteID() );
					yc.setCommand( cmd );
					yc.handleSerialNumber();
				}
			}
        }
		
		if (configs.length > 0) {
			// Add "Config" to hardware events
			com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
			com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
			com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
			
			eventDB.setInventoryID( invID );
			eventBase.setEventTypeID( hwEventEntryID );
			eventBase.setActionID( configEntryID );
			eventBase.setEventDateTime( now );
			
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			event.setDbConnection( conn );
			event.add();
			
			LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
			liteHw.getInventoryHistory().add( liteEvent );
		}
	}
}