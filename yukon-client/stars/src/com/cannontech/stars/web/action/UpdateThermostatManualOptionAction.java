package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMHardwareBase;
import com.cannontech.database.data.lite.stars.LiteLMThermostatManualEvent;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
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
            option.setTemperature( Integer.parseInt(req.getParameter("tempField")) );
            option.setHold( Boolean.valueOf(req.getParameter("hold")).booleanValue() );
            if (req.getParameter("mode").length() > 0)
	            option.setMode( StarsThermoModeSettings.valueOf(req.getParameter("mode")) );
	        if (req.getParameter("fan").length() > 0)
	            option.setFan( StarsThermoFanSettings.valueOf(req.getParameter("fan")) );
            option.setInventoryID( accountInfo.getStarsThermostatSettings().getInventoryID() );
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateThermostatManualOption( option );
            
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
			
			StarsUpdateThermostatManualOption starsOption = reqOper.getStarsUpdateThermostatManualOption();
			
			LiteLMHardwareBase liteHw = energyCompany.getLMHardware( starsOption.getInventoryID(), true );
			String routeStr = (energyCompany == null) ? "" : " select route id " + String.valueOf(energyCompany.getRouteID()) + " load 1";
			
			//StringBuffer cmd = new StringBuffer("putconfig xcom setstate")
			StringBuffer cmd = new StringBuffer("control xcom setstate")
					.append(" temp ").append(starsOption.getTemperature());
			if (starsOption.getMode() != null)
				cmd.append(" system ").append(starsOption.getMode().toString().toLowerCase());
			if (starsOption.getFan() != null)
				cmd.append(" fan ").append(starsOption.getFan().toString().toLowerCase());
			if (starsOption.getHold())
				cmd.append(" hold");
			cmd.append(" serial ").append(liteHw.getManufactureSerialNumber()).append(routeStr);
			ServerUtils.sendCommand( cmd.toString() );

			com.cannontech.database.data.stars.event.LMThermostatManualEvent event =
					new com.cannontech.database.data.stars.event.LMThermostatManualEvent();
			event.getLMCustomerEventBase().setEventTypeID( new Integer(
					energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMTHERMOSTAT_MANUAL).getEntryID()) );
			event.getLMCustomerEventBase().setActionID( new Integer(
					energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_MANUAL_OPTION).getEntryID()) );
			event.getLMCustomerEventBase().setEventDateTime( new Date() );
			
			event.getLmThermostatManualEvent().setInventoryID( new Integer(liteHw.getInventoryID()) );
			event.getLmThermostatManualEvent().setPreviousTemperature( new Integer(starsOption.getTemperature()) );
			event.getLmThermostatManualEvent().setHoldTemperature( starsOption.getHold() ? "Y" : "N" );
			event.getLmThermostatManualEvent().setOperationStateID( ServerUtils.getThermOptionOpStateID(starsOption.getMode(), energyCompanyID) );
			event.getLmThermostatManualEvent().setFanOperationID( ServerUtils.getThermOptionFanOpID(starsOption.getFan(), energyCompanyID) );
			
			event.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
			event = (com.cannontech.database.data.stars.event.LMThermostatManualEvent)
					Transaction.createTransaction(Transaction.INSERT, event).execute();
			
			LiteLMThermostatManualEvent liteEvent = (LiteLMThermostatManualEvent) StarsLiteFactory.createLite( event );
			liteAcctInfo.getThermostatSettings().getThermostatManualEvents().add( liteEvent );
			
			StarsThermostatManualEvent starsEvent = StarsLiteFactory.createStarsThermostatManualEvent( liteEvent );
			StarsUpdateThermostatManualOptionResponse resp = new StarsUpdateThermostatManualOptionResponse();
			resp.setStarsThermostatManualEvent( starsEvent );
			
			respOper.setStarsUpdateThermostatManualOptionResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update thermostat manual option") );
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
			
			StarsUpdateThermostatManualOptionResponse resp = operation.getStarsUpdateThermostatManualOptionResponse();
            if (resp == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;

			StarsThermostatManualEvent event = resp.getStarsThermostatManualEvent();
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
            		user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            accountInfo.getStarsThermostatSettings().addStarsThermostatManualEvent( event );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
