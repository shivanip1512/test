package com.cannontech.stars.web.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsGetServiceRequestHistoryResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: GetServiceRequestHistoryAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 7, 2002 5:52:47 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class GetServiceHistoryAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsGetServiceRequestHistory getServHist = new StarsGetServiceRequestHistory();
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsGetServiceRequestHistory( getServHist );
			
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
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsCustAccountInformation accountInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	if (accountInfo == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find customer account information") );
            	return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	int energyCompanyID = user.getEnergyCompanyID();
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
        	
        	if (accountInfo.getServiceRequestHistory() == null) {
		        com.cannontech.database.db.stars.report.WorkOrderBase[] orders =
		        		com.cannontech.database.db.stars.report.WorkOrderBase.getAllAccountWorkOrders( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
		        if (orders == null) {
		        	respOper.setStarsFailure( StarsFactory.newStarsFailure(
		        			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot find service request history") );
		        	return SOAPUtil.buildSOAPMessage( respOper );
		        }
		        
	        	accountInfo.setServiceRequestHistory( new ArrayList() );
	        	for (int i = 0; i < orders.length; i++) {
	        		LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( orders[i] );
	        		energyCompany.addWorkOrderBase( liteOrder );
	        	}
        	}
        	
        	StarsServiceRequestHistory orderHist = new StarsServiceRequestHistory();
        	for (int i = 0; i < accountInfo.getServiceRequestHistory().size(); i++) {
        		LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( ((Integer) accountInfo.getServiceRequestHistory().get(i)).intValue() );
        		orderHist.addStarsServiceRequest( StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompanyID ) );
        	}
        	
        	StarsGetServiceRequestHistoryResponse resp = new StarsGetServiceRequestHistoryResponse();
            resp.setStarsServiceRequestHistory( orderHist );
            
            respOper.setStarsGetServiceRequestHistoryResponse( resp );
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
            
            StarsGetServiceRequestHistoryResponse getServHistResp = operation.getStarsGetServiceRequestHistoryResponse();
            if (getServHistResp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute(ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            if (accountInfo == null)
            	return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
            	
            accountInfo.setStarsServiceRequestHistory( getServHistResp.getStarsServiceRequestHistory() );
			return 0;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
