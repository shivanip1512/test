package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.*;
import com.cannontech.database.db.stars.hardware.*;
import com.cannontech.message.porter.ClientConnection;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsCustListEntryFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;
import com.cannontech.stars.xml.util.*;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateThermostatOptionAction implements ActionBase {
	
	private static final String TIMEOUT_PERIOD_IN_MINUTE = "30";

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
        try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) return null;
            
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            if (accountInfo == null) return null;
            
            StarsThermostatManualOption option = new StarsThermostatManualOption();
            option.setTemperature( Integer.parseInt(req.getParameter("tempField")) );
            option.setHold( Boolean.valueOf(req.getParameter("hold")).booleanValue() );
            option.setMode( StarsThermoModeSettings.valueOf(req.getParameter("mode")) );
            option.setFan( StarsThermoFanSettings.valueOf(req.getParameter("fan")) );
            
            StarsUpdateThermostatSettings updateSettings = new StarsUpdateThermostatSettings();
            updateSettings.setStarsThermostatManualOption( option );
            updateSettings.setInventoryID( accountInfo.getStarsThermostatSettings().getInventoryID() );
            
            StarsOperation operation = new StarsOperation();
            operation.setStarsUpdateThermostatSettings( updateSettings );
            
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
                respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
                		StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
                return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (liteAcctInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
                
			int energyCompanyID = user.getEnergyCompanyID();
			LiteStarsEnergyCompany company = SOAPServer.getEnergyCompany( energyCompanyID );
			Hashtable selectionLists = (Hashtable) company.getAllSelectionLists();
			
			StarsUpdateThermostatSettings starsSettings = reqOper.getStarsUpdateThermostatSettings();
			StarsThermostatManualOption starsOption = starsSettings.getStarsThermostatManualOption();
			
			LiteLMHardwareBase liteHw = company.getLMHardware( starsSettings.getInventoryID(), true );
			String routeStr = (company == null) ? "" : " select route id " + String.valueOf(company.getRouteID()) + " load 1";
			
			StringBuffer cmd = new StringBuffer("control xcom setstate")
					.append(" system ").append(starsOption.getMode().toString().toLowerCase())
					.append(" fan ").append(starsOption.getFan().toString().toLowerCase())
					.append(" temp ").append(starsOption.getTemperature());
			if (!starsOption.getHold())
				cmd.append(" timeout ").append(TIMEOUT_PERIOD_IN_MINUTE);
			cmd.append(" serial ").append(liteHw.getManufactureSerialNumber()).append(routeStr);
			ServerUtils.sendCommand( cmd.toString() );
			
			LiteStarsThermostatSettings liteSettings = liteAcctInfo.getThermostatSettings();
			LiteLMThermostatManualOption liteOption = liteSettings.getThermostatOption();
			
			if (liteOption == null) liteOption = new LiteLMThermostatManualOption();
			liteOption.setInventoryID( starsSettings.getInventoryID() );
			liteOption.setPreviousTemperature( starsOption.getTemperature() );
			liteOption.setHoldTemperature( starsOption.getHold() );
			liteOption.setOperationStateID( ServerUtils.getThermOptionOpStateID(starsOption.getMode(), selectionLists).intValue() );
			liteOption.setFanOperationID( ServerUtils.getThermOptionFanOpID(starsOption.getFan(), selectionLists).intValue() );
			
			com.cannontech.database.db.stars.hardware.LMThermostatManualOption option =
					(com.cannontech.database.db.stars.hardware.LMThermostatManualOption) StarsLiteFactory.createDBPersistent( liteOption );
			if (liteSettings.getThermostatOption() == null) {
				Transaction.createTransaction(Transaction.INSERT, option).execute();
				liteSettings.setThermostatOption( liteOption );
			}
			else
				Transaction.createTransaction(Transaction.UPDATE, option).execute();
			
			// Add "config" to the hardware events
            Integer hwEventEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMEREVENT),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_LMHARDWAREEVENT)
            		.getEntryID() );
            Integer configEntryID = new Integer( StarsCustListEntryFactory.getStarsCustListEntry(
            		(LiteCustomerSelectionList) selectionLists.get(com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_LMCUSTOMERACTION),
            		com.cannontech.database.db.stars.CustomerListEntry.YUKONDEF_ACT_CONFIG)
            		.getEntryID() );
            
    		com.cannontech.database.data.stars.event.LMHardwareEvent event = new com.cannontech.database.data.stars.event.LMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMHardwareEvent eventDB = event.getLMHardwareEvent();
    		com.cannontech.database.db.stars.event.LMCustomerEventBase eventBase = event.getLMCustomerEventBase();
    		
    		eventDB.setInventoryID( new Integer(starsSettings.getInventoryID()) );
    		eventBase.setEventTypeID( hwEventEntryID );
    		eventBase.setActionID( configEntryID );
    		eventBase.setEventDateTime( new Date() );
    		
    		event.setEnergyCompanyID( new Integer(energyCompanyID) );
    		event = (com.cannontech.database.data.stars.event.LMHardwareEvent)
    				Transaction.createTransaction( Transaction.INSERT, event ).execute();
    				
    		LiteLMCustomerEvent liteEvent = (LiteLMCustomerEvent) StarsLiteFactory.createLite( event );
    		if (liteHw.getLmHardwareHistory() == null)
    			liteHw.setLmHardwareHistory( new ArrayList() );
    		liteHw.getLmHardwareHistory().add( liteEvent );
				
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Thermostat manual option updated successfully" );
			respOper.setStarsSuccess( success );
			
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
            e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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

			StarsSuccess success = operation.getStarsSuccess();
            if (success == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            	
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
            accountInfo.getStarsThermostatSettings().setStarsThermostatManualOption(
            		reqOper.getStarsUpdateThermostatSettings().getStarsThermostatManualOption() );
            
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
