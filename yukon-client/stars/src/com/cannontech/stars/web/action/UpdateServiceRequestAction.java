package com.cannontech.stars.web.action;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

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
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.stars.xml.serialize.StarsServiceCompany;
import com.cannontech.stars.xml.serialize.StarsServiceRequest;
import com.cannontech.stars.xml.serialize.StarsServiceRequestHistory;
import com.cannontech.stars.xml.serialize.StarsSuccess;
import com.cannontech.stars.xml.serialize.StarsUpdateServiceRequest;
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
	
	private static final String TO_BE_DELETED = "TO_BE_DELETED";

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			if (user == null) return null;
			Hashtable selectionLists = (Hashtable) user.getAttribute( ServletUtils.ATT_CUSTOMER_SELECTION_LISTS );
			
			String[] changed = req.getParameterValues( "Changed" );
			String[] deleted = req.getParameterValues( "Deleted" );
			String[] orderIDs = req.getParameterValues( "OrderID" );
			String[] orderNos = req.getParameterValues( "OrderNo" );
			String[] servTypes = req.getParameterValues( "ServiceType" );
			String[] status = req.getParameterValues( "Status" );
			String[] companies = req.getParameterValues( "ServiceCompany" );
			String[] descriptions = req.getParameterValues( "Description" );
			
			StarsUpdateServiceRequest updateOrders = new StarsUpdateServiceRequest();
			for (int i = 0; i < orderIDs.length; i++) {
				if (Boolean.valueOf( deleted[i] ).booleanValue()) {
					StarsServiceRequest order = new StarsServiceRequest();
					order.setOrderID( Integer.parseInt(orderIDs[i]) );
					order.setOrderNumber( TO_BE_DELETED );
					updateOrders.addStarsServiceRequest( order );
				}
				else if (Boolean.valueOf( changed[i] ).booleanValue()) {
					StarsServiceRequest order = new StarsServiceRequest();
					order.setOrderID( Integer.parseInt(orderIDs[i]) );
					
					if (orderNos != null) order.setOrderNumber( orderNos[i] );
					if (descriptions != null) order.setDescription( descriptions[i].replaceAll("\r\n", "<br>") );
					
					if (servTypes != null) {
						ServiceType servType = (ServiceType) StarsFactory.newStarsCustListEntry(
								ServletUtils.getStarsCustListEntryByID(
									selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_TYPE, Integer.parseInt(servTypes[i])),
								ServiceType.class );
						order.setServiceType( servType );
					}
					
					if (status != null) {
						CurrentState state = (CurrentState) StarsFactory.newStarsCustListEntry(
								ServletUtils.getStarsCustListEntryByID(
									selectionLists, YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS, Integer.parseInt(status[i])),
								CurrentState.class );
						order.setCurrentState( state );
					}
					
					if (companies != null) {
						ServiceCompany company = new ServiceCompany();
						company.setEntryID( Integer.parseInt(companies[i]) );
						order.setServiceCompany( company );
					}
					
					updateOrders.addStarsServiceRequest( order );
				}
			}
			
			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateServiceRequest( updateOrders );
			
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
        	
        	LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( user.getEnergyCompanyID() );
        	
        	ArrayList orderHist = accountInfo.getServiceRequestHistory();
        	StarsUpdateServiceRequest updateOrders = reqOper.getStarsUpdateServiceRequest();
        	
        	for (int i = 0; i < updateOrders.getStarsServiceRequestCount(); i++) {
        		StarsServiceRequest newOrder = updateOrders.getStarsServiceRequest(i);
        		for (int j = 0; j < orderHist.size(); j++) {
        			int orderID = ((Integer) orderHist.get(j)).intValue();
        			if (orderID != newOrder.getOrderID()) continue;
        			
        			if (newOrder.getOrderNumber() != null && newOrder.getOrderNumber().equals( TO_BE_DELETED )) {
        				com.cannontech.database.data.stars.report.WorkOrderBase order =
        						new com.cannontech.database.data.stars.report.WorkOrderBase();
        				order.setOrderID( new Integer(newOrder.getOrderID()) );
        				Transaction.createTransaction( Transaction.DELETE, order ).execute();
        				
        				energyCompany.deleteWorkOrderBase( newOrder.getOrderID() );
        			}
        			else {
        				LiteWorkOrderBase liteOrder = energyCompany.getWorkOrderBase( orderID );
        				
        				if (newOrder.getOrderNumber() != null) {
        					if (!liteOrder.getOrderNumber().equals( newOrder.getOrderNumber() )
        						&& WorkOrderBase.orderNumberExists( newOrder.getOrderNumber(), energyCompany.getEnergyCompanyID() )) {
				            	respOper.setStarsFailure( StarsFactory.newStarsFailure(
				            			StarsConstants.FAILURE_CODE_OPERATION_FAILED, "Order # already exists, please enter a different one") );
				            	return SOAPUtil.buildSOAPMessage( respOper );
        					}
        					liteOrder.setOrderNumber( newOrder.getOrderNumber() );
        				}
        				
        				if (newOrder.getServiceType() != null)
        					liteOrder.setWorkTypeID( newOrder.getServiceType().getEntryID() );
        				if (newOrder.getCurrentState() != null)
        					liteOrder.setCurrentStateID( newOrder.getCurrentState().getEntryID() );
        				if (newOrder.getServiceCompany() != null)
        					liteOrder.setServiceCompanyID( newOrder.getServiceCompany().getEntryID() );
        				if (newOrder.getDescription() != null)
        					liteOrder.setDescription( newOrder.getDescription() );
        				
        				com.cannontech.database.db.stars.report.WorkOrderBase orderDB =
        						(com.cannontech.database.db.stars.report.WorkOrderBase) StarsLiteFactory.createDBPersistent( liteOrder );
						Transaction.createTransaction(Transaction.UPDATE, orderDB).execute();
			            
        				break;
        			}
        		}
        	}
        	
        	StarsSuccess success = new StarsSuccess();
        	success.setDescription( "Service request updated successfully" );
            
            respOper.setStarsSuccess( success );
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
			
			StarsSuccess success = operation.getStarsSuccess();
			if (success == null)
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation( reqMsg );
			StarsUpdateServiceRequest updateOrders = reqOper.getStarsUpdateServiceRequest();
			
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
			StarsCustAccountInformation accountInfo = (StarsCustAccountInformation)
					user.getAttribute( ServletUtils.TRANSIENT_ATT_LEADING + ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO );
			StarsServiceRequestHistory orderHist = accountInfo.getStarsServiceRequestHistory();
			
			for (int i = 0; i < updateOrders.getStarsServiceRequestCount(); i++) {
				StarsServiceRequest newOrder = updateOrders.getStarsServiceRequest(i);
				for (int j = 0; j < orderHist.getStarsServiceRequestCount(); j++) {
					StarsServiceRequest order = orderHist.getStarsServiceRequest(j);
					if (order.getOrderID() != newOrder.getOrderID()) continue;
					
					if (newOrder.getOrderNumber() != null && newOrder.getOrderNumber().equals( TO_BE_DELETED ))
						orderHist.removeStarsServiceRequest( j );
					else {
						if (newOrder.getOrderNumber() != null) order.setOrderNumber( newOrder.getOrderNumber() );
						if (newOrder.getServiceType() != null) order.setServiceType( newOrder.getServiceType() );
						if (newOrder.getCurrentState() != null) order.setCurrentState( newOrder.getCurrentState() );
						if (newOrder.getServiceCompany() != null) order.setServiceCompany( newOrder.getServiceCompany() );
						if (newOrder.getDescription() != null) order.setDescription( newOrder.getDescription() );
					}
					break;
				}
			}
			
            return 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
