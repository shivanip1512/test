/*
 * Created on Oct 23, 2003
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
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsDeleteServiceRequest;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeleteServiceRequestAction implements ActionBase {

	/* (non-Javadoc)
	 * @see com.cannontech.stars.web.action.ActionBase#build(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsDeleteServiceRequest deleteOrder = new StarsDeleteServiceRequest();
			deleteOrder.setOrderID( Integer.parseInt(req.getParameter("OrderID")) );
			StarsOperation operation = new StarsOperation();
			operation.setStarsDeleteServiceRequest( deleteOrder );
			
			return SOAPUtil.buildSOAPMessage( operation );
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
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
			
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			
			int orderID = reqOper.getStarsDeleteServiceRequest().getOrderID();
			LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( orderID, true );
			
			com.cannontech.database.data.stars.report.WorkOrderBase order =
					new com.cannontech.database.data.stars.report.WorkOrderBase();
			order.setOrderID( new Integer(orderID) );
			Transaction.createTransaction(Transaction.DELETE, order).execute();
			energyCompany.deleteWorkOrderBase( orderID );
			
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			boolean fromWorkOrder = false;
			
			if (liteAcctInfo == null || liteAcctInfo.getAccountID() != liteOrder.getAccountID()) {
				// Request from WorkOrder.jsp
				liteAcctInfo = energyCompany.getCustAccountInformation(liteOrder.getAccountID(), false);
				fromWorkOrder = true;
			}
			
			if (liteAcctInfo != null) {
				java.util.ArrayList orderIDs = liteAcctInfo.getServiceRequestHistory();
				for (int i = 0; i < orderIDs.size(); i++) {
					if (((Integer) orderIDs.get(i)).intValue() == orderID) {
						orderIDs.remove(i);
						break;
					}
				}
			}
			
			if (fromWorkOrder) {	
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteOrder.getAccountID() );
				if (starsAcctInfo != null)
					parseResponse( orderID, starsAcctInfo );
				
				respOper.setStarsSuccess( new StarsSuccess() );
				return SOAPUtil.buildSOAPMessage( respOper );
			}
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription( "Work order deleted successfully" );
            
            respOper.setStarsSuccess( success );
			return SOAPUtil.buildSOAPMessage( respOper );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
            
			try {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the service request") );
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
			if (operation.getStarsSuccess().getDescription() == null)
				return 0;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			if (accountInfo == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			parseResponse( reqOper.getStarsDeleteServiceRequest().getOrderID(), accountInfo );
			
			return 0;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	private void parseResponse(int orderID, StarsCustAccountInformation starsAcctInfo) {
		StarsServiceRequestHistory soHist = starsAcctInfo.getStarsServiceRequestHistory();
		for (int i = 0; i < soHist.getStarsServiceRequestCount(); i++) {
			if (soHist.getStarsServiceRequest(i).getOrderID() == orderID) {
				soHist.removeStarsServiceRequest(i);
				return;
			}
		}
	}

}
