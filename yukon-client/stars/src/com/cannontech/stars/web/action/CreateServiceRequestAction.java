package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteServiceCompany;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.CurrentState;
import com.cannontech.stars.xml.serialize.ServiceCompany;
import com.cannontech.stars.xml.serialize.ServiceType;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );

			StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();
			createOrder.setDateReported( com.cannontech.util.ServletUtil.parseDateStringLiberally(req.getParameter("DateReported")) );
			createOrder.setOrderedBy( req.getParameter("OrderedBy") );
			createOrder.setDescription( req.getParameter("Notes").replaceAll("\r\n", "<br>") );
			
			String orderNo = req.getParameter("OrderNo");
			String enableOrderNo = req.getParameter("EnableOrderNo");
			if (enableOrderNo != null && Boolean.valueOf(enableOrderNo).booleanValue())
				orderNo = "\"" + orderNo + "\"";
			createOrder.setOrderNumber( orderNo );
			
			ServiceType servType = (ServiceType) StarsFactory.newStarsCustListEntry(
					ServletUtils.getStarsCustListEntryByID(
						selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE, Integer.parseInt(req.getParameter("ServiceType"))),
					ServiceType.class );
			createOrder.setServiceType( servType );
			
			ServiceCompany company = new ServiceCompany();
			company.setEntryID( Integer.parseInt(req.getParameter("ServiceCompany")) );
			createOrder.setServiceCompany( company );
			
            CurrentState status = (CurrentState) StarsFactory.newStarsCustListEntry(
            		ServletUtils.getStarsCustListEntry(
            			selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS, YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_UNSCHEDULED),
            		CurrentState.class );
            createOrder.setCurrentState( status );
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsCreateServiceRequest( createOrder );
			
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
            
            int energyCompanyID = user.getEnergyCompanyID();
            LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
        	
            StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            
            String orderNo = createOrder.getOrderNumber();
            if (orderNo == null) {
        		// Order # not provided, get the next one available
            	orderNo = energyCompany.getNextOrderNumber();
            	if (orderNo == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot get a order #") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
            	}
            }
            
            if (orderNo.charAt(0) == '"') {
        		// User specified order # is contained by quotes
        		orderNo = orderNo.substring( 1, orderNo.length() - 1 );
        		
	        	if (orderNo.trim().length() == 0) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Order # cannot be empty") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
	        	if (orderNo.startsWith( ServerUtils.CTI_NUMBER )) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Order # cannot start with reserved string \"" + ServerUtils.CTI_NUMBER + "\"") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
            	if (WorkOrderBase.orderNumberExists( orderNo, energyCompany.getEnergyCompanyID() )) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Order # already exists, please enter a different one") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
            	}
            }
            else {
            	orderNo = ServerUtils.CTI_NUMBER + orderNo;
    			
	        	if (WorkOrderBase.orderNumberExists( orderNo, energyCompany.getEnergyCompanyID() )) {
	        		energyCompany.resetNextOrderNumber();
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Order # already exists, please try again with the new order #") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
	        	}
            }
        	createOrder.setOrderNumber( orderNo );
            
            com.cannontech.database.data.stars.report.WorkOrderBase workOrder = new com.cannontech.database.data.stars.report.WorkOrderBase();
            com.cannontech.database.db.stars.report.WorkOrderBase workOrderDB = workOrder.getWorkOrderBase();

            StarsFactory.setWorkOrderBase( workOrderDB, createOrder );
            workOrderDB.setAccountID( new Integer(accountInfo.getCustomerAccount().getAccountID()) );
            workOrder.setEnergyCompanyID( new Integer(user.getEnergyCompanyID()) );
            
            Transaction transaction = Transaction.createTransaction( Transaction.INSERT, workOrder );
            workOrder = (com.cannontech.database.data.stars.report.WorkOrderBase)transaction.execute();
            
            LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( workOrderDB );
            ArrayList workOrderList = energyCompany.getAllWorkOrders();
            synchronized (workOrderList) { workOrderList.add( liteOrder ); }
            accountInfo.getServiceRequestHistory().add( 0, new Integer(liteOrder.getOrderID()) );
            
            StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompanyID );
            StarsCreateServiceRequestResponse resp = new StarsCreateServiceRequestResponse();
            resp.setStarsServiceRequest( starsOrder );

            respOper.setStarsCreateServiceRequestResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot create the service request") );
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
            StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
            
			// If we submitted the pre-assigned order #, we need to get a new one
			String orderNo = reqOper.getStarsCreateServiceRequest().getOrderNumber();
			boolean updateOrderNo = (orderNo != null && orderNo.charAt(0) != '"');
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				if (failure.getStatusCode() == StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD && updateOrderNo)
					user.removeAttribute( ServletUtils.ATT_ORDER_TRACKING_NUMBER );
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation) user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			if (accountInfo == null)
				return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
				
			StarsCreateServiceRequestResponse resp = operation.getStarsCreateServiceRequestResponse();
			accountInfo.getStarsServiceRequestHistory().addStarsServiceRequest( 0, resp.getStarsServiceRequest() );
			if (updateOrderNo) user.removeAttribute( ServletUtils.ATT_ORDER_TRACKING_NUMBER );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
