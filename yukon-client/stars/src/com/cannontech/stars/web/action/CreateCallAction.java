package com.cannontech.stars.web.action;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.stars.report.CallReportBase;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCreateCallReport;
import com.cannontech.stars.xml.serialize.StarsCreateCallReportResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
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
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			StarsCreateCallReport createCall = new StarsCreateCallReport();
			createCall.setCallDate( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("CallDate")) );
			createCall.setTakenBy( req.getParameter("TakenBy") );
			createCall.setDescription( req.getParameter("Description").replaceAll("\r\n", "<br>") );
			
			String callNo = req.getParameter("CallNo");
			String enableCallNo = req.getParameter("EnableCallNo");
			if (enableCallNo != null && Boolean.valueOf(enableCallNo).booleanValue())
				callNo = "\"" + callNo + "\"";	// Add quotes to indicate that this is a user specified call #
			createCall.setCallNumber( callNo );
			
			CallType callType = (CallType) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE, Integer.parseInt(req.getParameter("CallType"))),
					CallType.class );
			createCall.setCallType( callType );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateCallReport( createCall );
			
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
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	int energyCompanyID = user.getEnergyCompanyID();
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
            
            StarsCreateCallReport createCall = reqOper.getStarsCreateCallReport();
            
        	String callNo = createCall.getCallNumber();
        	if (callNo == null) {
        		// Call # not provided, get the next one available
        		callNo = energyCompany.getNextCallNumber();
        		if (callNo == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get a call tracking number") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
        		}
        	}
        	
        	if (callNo.charAt(0) == '"') {
        		// User specified call # is contained by quotes
        		callNo = callNo.substring( 1, callNo.length() - 1 );
        		
	        	if (callNo.trim().length() == 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Tracking # cannot be empty") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
	        	if (callNo.startsWith( ServerUtils.CTI_NUMBER )) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Tracking # cannot start with reserved string \"" + ServerUtils.CTI_NUMBER + "\"") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
	        	if (CallReportBase.callNumberExists( callNo, energyCompany.getEnergyCompanyID() )) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Tracking # already exists, please enter a different one") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
        	}
        	else {
    			callNo = ServerUtils.CTI_NUMBER + callNo;
    			
	        	if (CallReportBase.callNumberExists( callNo, energyCompany.getEnergyCompanyID() )) {
	        		energyCompany.resetNextCallNumber();
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Tracking # already exists, please try again with the new tracking #") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
        	}
    		createCall.setCallNumber( callNo );
            
            com.cannontech.database.data.stars.report.CallReportBase callReport = new com.cannontech.database.data.stars.report.CallReportBase();
            com.cannontech.database.db.stars.report.CallReportBase callReportDB = callReport.getCallReportBase();
            
            StarsFactory.setCallReportBase( callReportDB, createCall );
            callReportDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            callReport.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, callReport );
            callReport = (com.cannontech.database.data.stars.report.CallReportBase)transaction.execute();
            
            StarsCallReport call = (StarsCallReport) StarsFactory.newStarsCallReport( createCall, StarsCallReport.class );
            if (call.getCallNumber().startsWith( ServerUtils.CTI_NUMBER ))
            	call.setCallNumber( call.getCallNumber().substring(ServerUtils.CTI_NUMBER.length()) );
            call.setCallID( callReport.getCallReportBase().getCallID().intValue() );
            accountInfo.getCallReportHistory().add( 0, call );
            
            StarsCreateCallReportResponse resp = new StarsCreateCallReportResponse();
            resp.setStarsCallReport( call );
            
            respOper.setStarsCreateCallReportResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the call report") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
            
			// If we submitted the pre-assigned call #, we need to get a new one
			String callNo = reqOper.getStarsCreateCallReport().getCallNumber();
			boolean updateCallNo = (callNo != null && callNo.charAt(0) != '"');
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				if (failure.getStatusCode() == StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD && updateCallNo)
					user.removeAttribute( ServletUtils.ATT_CALL_TRACKING_NUMBER );
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (accountInfo == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsCreateCallReportResponse resp = operation.getStarsCreateCallReportResponse();
			accountInfo.getStarsCallReportHistory().addStarsCallReport( 0, resp.getStarsCallReport() );
			if (updateCallNo) user.removeAttribute( ServletUtils.ATT_CALL_TRACKING_NUMBER );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
