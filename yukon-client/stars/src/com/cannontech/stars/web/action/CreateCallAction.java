package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsCallReportFactory;
import com.cannontech.stars.xml.StarsFailureFactory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsCreateCallReport;
import com.cannontech.stars.xml.serialize.StarsCreateCallReportResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
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
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
            
            StarsCreateCallReport createCall = reqOper.getStarsCreateCallReport();
            com.cannontech.database.data.stars.report.CallReportBase callReport = new com.cannontech.database.data.stars.report.CallReportBase();
            com.cannontech.database.db.stars.report.CallReportBase callReportDB = callReport.getCallReportBase();
            
            StarsCallReportFactory.setCallReportBase( callReportDB, createCall );
            callReportDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            
            callReport.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, callReport );
            callReport = (com.cannontech.database.data.stars.report.CallReportBase)transaction.execute();
            
            StarsCallReport call = (StarsCallReport) StarsCallReportFactory.newStarsCallReport( createCall, StarsCallReport.class );
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
            	respOper.setStarsFailure( StarsFailureFactory.newStarsFailure(
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

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsCreateCallReportResponse resp = operation.getStarsCreateCallReportResponse();
			StarsCallReport call = resp.getStarsCallReport();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			// Call report history must already be retrieved before, e.g. when the account info is loaded
			accountInfo.getStarsCallReportHistory().addStarsCallReport( 0, call );
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
