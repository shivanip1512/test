package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCreateCallReport;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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
			StarsCreateCallReport createCall = new StarsCreateCallReport();

			/*
			String dateStr = req.getParameter("CallDate");
			if (dateStr != null)
				createCall.setCallDate( new org.exolab.castor.types.Date(dateFormat.parse(dateStr)) );
			else
				createCall.setCallDate( new org.exolab.castor.types.Date(Calendar.getInstance().getTime()) );
			*/
			String callNumber = req.getParameter("CallNumber");
			if (callNumber == null || callNumber.equals("")) return null;
			
			createCall.setCallNumber( callNumber );
			createCall.setCallDate( new org.exolab.castor.types.Date(new Date()) );
			CallType callType = new CallType();
			callType.setContent( req.getParameter("CallType") );
			createCall.setCallType( callType );
			createCall.setTakenBy( req.getParameter("TakenBy") );
			createCall.setDescription( req.getParameter("Description") );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateCallReport( createCall );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
        try {
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            StarsOperation respOper = new StarsOperation();
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            if (operator == null) {
            	StarsFailure failure = new StarsFailure();
            	failure.setStatusCode( StarsConstants.FAILURE_CODE_SESSION_INVALID );
            	failure.setDescription( "Session invalidated, please login again" );
            	respOper.setStarsFailure( failure );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            com.cannontech.database.data.starscustomer.CustomerAccount account =
            		(com.cannontech.database.data.starscustomer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            
            StarsCreateCallReport createCall = reqOper.getStarsCreateCallReport();
            com.cannontech.database.data.starsreport.CallReportBase callReport = new com.cannontech.database.data.starsreport.CallReportBase();
            com.cannontech.database.db.starsreport.CallReportBase callReportDB = callReport.getCallReportBase();
            
            callReportDB.setCallNumber( createCall.getCallNumber() );
            callReportDB.setCallType( createCall.getCallType().getContent() );
            callReportDB.setDateTaken( createCall.getCallDate().toDate() );
            callReportDB.setDescription( createCall.getDescription() );
            callReportDB.setActionItems( "" );
            callReportDB.setRelatedToAccountNumber( account.getCustomerAccount().getAccountNumber() );
            callReportDB.setCustomerID( account.getCustomerBase().getCustomerBase().getCustomerID() );
            
            callReport.setCustomerBase( account.getCustomerBase() );
            callReport.setCallReportBase( callReportDB );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, callReport );
            transaction.execute();
            
            StarsSuccess success = new StarsSuccess();
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) return failure.getStatusCode();
			
            if (operation.getStarsSuccess() == null)
            	return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            	
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			operator.removeAttribute("CALL_TRACKING");
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
