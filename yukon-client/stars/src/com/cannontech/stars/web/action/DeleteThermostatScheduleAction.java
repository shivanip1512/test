/*
 * Created on May 24, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.hardware.LMThermostatSchedule;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteThermostatSchedule;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSavedThermostatSchedules;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteThermostatScheduleAction implements ActionBase {

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#build(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			int scheduleID = Integer.parseInt( req.getParameter("ScheduleID") );
			if (scheduleID == -1)
				throw new WebClientException( "You must select a saved schedule" );
			
			StarsDeleteThermostatSchedule deleteSchedule = new StarsDeleteThermostatSchedule();
			deleteSchedule.setScheduleID( scheduleID );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteThermostatSchedule( deleteSchedule );
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException e) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Invalid request parameters" );
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#process(javax.xml.soap.SOAPMessage, javax.servlet.http.HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
		
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsDeleteThermostatSchedule deleteSchedule = reqOper.getStarsDeleteThermostatSchedule();
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (liteAcctInfo == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			
			LiteLMThermostatSchedule liteSchedule = null;
			for (int i = 0; i < liteAcctInfo.getThermostatSchedules().size(); i++) {
				if (((LiteLMThermostatSchedule) liteAcctInfo.getThermostatSchedules().get(i)).getScheduleID() == deleteSchedule.getScheduleID()) {
					liteSchedule = (LiteLMThermostatSchedule) liteAcctInfo.getThermostatSchedules().get(i);
					break;
				}
			}
			
			LMThermostatSchedule schedule = StarsLiteFactory.createLMThermostatSchedule( liteSchedule );
			Transaction.createTransaction( Transaction.DELETE, schedule ).execute();
			
			liteAcctInfo.getThermostatSchedules().remove( liteSchedule );
			
			StarsSuccess success = new StarsSuccess();
			success.setDescription( "Thermostat schedule deleted successfully" );
			
			respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot apply thermostat schedule") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#parse(javax.xml.soap.SOAPMessage, javax.xml.soap.SOAPMessage, javax.servlet.http.HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
            
			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			if (operation.getStarsSuccess() == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsDeleteThermostatSchedule deleteSchedule = SOAPUtil.parseSOAPMsgForOperation(reqMsg).getStarsDeleteThermostatSchedule();
			
			StarsCustAccountInformation starsAcctInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsSavedThermostatSchedules schedules = starsAcctInfo.getStarsSavedThermostatSchedules();
			for (int i = 0; i < schedules.getStarsThermostatProgramCount(); i++) {
				if (schedules.getStarsThermostatProgram(i).getScheduleID() == deleteSchedule.getScheduleID()) {
					schedules.removeStarsThermostatProgram(i);
					break;
				}
			}
			
			session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, "Thermostat schedule deleted successfully" );
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
