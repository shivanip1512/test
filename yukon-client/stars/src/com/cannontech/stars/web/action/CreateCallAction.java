package com.cannontech.stars.web.action;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.report.CallReportBase;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCreateCallReport;
import com.cannontech.stars.xml.serialize.StarsCreateCallReportResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: CreateCallAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 5, 2002 2:25:37 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CreateCallAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
			TimeZone tz = TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
			if (tz == null) tz = TimeZone.getDefault();
			
			StarsCreateCallReport createCall = new StarsCreateCallReport();
			if (req.getParameter("CallNo") != null)
				createCall.setCallNumber( req.getParameter("CallNo") );
			createCall.setTakenBy( req.getParameter("TakenBy") );
			createCall.setDescription( req.getParameter("Description").replaceAll(System.getProperty("line.separator"), "<br>") );
			
			java.util.Date callDate = ServletUtils.parseDateTime(
					req.getParameter("CallDate"), req.getParameter("CallTime"), tz );
			if (callDate == null)
				throw new WebClientException("Invalid date/time format '" + req.getParameter("CallDate") + " " + req.getParameter("CallTime") + "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
			createCall.setCallDate( callDate );
			
			CallType callType = new CallType();
			callType.setEntryID( Integer.parseInt(req.getParameter("CallType")) );
			createCall.setCallType( callType );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateCallReport( createCall );
			
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

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public synchronized SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();
        
		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
			LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) session.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
            
			StarsCreateCallReport createCall = reqOper.getStarsCreateCallReport();
            
			String callNo = createCall.getCallNumber();
			if (callNo != null) {
				if (callNo.trim().length() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Tracking # cannot be empty") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				if (callNo.startsWith( ServerUtils.AUTO_GEN_NUM_PREC )) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Tracking # cannot start with reserved string \"" + ServerUtils.AUTO_GEN_NUM_PREC + "\"") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				if (CallReportBase.callNumberExists( callNo, energyCompany.getEnergyCompanyID() )) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Tracking # already exists, please enter a different one") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
			}
			else {
				// Call tracking # not provided, get the next one available
				callNo = energyCompany.getNextCallNumber();
				if (callNo == null) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot assign a tracking #") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				createCall.setCallNumber( ServerUtils.AUTO_GEN_NUM_PREC + callNo );
			}
            
			com.cannontech.database.data.stars.report.CallReportBase callReport = new com.cannontech.database.data.stars.report.CallReportBase();
			com.cannontech.database.db.stars.report.CallReportBase callReportDB = callReport.getCallReportBase();
            
			StarsFactory.setCallReportBase( callReportDB, createCall );
			callReportDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
			callReport.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
            
			callReport = (com.cannontech.database.data.stars.report.CallReportBase)
					Transaction.createTransaction(Transaction.INSERT, callReport).execute();
            
			StarsCallReport call = StarsFactory.newStarsCallReport( callReport.getCallReportBase() );
			accountInfo.getCallReportHistory().add( 0, call );
            
			StarsCreateCallReportResponse resp = new StarsCreateCallReportResponse();
			resp.setStarsCallReport( call );
            
			respOper.setStarsCreateCallReportResponse( resp );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the call report") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			catch (Exception e2) {
				CTILogger.error( e2.getMessage(), e2 );
			}
		}
        
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
		try {
			StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsCreateCallReportResponse resp = operation.getStarsCreateCallReportResponse();
			accountInfo.getStarsCallReportHistory().addStarsCallReport( 0, resp.getStarsCallReport() );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
