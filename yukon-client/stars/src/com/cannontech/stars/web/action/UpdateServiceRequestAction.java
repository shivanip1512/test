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
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.stars.web.servlet.WorkOrderManager;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateServiceRequest;
import com.cannontech.stars.xml.serialize.StarsUpdateServiceRequestResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateServiceRequestAction implements ActionBase {
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
            
        	LiteStarsCustAccountInformation liteAcctInfo = (LiteStarsCustAccountInformation) user.getAttribute( ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
        	StarsUpdateServiceRequest updateOrder = reqOper.getStarsUpdateServiceRequest();
        	LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( updateOrder.getOrderID(), true );
        	
        	if (updateOrder.getOrderNumber() != null &&
        		!updateOrder.getOrderNumber().equals( liteOrder.getOrderNumber() ) &&
        		WorkOrderBase.orderNumberExists( updateOrder.getOrderNumber(), energyCompany.getEnergyCompanyID() ))
        	{
				respOper.setStarsFailure( StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD, "Order # already exists, please enter a different one") );
				return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
			int statusPending = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING).getEntryID();
			int statusScheduled = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_SCHEDULED).getEntryID();
			int statusCompleted = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_COMPLETED).getEntryID();
			int statusCancelled = energyCompany.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_CANCELLED).getEntryID();
			
			if (liteOrder.getCurrentStateID() == statusCompleted ||
				liteOrder.getCurrentStateID() == statusCancelled ||
				liteOrder.getCurrentStateID() == statusScheduled && updateOrder.getCurrentState().getEntryID() == statusPending)
			{
				// If the current state is not "upgraded" (in the order of
				// pending -> scheduled -> completed or cancelled), then reset it.
				updateOrder.getCurrentState().setEntryID( liteOrder.getCurrentStateID() );
			}
        	
			WorkOrderBase order = (WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
			StarsFactory.setWorkOrderBase( order, updateOrder );
        	
        	order = (WorkOrderBase) Transaction.createTransaction( Transaction.UPDATE, order ).execute();
        	StarsLiteFactory.setLiteWorkOrderBase( liteOrder, order );
        	
        	if (updateOrder.hasAccountID()) {
				// Request from WorkOrder.jsp
				StarsCustAccountInformation starsAcctInfo = energyCompany.getStarsCustAccountInformation( liteOrder.getAccountID() );
				if (starsAcctInfo != null) {
					StarsServiceRequest starsOrder = StarsLiteFactory.createStarsServiceRequest( liteOrder, energyCompany );
					parseResponse( starsOrder, starsAcctInfo );
				}
        		
				respOper.setStarsSuccess( new StarsSuccess() );
				return SOAPUtil.buildSOAPMessage( respOper );
        	}
        	
        	StarsUpdateServiceRequestResponse resp = new StarsUpdateServiceRequestResponse();
        	resp.setStarsServiceRequest( StarsLiteFactory.createStarsServiceRequest(liteOrder, energyCompany) );
            
            respOper.setStarsUpdateServiceRequestResponse( resp );
            return SOAPUtil.buildSOAPMessage( respOper );
        }
        catch (Exception e) {
        	e.printStackTrace();
            
            try {
            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Cannot update the service requests") );
            	return SOAPUtil.buildSOAPMessage( respOper );
            }
            catch (Exception e2) {
            	e2.printStackTrace();
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
			
			if (operation.getStarsSuccess() != null) return 0;
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			
			StarsServiceRequest order = operation.getStarsUpdateServiceRequestResponse().getStarsServiceRequest();
			parseResponse( order, accountInfo );
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}
	
	public static StarsOperation getRequestOperation(HttpServletRequest req, TimeZone tz)
	throws WebClientException {
		StarsUpdateServiceRequest updateOrder = new StarsUpdateServiceRequest();
		WorkOrderManager.setStarsServiceRequest( updateOrder, req, tz );
			
		StarsOperation operation = new StarsOperation();
		operation.setStarsUpdateServiceRequest( updateOrder );
		return operation;
	}
	
	private void parseResponse(StarsServiceRequest starsOrder, StarsCustAccountInformation starsAcctInfo) {
		StarsServiceRequestHistory orders = starsAcctInfo.getStarsServiceRequestHistory();
		for (int i = 0; i < orders.getStarsServiceRequestCount(); i++) {
			if (orders.getStarsServiceRequest(i).getOrderID() == starsOrder.getOrderID()) {
				orders.setStarsServiceRequest(i, starsOrder);
				break;
			}
		}
	}

}
