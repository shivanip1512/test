package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.event.EventWorkOrder;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.integration.SAMToCRS_PTJ;
import com.cannontech.stars.database.db.report.WorkOrderBase;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsCreateServiceRequestResponse;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.user.YukonUserContext;

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
			YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(req);
			TimeZone tz = yukonUserContext.getTimeZone();
			
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
			if (createOrder.hasAccountID())	{ 
			    // Request from CreateOrder.jsp
			    StarsCustAccountInformationDao starsCustAccountInformationDao =
			        YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
			    liteAcctInfo = starsCustAccountInformationDao.getById(createOrder.getAccountID(),
			                                                          energyCompany.getEnergyCompanyId());
			}else {
				liteAcctInfo = (LiteStarsCustAccountInformation) session.getAttribute(ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);
			}
			
            LiteWorkOrderBase liteOrder = null;
            try {
            	liteOrder = createServiceRequest( createOrder, liteAcctInfo, energyCompany, user.getUserID());
                
                WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
                if (workOrderBean != null) {
                    workOrderBean.addWorkOrder(liteOrder);
                }
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
                WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
                /*
                 * No filters defined so we need to make sure we don't directly navigate to WorkOrder.jsp
                 */
                if (workOrderBean != null && workOrderBean.getFilters() == null) {
                    redirect = redirect.substring(0, redirect.indexOf("WorkOrder"));
                    redirect += "WorkOrder/WOFilter.jsp";
                }
                else {
                    
    				redirect += liteOrder.getOrderID();
                }
                session.setAttribute(ServletUtils.ATT_REDIRECT, redirect);
            } else {
                StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompany );
                StarsCreateServiceRequestResponse resp = new StarsCreateServiceRequestResponse();
                resp.setStarsServiceRequest( starsOrder );
                respOper.setStarsCreateServiceRequestResponse( resp );
                
                StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
                session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
                if (accountInfo != null && starsOrder != null) {
                    accountInfo.getStarsServiceRequestHistory().addStarsServiceRequest( 0, starsOrder);
                }
            }
            
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Work order is created successfully");            
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

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage reqMsg, SOAPMessage respMsg, HttpSession session) {
        try {
            StarsOperation operation = SOAPUtil.parseSOAPMsgForOperation( respMsg );

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, failure.getDescription() );
				return failure.getStatusCode();
			}
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success != null) {
			    session.setAttribute( ServletUtils.ATT_CONFIRM_MESSAGE, success.getDescription() );
			    return 0;			    
			}
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
		LiteStarsEnergyCompany energyCompany, int userID, boolean checkConstraint) throws WebClientException, CommandExecutionException
	{
		String orderNo = createOrder.getOrderNumber();
		
		if (checkConstraint) {
			if (orderNo != null) {
				if (orderNo.trim().length() == 0)
					throw new WebClientException( "Order # cannot be empty" );
				if (WorkOrderBase.orderNumberExists( orderNo, energyCompany.getEnergyCompanyId() ))
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
        
		com.cannontech.stars.database.data.report.WorkOrderBase workOrder = new com.cannontech.stars.database.data.report.WorkOrderBase();
        
		StarsFactory.setWorkOrderBase( workOrder, createOrder );
        
		if (createOrder.getCurrentState() == null) {
			workOrder.getWorkOrderBase().setCurrentStateID( new Integer(
					energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING).getEntryID()) );
		}
		if (!createOrder.hasAccountID()) {
			if (liteAcctInfo != null){
				workOrder.getWorkOrderBase().setAccountID( new Integer(liteAcctInfo.getAccountID())) ;
			}
		}
		
		workOrder.setEnergyCompanyID( energyCompany.getEnergyCompanyId() );
		workOrder = Transaction.createTransaction(Transaction.INSERT, workOrder).execute();
        
		//New event!
		Date eventTimestamp = createOrder.getDateReported();
        EventWorkOrder eventWorkOrder = (EventWorkOrder)EventUtils.logSTARSDatedEvent(userID, EventUtils.EVENT_CATEGORY_WORKORDER, workOrder.getWorkOrderBase().getCurrentStateID().intValue(), workOrder.getWorkOrderBase().getOrderID().intValue(), eventTimestamp);
       	workOrder.getEventWorkOrders().add(0, eventWorkOrder);

       	if (VersionTools.crsPtjIntegrationExists())
       	{
           	YukonListEntry listEntry = DaoFactory.getYukonListDao().getYukonListEntry(workOrder.getWorkOrderBase().getCurrentStateID().intValue());
            SAMToCRS_PTJ.handleCRSIntegration(listEntry.getYukonDefID(), workOrder, liteAcctInfo, energyCompany, userID, null);
       	}
       	
		
		LiteWorkOrderBase liteOrder = (LiteWorkOrderBase) StarsLiteFactory.createLite( workOrder );
		return liteOrder;
	}
	
	public static LiteWorkOrderBase createServiceRequest(StarsCreateServiceRequest createOrder, LiteStarsCustAccountInformation liteAcctInfo,
		LiteStarsEnergyCompany energyCompany, int userID) throws WebClientException, CommandExecutionException
	{
		return createServiceRequest(createOrder, liteAcctInfo, energyCompany, userID, true);
	}

}
