package com.cannontech.stars.web.action;

import javax.servlet.http.*;
import javax.xml.soap.SOAPMessage;
import java.util.*;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;

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
            
            com.cannontech.database.data.starscustomer.CustomerAccount account =
            		(com.cannontech.database.data.starscustomer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            com.cannontech.database.db.starsreport.WorkOrderBase[] orders =
            		com.cannontech.database.db.starsreport.WorkOrderBase.getAllWorkOrders(
            			account.getCustomerBase().getCustomerBase().getCustomerID(), conn );
            if (orders == null) return null;
            
            StarsGetServiceRequestHistoryResponse getServHistResp = new StarsGetServiceRequestHistoryResponse();
            for (int i = 0; i < orders.length; i++) {
            	StarsServiceRequestHistory servHist = new StarsServiceRequestHistory();
            	
            	servHist.setOrderNumber( orders[i].getOrderNumber().toString() );
            	ServiceType servType = new ServiceType();
            	servType.setContent( orders[i].getWorkType() );
            	servHist.setServiceType( servType );
            	CurrentState state = new CurrentState();
            	state.setContent( orders[i].getCurrentState() );
            	servHist.setCurrentState( state );
            	servHist.setDateAssigned( orders[i].getDateAssigned() );
            	servHist.setDescription( orders[i].getDescription() );
            	servHist.setDateCompleted( orders[i].getDateCompleted() );
            	servHist.setActionTaken( orders[i].getActionTaken() );
            	ServiceCompany servCompany = new ServiceCompany();
            	servCompany.setContent( orders[i].getServiceProviderName() );
            	servHist.setServiceCompany( servCompany );
            	
            	getServHistResp.addStarsServiceRequestHistory( servHist );
            }
            
            respOper.setStarsGetServiceRequestHistoryResponse( getServHistResp );
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
            
            StarsGetServiceRequestHistoryResponse getServHistResp = operation.getStarsGetServiceRequestHistoryResponse();
            if (getServHistResp == null) return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
            
			StarsOperator operator = (StarsOperator) session.getAttribute("OPERATOR");
            operator.setAttribute("SERVICE_HISTORY", getServHistResp);
            
			return 0;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
