package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.util.CommonUtils;
import com.cannontech.stars.xml.StarsCallReportFactory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsCreateCallReport;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse;
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
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			if (operator == null) return null;
			Hashtable selectionLists = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );
			
			StarsCreateCallReport createCall = new StarsCreateCallReport();			
			createCall.setCallNumber( req.getParameter("CallNumber") );
			createCall.setCallDate( new Date() );
			
			CallType callType = new CallType();
			callType.setEntryID( Integer.parseInt(req.getParameter("CallType")) );
			StarsCustSelectionList callTypeList = (StarsCustSelectionList) selectionLists.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CALLTYPE );
			for (int i = 0; i < callTypeList.getStarsSelectionListEntryCount(); i++) {
				StarsSelectionListEntry entry = callTypeList.getStarsSelectionListEntry(i);
				if (entry.getEntryID() == callType.getEntryID()) {
					callType.setContent( entry.getContent() );
					break;
				}
			}
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
            
            com.cannontech.database.data.stars.customer.CustomerAccount account =
            		(com.cannontech.database.data.stars.customer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            
            StarsCreateCallReport createCall = reqOper.getStarsCreateCallReport();
            com.cannontech.database.data.stars.report.CallReportBase callReport = new com.cannontech.database.data.stars.report.CallReportBase();
            com.cannontech.database.db.stars.report.CallReportBase callReportDB = callReport.getCallReportBase();
            
            StarsCallReportFactory.setCallReportBase( callReportDB, createCall );
            callReportDB.setAccountID( account.getCustomerAccount().getAccountID() );
            callReportDB.setCustomerID( account.getCustomerBase().getCustomerBase().getCustomerID() );
            
            callReport.setCustomerAccount( account );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, callReport );
            callReport = (com.cannontech.database.data.stars.report.CallReportBase)transaction.execute();
            
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
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsCreateCallReport createCall = reqOper.getStarsCreateCallReport();
			StarsCallReportHistory callHist = (StarsCallReportHistory) StarsCallReportFactory.newStarsCallReport( createCall, StarsCallReportHistory.class );
			
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
			StarsGetCallReportHistoryResponse callHists = (StarsGetCallReportHistoryResponse) operator.getAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "CALL_TRACKING" );
			if (callHists == null) {
				callHists = new StarsGetCallReportHistoryResponse();
				operator.setAttribute( CommonUtils.TRANSIENT_ATT_LEADING + "CALL_TRACKING", callHists );
			}
			callHists.addStarsCallReportHistory( 0, callHist );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
