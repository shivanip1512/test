package com.cannontech.stars.web.action;

import javax.servlet.http.*;
import javax.xml.soap.SOAPMessage;
import com.cannontech.stars.web.StarsOperator;
import java.util.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.database.Transaction;

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
			servType.setContent( req.getParameter("ServiceType") );
			createOrder.setServiceType( servType );
			createOrder.setDateAssigned( new Date() );
			ServiceCompany servCompany = new ServiceCompany();
			servCompany.setContent( req.getParameter("ServiceCompany") );
			createOrder.setServiceCompany( servCompany );
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
            
            com.cannontech.database.data.starscustomer.CustomerAccount account =
            		(com.cannontech.database.data.starscustomer.CustomerAccount) operator.getAttribute("CUSTOMER_ACCOUNT");
            		
            StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            com.cannontech.database.data.starsreport.WorkOrderBase workOrder = new com.cannontech.database.data.starsreport.WorkOrderBase();
            com.cannontech.database.db.starsreport.WorkOrderBase workOrderDB = workOrder.getWorkOrderBase();
            
            workOrderDB.setOrderNumber( Integer.valueOf(createOrder.getOrderNumber()) );
            workOrderDB.setWorkType( createOrder.getServiceType().getContent() );
            workOrderDB.setCurrentState( "Open" );
            workOrderDB.setDateAssigned( createOrder.getDateAssigned() );
            workOrderDB.setDescription( createOrder.getDescription() );
            workOrderDB.setServiceProviderName( createOrder.getServiceCompany().getContent() );
            workOrderDB.setAccountNumber( account.getCustomerAccount().getAccountNumber() );
            workOrderDB.setDateCompleted( new Date() );
            workOrderDB.setActionTaken("");
            workOrderDB.setSPContactFirstName("");
            workOrderDB.setSPContactLastName("");
            workOrderDB.setSPMainPhone("");
            workOrderDB.setSPSecondPhone("");
            workOrderDB.setAcctAddress1("");
            workOrderDB.setAcctAddress2("");
            workOrderDB.setAcctCity("");
            workOrderDB.setAcctFirstName("");
            workOrderDB.setAcctGridNumber("");
            workOrderDB.setAcctLastName("");
            workOrderDB.setAcctMainPhone("");
            workOrderDB.setAcctSecondPhone("");
            workOrderDB.setAcctState("");
            workOrderDB.setAcctZip("");
            
            workOrder.setEnergyCompanyBase( account.getCustomerBase().getEnergyCompanyBase() );
            workOrder.setWorkOrderBase( workOrderDB );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, workOrder );
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
			operator.removeAttribute("SERVICE_HISTORY");
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
