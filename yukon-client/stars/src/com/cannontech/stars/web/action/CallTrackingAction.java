package com.cannontech.stars.web.action;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsGetCallReportHistoryResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: CallTrackingAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 5, 2002 4:01:37 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CallTrackingAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetCallReportHistory callTracking = new StarsGetCallReportHistory();
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetCallReportHistory( callTracking );
			
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
		java.sql.Connection conn = null;
		
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
            
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            com.cannontech.database.db.stars.report.CallReportBase[] calls =
            		com.cannontech.database.db.stars.report.CallReportBase.getAllAccountCallReports(
            			account.getCustomerAccount().getAccountID(), conn );
            if (calls == null) return null;
            
            Hashtable selectionListTable = (Hashtable) operator.getAttribute( "CUSTOMER_SELECTION_LIST" );
            com.cannontech.database.db.stars.CustomerListEntry[] callTypes =
            		com.cannontech.database.data.stars.CustomerListEntry.getAllListEntries(
            			(Integer) selectionListTable.get( com.cannontech.database.db.stars.CustomerSelectionList.LISTNAME_CALLTYPE) );
            
            StarsGetCallReportHistoryResponse callTrackingResp = new StarsGetCallReportHistoryResponse();
            for (int i = 0; i < calls.length; i++) {
            	StarsCallReportHistory callHist = new StarsCallReportHistory();
            	
				callHist.setCallNumber( calls[i].getCallNumber() );
            	callHist.setCallDate( calls[i].getDateTaken() );
            	
            	CallType callType = new CallType();
            	for (int j = 0; j < callTypes.length; j++)
            		if (callTypes[j].getEntryID().intValue() == calls[i].getCallTypeID().intValue()) {
            			callType.setEntryID( callTypes[j].getEntryID().intValue() );
		            	callType.setContent( callTypes[j].getEntryText() );
            		}
            	callHist.setCallType( callType );
            	
            	callHist.setTakenBy( "" );
            	callHist.setDescription( calls[i].getDescription() );
            	
            	callTrackingResp.addStarsCallReportHistory( callHist );
            }
            
            respOper.setStarsGetCallReportHistoryResponse( callTrackingResp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {
        	try {
	        	if (conn != null) conn.close();
        	}
        	catch (Exception e) {
        		e.printStackTrace();
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
            if (failure != null) return failure.getStatusCode();
            
            StarsGetCallReportHistoryResponse callTrackingResp = operation.getStarsGetCallReportHistoryResponse();
            if (callTrackingResp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            operator.setAttribute("CALL_TRACKING", callTrackingResp);
            
			return 0;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
