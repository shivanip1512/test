package com.cannontech.stars.web.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.database.Transaction;
import com.cannontech.stars.web.StarsOperator;
import com.cannontech.stars.web.util.CommonUtils;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * <p>Title: CreateServiceRequestAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 7, 2002 1:10:51 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CreateServiceRequestAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();

			String orderNumber = req.getParameter("OrderNumber");
			if (orderNumber == null || orderNumber.equals("")) return null;
			createOrder.setOrderNumber( orderNumber );
			
			ServiceType servType = new ServiceType();
			servType.setEntryID( Integer.parseInt(req.getParameter("ServiceType")) );
			createOrder.setServiceType( servType );
			
			ServiceCompany servCompany = new ServiceCompany();
			servCompany.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			createOrder.setServiceCompany( servCompany );
			
			createOrder.setDateReported( new Date() );
			createOrder.setDescription( req.getParameter("Notes") );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateServiceRequest( createOrder );
			
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
            		
            StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            com.cannontech.database.data.stars.report.WorkOrderBase workOrder = new com.cannontech.database.data.stars.report.WorkOrderBase();
            com.cannontech.database.db.stars.report.WorkOrderBase workOrderDB = workOrder.getWorkOrderBase();
            
            workOrderDB.setOrderNumber( createOrder.getOrderNumber() );
            workOrderDB.setWorkTypeID( new Integer(createOrder.getServiceType().getEntryID()) );
            workOrderDB.setCurrentStateID( new Integer(19) );	// "To be scheduled"
            workOrderDB.setCustomerID( account.getCustomerBase().getCustomerBase().getCustomerID() );
            workOrderDB.setSiteID( account.getAccountSite().getAccountSite().getAccountSiteID() );
            workOrderDB.setServiceCompanyID( new Integer(createOrder.getServiceCompany().getEntryID()) );
            workOrderDB.setDateReported( createOrder.getDateReported() );
            workOrderDB.setDescription( createOrder.getDescription() );
            
            workOrder.setCustomerBase( account.getCustomerBase() );
            workOrder.setWorkOrderBase( workOrderDB );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, workOrder );
            workOrder = (com.cannontech.database.data.stars.report.WorkOrderBase)transaction.execute();
            
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
			operator.removeAttribute(CommonUtils.TRANSIENT_ATT_LEADING + "SERVICE_HISTORY");
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
