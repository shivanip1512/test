package com.cannontech.stars.web.action;

import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;
import com.cannontech.database.data.lite.stars.StarsLiteFactory;
import com.cannontech.database.data.stars.event.EventWorkOrder;
import com.cannontech.database.db.stars.integration.SAMToCRS_PTJ;
import com.cannontech.database.db.stars.report.WorkOrderBase;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.util.EventUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.util.WorkOrderManagerUtil;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsUpdateServiceRequestResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.user.YukonUserContext;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateServiceRequestAction implements ActionBase {
    private final StarsCustAccountInformationDao starsCustAccountInformationDao = 
        YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
    
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
            
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            if (user == null) {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_SESSION_INVALID, "Session invalidated, please login again") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            
        	LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );

        	StarsUpdateServiceRequest updateOrder = reqOper.getStarsUpdateServiceRequest();
        	
        	StarsWorkOrderBaseDao starsWorkOrderBaseDao =
        	    YukonSpringHook.getBean("starsWorkOrderBaseDao", StarsWorkOrderBaseDao.class);

        	LiteWorkOrderBase liteOrder = starsWorkOrderBaseDao.getById(updateOrder.getOrderID());

        	LiteStarsCustAccountInformation liteAcctInfo = 
        	    starsCustAccountInformationDao.getById(liteOrder.getAccountID(), liteStarsEC.getEnergyCompanyId());

        	if (updateOrder.getOrderNumber() != null &&
        		!updateOrder.getOrderNumber().equals( liteOrder.getOrderNumber() ) &&
        		WorkOrderBase.orderNumberExists( updateOrder.getOrderNumber(), liteStarsEC.getEnergyCompanyId() ))
        	{
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Order # already exists, please enter a different one") );
				return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
			com.cannontech.database.data.stars.report.WorkOrderBase order = (com.cannontech.database.data.stars.report.WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
			StarsFactory.setWorkOrderBase( order, updateOrder );
        	
        	order = Transaction.createTransaction( Transaction.UPDATE, order ).execute();
        	//compare with old liteOrder before updating the liteOrder too.
        	if( liteOrder.getCurrentStateID() != updateOrder.getCurrentState().getEntryID())
			{	//New event!
				Date eventTimestamp = updateOrder.getDateReported();
	            EventWorkOrder eventWorkOrder = (EventWorkOrder)EventUtils.logSTARSDatedEvent(user.getUserID(), EventUtils.EVENT_CATEGORY_WORKORDER, updateOrder.getCurrentState().getEntryID(), updateOrder.getOrderID(), eventTimestamp);
	           	order.getEventWorkOrders().add(0, eventWorkOrder);
//TODO if serviceCompany changes, change state?
	           	if (VersionTools.crsPtjIntegrationExists())
	           	{
	           		YukonListEntry listEntry = DaoFactory.getYukonListDao().getYukonListEntry(updateOrder.getCurrentState().getEntryID());
                    SAMToCRS_PTJ.handleCRSIntegration(listEntry.getYukonDefID(), order, liteAcctInfo, liteStarsEC, user.getUserID(), null);
	           	}
			}
        	StarsLiteFactory.setLiteWorkOrderBase( liteOrder, order );
        	
        	if (!updateOrder.hasAccountID()) {
        	    StarsUpdateServiceRequestResponse resp = new StarsUpdateServiceRequestResponse();
        	    StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest(liteOrder, liteStarsEC);
        	    resp.setStarsServiceRequest( starsOrder );
        	    respOper.setStarsUpdateServiceRequestResponse( resp );
        	    
        	    StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
        	    session.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	    parseResponse( starsOrder, accountInfo );        	    
        	}
            StarsSuccess success = new StarsSuccess();
            success.setDescription("Work order is updated successfully");
            respOper.setStarsSuccess( success );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	CTILogger.error( e.getMessage(), e );
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the service requests") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	CTILogger.error( e2.getMessage(), e2 );
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
		StarsUpdateServiceRequest updateOrder = new StarsUpdateServiceRequest();
		WorkOrderManagerUtil.setStarsServiceRequest( updateOrder, req, tz );
			
		StarsOperation operation = new StarsOperation();
		operation.setStarsUpdateServiceRequest( updateOrder );
		return operation;
	}
	
	private void parseResponse(StarsServiceRequest starsOrder, StarsCustAccountInformation starsAcctInfo) {
		if(starsAcctInfo != null){
			StarsServiceRequestHistory orders = starsAcctInfo.getStarsServiceRequestHistory();
			for (int i = 0; i < orders.getStarsServiceRequestCount(); i++) {
				if (orders.getStarsServiceRequest(i).getOrderID() == starsOrder.getOrderID()) {
					orders.setStarsServiceRequest(i, starsOrder);
					break;
				}
			}
		}
	}

}
