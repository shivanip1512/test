package com.cannontech.stars.web.action;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
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
			
			StarsEnergyCompanySettings ecSettings =
					(StarsEnergyCompanySettings) session.getAttribute(ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
			TimeZone tz = TimeZone.getTimeZone( ecSettings.getStarsEnergyCompany().getTimeZone() );
			if (tz == null) tz = TimeZone.getDefault();
			
			StarsOperation operation = (StarsOperation) session.getAttribute(WorkOrderManagerUtil.STARS_WORK_ORDER_OPER_REQ);
			session.removeAttribute( WorkOrderManagerUtil.STARS_WORK_ORDER_OPER_REQ );
			if (operation == null)
				operation = getRequestOperation( req, tz );
			
			return SOAPUtil.buildSOAPMessage( operation );
		}
		catch (WebClientException we) {
			session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, we.getMessage() );
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
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
			StarsCreateServiceRequest createOrder = reqOper.getStarsCreateServiceRequest();
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
			LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
			
			LiteStarsCustAccountInformation liteAcctInfo = null;
			if (createOrder.hasAccountID())	// Request from CreateOrder.jsp
				liteAcctInfo = energyCompany.getCustAccountInformation(createOrder.getAccountID(), false);
			else
				liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
            
            LiteWorkOrderBase liteOrder = null;
            try {
            	liteOrder = createServiceRequest( createOrder, liteAcctInfo, energyCompany );
            }
            catch (WebClientException e) {
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED, e.getMessage()) );
				return SOAPUtil.buildSOAPMessage( respOper );
            }
            
            if (createOrder.hasAccountID()) {
            	// Request from CreateOrder.jsp
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
            
            StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompany );
            StarsCreateServiceRequestResponse resp = new StarsCreateServiceRequestResponse();
            resp.setStarsServiceRequest( starsOrder );
            
            respOper.setStarsCreateServiceRequestResponse( resp );
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
            
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
				
			StarsCreateServiceRequestResponse resp = operation.getStarsCreateServiceRequestResponse();
			accountInfo.getStarsServiceRequestHistory().addStarsServiceRequest( 0, resp.getStarsServiceRequest() );
			
            return 0;
        }
        catch (Exception e) {
            CTILogger.error( e.getMessage(), e );
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz)
		throws WebClientException
	{
		StarsCreateServiceRequest createOrder = new StarsCreateServiceRequest();
		WorkOrderManagerUtil.setStarsServiceRequest( createOrder, req, tz );
		
		StarsOperation operation = new StarsOperation();
		operation.setStarsCreateServiceRequest( createOrder );
		return operation;
	}
	
	public static LiteWorkOrderBase createServiceRequest(StarsCreateServiceRequest createOrder, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, boolean checkConstraint) throws WebClientException, CommandExecutionException
	{
		String orderNo = createOrder.getOrderNumber();
		
		if (checkConstraint) {
			if (orderNo != null) {
				if (orderNo.trim().length() == 0)
					throw new WebClientException( "Order # cannot be empty" );
				if (WorkOrderBase.orderNumberExists( orderNo, energyCompany.getEnergyCompanyID() ))
					throw new WebClientException( "Order # already exists" );
			}
			else {
				// Order # not provided, get the next one available
				orderNo = energyCompany.getNextOrderNumber();
				if (orderNo == null)
					throw new WebClientException( "Failed to assign an order # automatically" );
				createOrder.setOrderNumber( orderNo );
			}
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
		if (liteAcctInfo != null)
			liteAcctInfo.getServiceRequestHistory().add( 0, new Integer(liteOrder.getOrderID()) );
		
		return liteOrder;
	}
	
	public static LiteWorkOrderBase createServiceRequest(StarsCreateServiceRequest createOrder, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany) throws WebClientException, CommandExecutionException
	{
		return createServiceRequest(createOrder, liteAcctInfo, energyCompany, true);
	}

}
