package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.stars.LiteLMThermostatManualEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ECUtils;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsThermostatManualEvent;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOption;
import com.cannontech.stars.xml.serialize.StarsUpdateThermostatManualOptionResponse;
import com.cannontech.stars.xml.serialize.types.StarsThermoFanSettings;
import com.cannontech.stars.xml.serialize.types.StarsThermoModeSettings;
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
public class UpdateThermostatManualOptionAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            if (accountInfo == null) return null;
            
            StarsUpdateThermostatManualOption option = new StarsUpdateThermostatManualOption();
            
			String[] invIDStrs = req.getParameterValues("InvIDs");
			if (invIDStrs == null || invIDStrs.length == 0) {
				option.setInventoryID( Integer.parseInt(req.getParameter("InvID")) );
			}
			else {
				StringBuffer invIDStr = new StringBuffer( invIDStrs[0] );
				for (int i = 1; i < invIDStrs.length; i++)
					invIDStr.append(",").append(invIDStrs[i]);
            	
				option.setInventoryIDs( invIDStr.toString() );
			}
			
            if (Boolean.valueOf( req.getParameter("RunProgram") ).booleanValue())
            	option.setTemperature( -1 );	// "Run Program" button is clicked
            else
	            option.setTemperature( Integer.parseInt(req.getParameter("tempField")) );
            option.setHold( Boolean.valueOf(req.getParameter("hold")).booleanValue() );
            if (req.getParameter("mode").length() > 0)
	            option.setMode( StarsThermoModeSettings.valueOf(req.getParameter("mode")) );
	        if (req.getParameter("fan").length() > 0)
	            option.setFan( StarsThermoFanSettings.valueOf(req.getParameter("fan")) );
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateThermostatManualOption( option );
            
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
    		
			boolean hasTwoWay = false;
			
			StarsUpdateThermostatManualOption starsOption = reqOper.getStarsUpdateThermostatManualOption();
			StarsUpdateThermostatManualOptionResponse resp = new StarsUpdateThermostatManualOptionResponse();
    		
			int[] invIDs = null;
			if (starsOption.getInventoryIDs() == null) {
				// Setup for a single thermostat
				invIDs = new int[] {starsOption.getInventoryID()};
			}
			else {
				// Setup for multiple thermostats
				String[] invIDStrs = starsOption.getInventoryIDs().split(",");
				invIDs = new int[ invIDStrs.length ];
				for (int i = 0; i < invIDs.length; i++)
					invIDs[i] = Integer.parseInt( invIDStrs[i] );
			}
			
			for (int i = 0; i < invIDs.length; i++) {
				LiteStarsLMHardware liteHw = (LiteStarsLMHardware) energyCompany.getInventory( invIDs[i], true );
				
				if (liteHw.getDeviceStatus() == YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_UNAVAIL) {
					String errorMsg = ServerUtils.isOperator(user) ?
							((invIDs.length == 1)?
								"The thermostat is currently out of service" :
								"The thermostat '" + liteHw.getManufacturerSerialNumber() + "' is currently out of service") :
							"Cannot send manual option to the thermostat. Please contact your utility company to report this problem.";
					
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, errorMsg) );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
    			
				if (liteHw.getManufacturerSerialNumber().trim().length() == 0) {
					String errorMsg = ServerUtils.isOperator(user) ?
							"The serial # of the thermostat cannot be empty." :
							"Cannot send manual option to the thermostat. Please contact your utility company to report this problem.";
					
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, errorMsg) );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				
				StringBuffer cmd = new StringBuffer();
				if (liteHw.isTwoWayThermostat())
					cmd.append("putconfig epro setstate");
				else
					cmd.append("putconfig xcom setstate");
				if (starsOption.getMode() != null)
					cmd.append(" system ").append(starsOption.getMode().toString().toLowerCase());
				if (starsOption.getTemperature() == -1) {
					// Run scheduled program
					cmd.append(" run");
				}
				else {
					cmd.append(" temp ").append(starsOption.getTemperature());
					if (starsOption.getFan() != null)
						cmd.append(" fan ").append(starsOption.getFan().toString().toLowerCase());
					if (starsOption.getHold())
						cmd.append(" hold");
				}
				cmd.append(" serial ").append(liteHw.getManufacturerSerialNumber());
				
				int routeID = liteHw.getRouteID();
				if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
				
				com.cannontech.yc.gui.YC yc = SOAPServer.getYC();
				synchronized (yc) {
					yc.setRouteID( routeID );
					yc.setCommand( cmd.toString() );
					yc.handleSerialNumber();
				}
				
				// Add to the thermostat manual events
				com.cannontech.database.data.stars.event.LMThermostatManualEvent event =
						new com.cannontech.database.data.stars.event.LMThermostatManualEvent();
				event.getLMCustomerEventBase().setEventTypeID( new Integer(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL).getEntryID()) );
				event.getLMCustomerEventBase().setActionID( new Integer(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_MANUAL_OPTION).getEntryID()) );
				event.getLMCustomerEventBase().setEventDateTime( new Date() );
				
				event.getLmThermostatManualEvent().setInventoryID( new Integer(invIDs[i]) );
				event.getLmThermostatManualEvent().setPreviousTemperature( new Integer(starsOption.getTemperature()) );
				event.getLmThermostatManualEvent().setHoldTemperature( starsOption.getHold() ? "Y" : "N" );
				event.getLmThermostatManualEvent().setOperationStateID( new Integer(ECUtils.getThermOptionOpStateID(starsOption.getMode(), energyCompany)) );
				event.getLmThermostatManualEvent().setFanOperationID( ECUtils.getThermOptionFanOpID(starsOption.getFan(), energyCompany) );
				
				event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
				event = (com.cannontech.database.data.stars.event.LMThermostatManualEvent)
						Transaction.createTransaction(Transaction.INSERT, event).execute();
				
				LiteLMThermostatManualEvent liteEvent = (LiteLMThermostatManualEvent) StarsLiteFactory.createLite( event );
				liteHw.getThermostatSettings().getThermostatManualEvents().add( liteEvent );
				
				// Log activity
				String tempUnit = "F";
				if (liteHw.getThermostatSettings().getDynamicData() != null &&
					liteHw.getThermostatSettings().getDynamicData().getDisplayedTempUnit() != null)
					tempUnit = liteHw.getThermostatSettings().getDynamicData().getDisplayedTempUnit();
				
				String logMsg = "Serial #:" + liteHw.getManufacturerSerialNumber();
				if (starsOption.getMode() != null)
					logMsg += ", Mode:" + starsOption.getMode().toString();
				if (starsOption.getTemperature() == -1) {
					logMsg += ", Run Program";
				}
				else {
					logMsg += ", Temp:" + starsOption.getTemperature() + tempUnit;
					if (starsOption.getHold())
						logMsg += "(HOLD)";
					if (starsOption.getFan() != null)
						logMsg += ", Fan:" + starsOption.getFan().toString();
				}
				
				ActivityLogger.logEvent(user.getUserID(), liteAcctInfo.getAccountID(), energyCompany.getLiteID(), liteAcctInfo.getCustomer().getCustomerID(),
						ActivityLogActions.THERMOSTAT_MANUAL_ACTION, logMsg);
				
				// The StarsThermostatManualEvent element of the response message only need to be set once
				if (resp.getStarsThermostatManualEvent() == null) {
					StarsThermostatManualEvent starsEvent = StarsLiteFactory.createStarsThermostatManualEvent( liteEvent );
					resp.setStarsThermostatManualEvent( starsEvent );
				}
				
				if (liteHw.isTwoWayThermostat()) hasTwoWay = true;
			}
            
            if (hasTwoWay) {
            	try {
					Thread.sleep(3 * 1000);	// Wait a while for the new settings to be reflected in the table
            	}
            	catch (InterruptedException e) {}
            	
				ECUtils.updateThermostatSettings( liteAcctInfo, energyCompany );
            }
			
			respOper.setStarsUpdateThermostatManualOptionResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update thermostat manual option") );
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

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsUpdateThermostatManualOptionResponse resp = operation.getStarsUpdateThermostatManualOptionResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
            		user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateThermostatManualOption option = reqOper.getStarsUpdateThermostatManualOption();
			
			int[] invIDs = null;
			if (option.getInventoryIDs() == null) {
				invIDs = new int[] {option.getInventoryID()};
			}
			else {
				String[] invIDStrs = option.getInventoryIDs().split(",");
				invIDs = new int[ invIDStrs.length ];
				for (int i = 0; i < invIDs.length; i++)
					invIDs[i] = Integer.parseInt( invIDStrs[i] );
			}
            
			StarsThermostatManualEvent event = resp.getStarsThermostatManualEvent();
            String confirmMsg = "Thermostat settings has been sent.";
            
            for (int i = 0; i < invIDs.length; i++) {
				for (int j = 0; j < accountInfo.getStarsInventories().getStarsInventoryCount(); j++) {
					StarsInventory inv = accountInfo.getStarsInventories().getStarsInventory(j);
					
					if (inv.getInventoryID() == invIDs[i]) {
						inv.getLMHardware().getStarsThermostatSettings().addStarsThermostatManualEvent( resp.getStarsThermostatManualEvent() );
						break;
					}
				}
            }
            
            session.setAttribute(ServletUtils.ATT_CONFIRM_MESSAGE,
					"The manual option has been sent. It may take a few minutes before the thermostat receives it.");
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
