package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateCallReport;
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
public class UpdateCallReportAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			String[] changed = req.getParameterValues( "changed" );
			String[] callIDs = req.getParameterValues( "CallID" );
			String[] callNos = req.getParameterValues( "CallNo" );
			String[] callTypes = req.getParameterValues( "CallType" );
			String[] descriptions = req.getParameterValues( "Description" );
			
			StarsUpdateCallReport updateCalls = new StarsUpdateCallReport();
			for (int i = 0; i < changed.length; i++) {
				if (changed[i].equals("true")) {
					StarsCallReport call = new StarsCallReport();
					call.setCallID( Integer.parseInt(callIDs[i]) );
					
					if (callNos != null) call.setCallNumber( callNos[i] );
					if (descriptions != null) call.setDescription( descriptions[i] );
					
					if (callTypes != null) {
						CallType callType = (CallType) StarsFactory.newStarsCustListEntry(
								ServletUtils.getStarsCustListEntryByID(
									selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_CALL_TYPE, Integer.parseInt(callTypes[i])),
								CallType.class );
						call.setCallType( callType );
					}
					
					updateCalls.addStarsCallReport( call );
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateCallReport( updateCalls );
			
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
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	ArrayList callHist = accountInfo.getCallReportHistory();
        	StarsUpdateCallReport updateCalls = reqOper.getStarsUpdateCallReport();
        	
			com.cannontech.database.db.stars.report.CallReportBase callDB = new com.cannontech.database.db.stars.report.CallReportBase();
			callDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
        	
        	for (int i = 0; i < updateCalls.getStarsCallReportCount(); i++) {
        		StarsCallReport newCall = updateCalls.getStarsCallReport(i);
        		for (int j = 0; j < callHist.size(); j++) {
        			StarsCallReport call = (StarsCallReport) callHist.get(j);
        			if (call.getCallID() == newCall.getCallID()) {
        				if (newCall.getCallType() != null) call.setCallType( newCall.getCallType() );
        				if (newCall.getDescription() != null) call.setDescription( newCall.getDescription() );
        				
        				if (newCall.getCallNumber() != null) {
        					if (!call.getCallNumber().equals( newCall.getCallNumber() )
        						&& ServerUtils.callNumberExists( newCall.getCallNumber(), user.getEnergyCompanyID() )) {
				            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
				            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Call number already exists, please choose a different one") );
				            	return SOAPUtil.buildSOAPMessage( respOper );
        					}
        					call.setCallNumber( newCall.getCallNumber() );
        				}
        				
        				StarsFactory.setCallReportBase( callDB, call );
        				callDB.setCallID( new Integer(call.getCallID()) );
						Transaction.createTransaction(Transaction.UPDATE, callDB).execute();
        				break;
        			}
        		}
        	}
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Call reports updated successfully");
            
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the call reports") );
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
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateCallReport updateCalls = reqOper.getStarsUpdateCallReport();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsCallReportHistory callHist = accountInfo.getStarsCallReportHistory();
			
			for (int i = 0; i < updateCalls.getStarsCallReportCount(); i++) {
				StarsCallReport newCall = updateCalls.getStarsCallReport(i);
				for (int j = 0; j < callHist.getStarsCallReportCount(); j++) {
					StarsCallReport call = callHist.getStarsCallReport(j);
					if (call.getCallID() == newCall.getCallID()) {
						if (newCall.getCallNumber() != null) call.setCallNumber( newCall.getCallNumber() );
						if (newCall.getCallType() != null) call.setCallType( newCall.getCallType() );
						if (newCall.getDescription() != null) call.setDescription( newCall.getDescription() );
						break;
					}
				}
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
