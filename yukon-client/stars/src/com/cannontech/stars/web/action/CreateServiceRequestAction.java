package com.cannontech.stars.web.action;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.web.servlet.WorkOrderManager;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
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
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			
			StarsGetEnergyCompanySettingsResponse ecSettings =
					(StarsGetEnergyCompanySettingsResponse) user.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
			TimeZone tz = TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
			if (tz == null) tz = TimeZone.getDefault();
			
			StarsOperation operation = (StarsOperation) session.getAttribute(WorkOrderManager.STARS_WORK_ORDER_OPER_REQ);
			session.removeAttribute( WorkOrderManager.STARS_WORK_ORDER_OPER_REQ );
			if (operation == null)
				operation = getRequestOperation( req, tz );
			
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
            
			LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
			LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	
			StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            
            String orderNo = createOrder.getOrderNumber();
            if (orderNo != null) {
				if (orderNo.trim().length() == 0) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Order # cannot be empty") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				if (orderNo.startsWith( ServerUtils.AUTO_GEN_NUM_PREC )) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Order # cannot start with reserved string \"" + ServerUtils.AUTO_GEN_NUM_PREC + "\"") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
				if (WorkOrderBase.orderNumberExists( orderNo, energyCompany.getEnergyCompanyID() )) {
					respOper.setStarsFailure( StarsFactory.newStarsFailure(
							StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Order # already exists, please enter a different one") );
					return SOAPUtil.buildSOAPMessage( respOper );
				}
            }
            else {
        		// Order # not provided, get the next one available
            	orderNo = energyCompany.getNextOrderNumber();
            	if (orderNo == null) {
	            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
	            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot assign an order #") );
	            	return SOAPUtil.buildSOAPMessage( respOper );
            	}
				createOrder.setOrderNumber( ServerUtils.AUTO_GEN_NUM_PREC + orderNo );
            }
            
            com.cannontech.database.data.stars.report.WorkOrderBase workOrder = new com.cannontech.database.data.stars.report.WorkOrderBase();
            com.cannontech.database.db.stars.report.WorkOrderBase workOrderDB = workOrder.getWorkOrderBase();
            
            StarsFactory.setWorkOrderBase( workOrderDB, createOrder );
            
            if (createOrder.getCurrentState() == null) {
				workOrderDB.setCurrentStateID( new Integer(
						energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING).getEntryID()) );
            }
            if (!createOrder.hasAccountID())
	            workOrderDB.setAccountID( new Integer(liteAcctInfo.getAccountID()) );
            workOrder.setEnergyCompanyID( energyCompany.getEnergyCompanyID() );
            
            workOrder = (com.cannontech.database.data.stars.report.WorkOrderBase)
					Transaction.createTransaction(Transaction.INSERT, workOrder).execute();
            
            LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( workOrderDB );
            energyCompany.addWorkOrderBase( liteOrder );
            
            if (createOrder.hasAccountID()) {
            	// Request from CreateOrder.jsp
				liteAcctInfo = energyCompany.getCustAccountInformation(createOrder.getAccountID(), false);
				if (liteAcctInfo != null)
					liteAcctInfo.getServiceRequestHistory().add( 0, new Integer(liteOrder.getOrderID()) );
				
				// The request parameter REDIRECT doesn't know the order ID,
				// so we append it to the end of the parameter value
				String redirect = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
				redirect += liteOrder.getOrderID();
				session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
				
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( createOrder.getAccountID() );
				if (starsAcctInfo != null) {
					StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompany );
					starsAcctInfo.getStarsServiceRequestHistory().addStarsServiceRequest(0, starsOrder);
				}
				
				respOper.setStarsSuccess( new StarsSuccess() );
				return SOAPUtil.buildSOAPMessage( respOper );
            }
            
			liteAcctInfo.getServiceRequestHistory().add( 0, new Integer(liteOrder.getOrderID()) );
            
            StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompany );
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

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			if (operation.getStarsSuccess() != null) return 0;
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
				
			StarsCreateServiceRequestResponse resp = operation.getStarsCreateServiceRequestResponse();
			accountInfo.getStarsServiceRequestHistory().addStarsServiceRequest( 0, resp.getStarsServiceRequest() );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz)
	throws WebClientException {
		StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();
		WorkOrderManager.setStarsServiceRequest( createOrder, req, tz );
			
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateServiceRequest( createOrder );
		return operation;
	}

}
